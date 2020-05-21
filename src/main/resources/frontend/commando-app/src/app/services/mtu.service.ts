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

import {Injectable, OnDestroy} from '@angular/core';
import {UserSession, UserSessionState} from '../models/user-session';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {NGXLogger} from 'ngx-logger';
import {OAuthResponse} from '../models/oauth-response';
import {AdminRequest} from "../models/admin-request";
import {AuthService} from "./auth.service";
import {MeasuringTransmittingUnit} from "../models/measuring-transmitting-unit";

@Injectable({
  providedIn: 'root'
})
export class MtuService {
  constructor(private authService: AuthService, private http: HttpClient, private logger: NGXLogger) {
  }

  /**
   * Returns a list of all MTUs in the system ordered by name
   */
  public findAllMTU() {
    this.logger.debug('[findAllMTU] Looking up mtu list');
    return new Promise(mtuResult => {
      const response = this.http.get('/api/mtu', this.authService.getAuthorizedHttpOptions());

      response.subscribe((data: Array<MeasuringTransmittingUnit>) => {
          mtuResult(data);
        },
        error => {
          this.logger.warn('[findAllMTU] Get Error: ' + JSON.stringify(error));
          mtuResult([]);
        });
    });
  }

}



