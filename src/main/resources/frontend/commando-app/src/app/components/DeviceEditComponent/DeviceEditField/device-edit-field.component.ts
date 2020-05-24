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

  @Input()
  public dailyEnergyData:DailyEnergyData = new DailyEnergyData();


  constructor(private authService: AuthService,
              private dailyEnergyService:DailyEnergyService,
              private logger: NGXLogger) {
  }

  onChange() {
    if (this.dailyEnergyData != null) {
      this.logger.debug("[onChange] Value: " + JSON.stringify(this.dailyEnergyData));
      this.dailyEnergyService.update(this.dailyEnergyData);
    }

  }
}
