/*
 * Copyright (c) 2020. Energy, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

import {AfterContentInit, Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from "@angular/material/dialog";
import {MtuService} from "../../services/mtu.service";
import {MeasuringTransmittingUnit} from "../../models/measuring-transmitting-unit";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DailyEnergyService} from "../../services/daily-energy.service";
import {DailyEnergyData} from "../../models/daily-energy-data";

@Component({
  selector: 'app-device-edit',
  templateUrl: './device-edit.component.html',
  styleUrls: ['./device-edit.component.scss']
})

export class DeviceEditComponent implements OnInit, AfterContentInit {
  form: FormGroup;

  public deviceId:string = '';
  public timezones: Array<string>=[];
  public mtu:MeasuringTransmittingUnit = new MeasuringTransmittingUnit();
  public dailyEnergyData:Array<DailyEnergyData> = [];
  public months:Array<string>=['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  public month: string = this.months[new Date().getMonth()];
  public year: string = new Date().getFullYear().toString();


  public years:Array<string>=[];




  constructor(private authService: AuthService,
              private mtuService:MtuService,
              private dailyEnergyService:DailyEnergyService,
              private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              public dialog: MatDialog,
              private _snackBar: MatSnackBar,
              private logger: NGXLogger) {

    let y = new Date().getFullYear();
    for (let i=0; i < 10; i++){
      this.years.push(y.toString());
      y--;
    }

  }


  ngOnInit() {
    this.form = this.formBuilder.group({
      name: [null, [Validators.required]],
      rate: [null, Validators.required],
      timezone: [null, Validators.required],
    });
  }


  ngAfterContentInit(): void {
    this.deviceId = this.route.snapshot.paramMap.get('deviceId');

    this.setMTU(new MeasuringTransmittingUnit());


    this.authService.verifyAccessToken().then(r=>{
      if (r){
        this.authService.getTimeZones().then((tz:Array<string>)=>{
          this.timezones = tz;

          this.mtuService.findOne(this.deviceId).then((mtu:MeasuringTransmittingUnit)=>{
            this.setMTU(mtu);
          });

          this.searchByDateRange(this.months[new Date().getMonth()],new Date().getFullYear().toString());


        })

      } else {
        this.authService.logOut();
        this.router.navigate(['/login']);
      }
    });



  }


  setMTU(mtu:MeasuringTransmittingUnit){
    this.mtu =mtu;
    this.form.get('name').setValue(mtu.name);
    this.form.get('rate').setValue(mtu.rate);
    this.form.get('timezone').setValue(mtu.timezone);
  }

  onClose() {
    this.router.navigate(['/serverSettings']);
  }

  submit() {
     this.mtu.name = this.form.get('name').value;
     this.mtu.rate = this.form.get('rate').value;
     this.mtu.timezone = this.form.get('timezone').value;
     this.mtuService.updateSettings(this.mtu).then((mtu:MeasuringTransmittingUnit)=>{
       this.setMTU(mtu);
       this._snackBar.open('Device settings updated', 'Close', {duration: 3000})
         .onAction().subscribe(() => {
         this._snackBar.dismiss();
       });
     });
  }


  searchByDateRange(m:string,y:string){
    this.logger.debug('[searchByDateRange] Month:' + m + ' Year: ' + y);
    let month:number = 0;
    for (let i=0; i < this.months.length; i++){
      if (m == this.months[i]){
        month = i;
        break;
      }
    }

    let startDate:Date = new Date(parseInt(y), month, 1, 0,0,0,0);
    let endDate:Date = new Date(parseInt(y), month+1, 1, 0,0,0,0);
    this.dailyEnergyService.findByIdDate(this.deviceId, startDate, endDate)
      .then((r:Array<DailyEnergyData>)=>{
        this.dailyEnergyData = r;
      });

  }


  onNewMonth() {
    this.logger.debug("[onNewMonth] " + this.month + ' selected');
    this.searchByDateRange(this.month, this.year);

  }

  onNewYear() {
    this.logger.debug("[onNewYear] " + this.year + ' selected');
    this.searchByDateRange(this.month, this.year);
  }
}
