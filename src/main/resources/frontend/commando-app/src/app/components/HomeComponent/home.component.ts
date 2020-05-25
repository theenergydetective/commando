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

import {AfterContentInit, Component} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';
import {MatDialog} from "@angular/material/dialog";
import {MtuService} from "../../services/mtu.service";
import {MeasuringTransmittingUnit} from "../../models/measuring-transmitting-unit";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})

export class HomeComponent implements AfterContentInit {

  private static readonly MAX_COLUMN_COUNT:number = 5;

  mtuList:Array<MeasuringTransmittingUnit> = [];
  columnCount:Array<number> = Array(HomeComponent.MAX_COLUMN_COUNT +1).fill(0).map((x,i)=>i);
  dataColumns:Array<Array<MeasuringTransmittingUnit>> = [[]];
  loaded: boolean = false;
  billingCycleStart: number = 1;
  exportType: string = 'DAY';
  startYear:number = new Date().getFullYear();
  valid: boolean = false;


  constructor(private authService: AuthService,
              private mtuService:MtuService,
              private formBuilder: FormBuilder,
              private router: Router,
              public dialog: MatDialog,
              private logger: NGXLogger) {

  }


  ngAfterContentInit(): void {

    this.loaded = false;
    this.authService.verifyAccessToken().then(r=>{
      if (r){
        this.mtuService.findAllMTU(true).then((r:Array<MeasuringTransmittingUnit>)=>{
          this.mtuList = r;
          this.startYear = this.calcStartYear(this.mtuList);

          //this.onSelectAll();
          this.onClear();

          let colSize = 10;
          let colCount = HomeComponent.MAX_COLUMN_COUNT;

          if (this.mtuList.length > (HomeComponent.MAX_COLUMN_COUNT * colSize)){
            //See if we need to adjust the column length
            colSize = Math.ceil(this.mtuList.length / HomeComponent.MAX_COLUMN_COUNT);
          } else {
            //See if we need to reduce columns
            let columnsNeeded = Math.ceil(this.mtuList.length / colSize);
            if (columnsNeeded < colCount) {
              colCount = columnsNeeded;

            }
          }

          this.columnCount = Array(colCount +1).fill(0).map((x,i)=>i);
          this.dataColumns = [[]];
          for (let c=0; c < colCount; c++) {
            this.dataColumns.push(this.createArrayChunk(this.mtuList, c, colSize));
          }

          this.loaded = true;
        });
      } else {
        this.authService.logOut();
        this.router.navigate(['/login']);
      }
    });
  }


  createArrayChunk(array:Array<MeasuringTransmittingUnit>, column:number, maxSize:number){
    let offset:number = column * maxSize;
    if (offset >= array.length) return []; //No more elements

    let endIndex = offset + maxSize;
    if (endIndex > array.length) endIndex = array.length;

    console.log('CREATING ' + column + ' ms: ' + maxSize + ' offset:' + offset + ' end:' + endIndex);

    let result:Array<MeasuringTransmittingUnit> = [];

    for (let i=offset; i < endIndex; i++){
      result.push(array[i]);
    }

    return result;


  }

  onClear() {
    for (let i=0; i < this.mtuList.length; i++) this.mtuList[i].selected = false;
    this.validateForm();
  }

  onSelectAll(){
    for (let i=0; i < this.mtuList.length; i++) this.mtuList[i].selected = true;
    this.validateForm();
  }

  onBillingCycleChange() {
    if (this.billingCycleStart < 1) this.billingCycleStart = 1;
    if (this.billingCycleStart > 31) this.billingCycleStart = 31;
  }

  calcStartYear(mtuList:Array<MeasuringTransmittingUnit>) {
    let oldestDate = Math.floor(new Date().getTime()/1000);

     for (let m=0; m < mtuList.length; m++){
       let mtu = mtuList[m];
       if (oldestDate > mtu.created) oldestDate = mtu.created;
     }
    return new Date(oldestDate * 1000).getFullYear();
  }

  validateForm() {
    this.valid = false;
    for (let m=0; m < this.mtuList.length; m++){
      let mtu = this.mtuList[m];
      if (mtu.selected){
        this.valid = true;
      }
    }
  }

  onExport() {

  }


}
