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

import {Component, Input} from '@angular/core';
import {AuthService} from '../../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {DailyEnergyService} from "../../../services/daily-energy.service";
import {DailyEnergyData} from "../../../models/daily-energy-data";

@Component({
  selector: 'app-device-edit-field',
  templateUrl: './device-edit-field.component.html',
  styleUrls: ['./device-edit-field.component.scss']
})

export class DeviceEditFieldComponent {


  private _dailyEnergyData;
  public energyDate:string;
  public energyValue:string = '0.000';

  @Input()
  set dailyEnergyData(dailyEnergyData:DailyEnergyData){
    this._dailyEnergyData = dailyEnergyData;
    this.energyDate = dailyEnergyData.formattedDate;
    this.energyValue = (dailyEnergyData.energyValue / 1000.0).toFixed(3);
  }

  constructor(private authService: AuthService,
              private dailyEnergyService:DailyEnergyService,
              private logger: NGXLogger) {
  }

  onChange() {
    if (this._dailyEnergyData != null) {
      this._dailyEnergyData.energyValue = parseFloat(this.energyValue) * 1000.0;
      this.logger.debug("[onChange] Value: " + JSON.stringify(this._dailyEnergyData));
      this.dailyEnergyService.update(this._dailyEnergyData);
    }

  }
}
