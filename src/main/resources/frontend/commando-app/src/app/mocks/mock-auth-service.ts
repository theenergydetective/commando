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

import {AdminRequest} from "../models/admin-request";
import {HttpHeaders} from "@angular/common/http";
import {ActivationDetails} from "../models/activation-details";

/***
 * Simple mock for the auth service used for testing.
 */
export class MockAuthService {
  login() {
  }

  getUser() {
  }

  logOut() {
  }

  public getAuthorizedHttpOptions() {
    return {
      headers: new HttpHeaders({Authorization: 'Bearer 12345', FWTOKEN: '12345', 'Content-Type': 'application/json',})
    };
  }

  getAdminRequest(){
    return new Promise(r=>{r(new AdminRequest())})
  }

  getActivationDetails(){
    return new Promise(r=>{r(new ActivationDetails())})
  }

  getTimeZones(){
    return new Promise(r=>{r([])});
  }

  verifyAccessToken(){
    return new Promise(r=>{r(true)});
  }
}
