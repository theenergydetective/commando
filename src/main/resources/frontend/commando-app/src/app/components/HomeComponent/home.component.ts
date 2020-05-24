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
import {FormBuilder, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {MatDialog} from "@angular/material/dialog";
import {MtuService} from "../../services/mtu.service";
import {MeasuringTransmittingUnit} from "../../models/measuring-transmitting-unit";
import {DailyEnergyData} from "../../models/daily-energy-data";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})

export class HomeComponent implements AfterContentInit {
  public mtuList:Array<MeasuringTransmittingUnit> = [];
  private static readonly MAX_COLUMN_COUNT:number = 5;
  public columnCount:Array<number> = Array(HomeComponent.MAX_COLUMN_COUNT +1).fill(0).map((x,i)=>i);
  public dataColumns:Array<Array<MeasuringTransmittingUnit>> = [[]];


  constructor(private authService: AuthService,
              private mtuService:MtuService,
              private formBuilder: FormBuilder,
              private router: Router,
              public dialog: MatDialog,
              private logger: NGXLogger) {

  }


  ngAfterContentInit(): void {

    this.authService.verifyAccessToken().then(r=>{
      if (r){
        this.mtuService.findAllMTU(true).then((r:Array<MeasuringTransmittingUnit>)=>{
          this.mtuList = r;

          let colSize = 10;
          if (this.mtuList.length > (HomeComponent.MAX_COLUMN_COUNT * colSize)){
            colSize = Math.ceil(this.mtuList.length / HomeComponent.MAX_COLUMN_COUNT);
          }
          this.dataColumns = [[]];
          for (let c=0; c < HomeComponent.MAX_COLUMN_COUNT; c++) {
            this.dataColumns.push(this.createArrayChunk(this.mtuList, c, colSize));
          }
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

}
