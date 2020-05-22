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

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {NGXLogger} from 'ngx-logger';
import {AuthService} from "./auth.service";
import {FormatHelper} from "../helpers/format-helper";
import {DailyEnergyData} from "../models/daily-energy-data";

@Injectable({
  providedIn: 'root'
})
export class DailyEnergyService {
  constructor(private authService: AuthService, private http: HttpClient, private logger: NGXLogger) {
  }


  /**
   * Returns a list of all MTUs in the system ordered by name
   */
  public findByIdDate(mtuId:string, startDate:Date, endDate:Date) {
    let url = '/api/dailyEnergyData/' + mtuId;
    url +='?startDate=' + FormatHelper.formatDateQueryParam(startDate);
    url +='&endDate=' + FormatHelper.formatDateQueryParam(endDate);

    this.logger.debug('[findByIdDate] Looking up daily data: ' + url);
    return new Promise(result => {
      const response = this.http.get(url, this.authService.getAuthorizedHttpOptions());
      response.subscribe((data: Array<DailyEnergyData>) => {
          result(data);
        },
        error => {
          this.logger.warn('[findByIdDate] Get Error: ' + JSON.stringify(error));
          result([]);
        });
    });
  }


  /**
   * Updates the specific daily energy record (just the watts)
   * @param dailyEnergyData
   */
  public update(dailyEnergyData:DailyEnergyData) {
    this.logger.debug('[update] Updating:'  + JSON.stringify(dailyEnergyData));
    return new Promise(result => {
      const response = this.http.post('/api/dailyEnergyData', JSON.stringify(dailyEnergyData), this.authService.getAuthorizedHttpOptions());
      response.subscribe((data: DailyEnergyData) => {
          result(data);
        },
        error => {
          this.logger.warn('[update] Get Error: ' + JSON.stringify(error));
          result(new DailyEnergyData());
        });
    });
  }
}



