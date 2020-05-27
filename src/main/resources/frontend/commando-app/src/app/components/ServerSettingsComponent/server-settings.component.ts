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
import {Router} from '@angular/router';
import {MatDialog} from "@angular/material/dialog";
import {MtuService} from "../../services/mtu.service";
import {MeasuringTransmittingUnit} from "../../models/measuring-transmitting-unit";
import {ActivationDetails} from "../../models/activation-details";
import {ConfirmDialogComponent} from "../confirm-dialog";
import {ImportDevicesDialogComponent} from "../import-devices-dialog";
import {ImportDataDialogComponent} from "../import-data-dialog";

@Component({
  selector: 'app-server-settings',
  templateUrl: './server-settings.component.html',
  styleUrls: ['./server-settings.component.scss']
})

export class ServerSettingsComponent implements AfterContentInit {
  public mtuList:Array<MeasuringTransmittingUnit> = [];
  public activationDetails: ActivationDetails = new ActivationDetails();


  constructor(private authService: AuthService,
              private mtuService:MtuService,
              private router: Router,
              public dialog: MatDialog,
              private logger: NGXLogger) {

  }


  ngAfterContentInit(): void {

    this.authService.verifyAccessToken().then(r=>{
      if (r){
        this.authService.getActivationDetails()
          .then((ad:ActivationDetails)=>{
            this.activationDetails = ad;
          });
        this.mtuService.findAllMTU(false).then((r:Array<MeasuringTransmittingUnit>)=>{
          this.mtuList = r;
        });
      } else {
        this.authService.logOut();
        this.router.navigate(['/login']);
      }
    });

  }

  copyUrl() {
    this.copyToClip('http://' + this.activationDetails.domain + '/api/activate');
  }

  copyKey() {
    this.copyToClip(this.activationDetails.activationKey);
  }


  copyToClip(str:string) {
    function listener(e) {
      e.clipboardData.setData("text/html", str);
      e.clipboardData.setData("text/plain", str);
      e.preventDefault();
    }
    document.addEventListener("copy", listener);
    document.execCommand("copy");
    document.removeEventListener("copy", listener);
  };

  formatLastPost(lastPost: number) {
    let date:Date = new Date();
    if (lastPost == null || lastPost == 0) return 'Never Posted';
    date.setTime(lastPost * 1000);
    if (date.getFullYear() < 2000) return 'Never Posted';
    return date.toLocaleString();
  }

  onDeviceEdit(device: MeasuringTransmittingUnit) {
    this.router.navigate(['/edit/' + device.id]);
  }

  importDevices() {
    const dialogRef = this.dialog.open(ImportDevicesDialogComponent, {});

    dialogRef.afterClosed().subscribe((result) => {
      if (result != null && result) {
        this.ngAfterContentInit();
      }
    });
  }

  importData() {

    const dialogRef = this.dialog.open(ImportDataDialogComponent, {});

    dialogRef.afterClosed().subscribe((result) => {
      if (result != null && result) {
        this.ngAfterContentInit();
      }
    });
  }
}
