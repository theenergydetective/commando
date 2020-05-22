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

import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {UserSessionState} from '../models/user-session';
import {LoggerConfig, NGXLogger, NGXLoggerHttpService, NgxLoggerLevel, NGXMapperService} from 'ngx-logger';
import {NGXLoggerHttpServiceMock, NGXMapperServiceMock} from 'ngx-logger/testing';
import {DatePipe} from "@angular/common";
import {AdminRequest} from "../models/admin-request";
import {MtuService} from "./mtu.service";
import {MeasuringTransmittingUnit} from "../models/measuring-transmitting-unit";

describe('MtuService', () => {
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;


  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        // LoggerTestingModule
      ],
      providers: [
        NGXLogger,
        DatePipe,
        {provide: NGXLoggerHttpService, useClass: NGXLoggerHttpServiceMock},
        {provide: NGXMapperService, useClass: NGXMapperServiceMock},
        {provide: LoggerConfig, useValue: {level: NgxLoggerLevel.TRACE}}
      ]
    });

    // Inject the http service and test controller for each test

    httpClient = TestBed.get(HttpClient);
    httpTestingController = TestBed.get(HttpTestingController);

    // localStorage.clear();
  });

  it('should be created', () => {
    const service: MtuService = TestBed.get(MtuService);
    expect(service).toBeTruthy();
  });


  it('can find mtus', () => {
    let mtuArray:Array<MeasuringTransmittingUnit> = [];
    for (let i=0; i < 10; i++){
      let mtu:MeasuringTransmittingUnit = new MeasuringTransmittingUnit();
      mtu.id ='TEST' + i;
      mtu.name = mtu.id;
      mtuArray.push(mtu);
    }

    const service: MtuService = TestBed.get(MtuService);
    service.findAllMTU();
    const req = httpTestingController.expectOne('/api/mtu');
    expect(req.request.method).toEqual('GET');
    req.flush(mtuArray);
    httpTestingController.verify();
  });

  it('can find mtu', () => {
    let mtu:MeasuringTransmittingUnit =new MeasuringTransmittingUnit();
    mtu.id = 'TEST';
    const service: MtuService = TestBed.get(MtuService);
    service.findOne('TEST');
    const req = httpTestingController.expectOne('/api/mtu/TEST');
    expect(req.request.method).toEqual('GET');
    req.flush(mtu);
    httpTestingController.verify();
  });


  it('can post mtu', () => {
    let mtu:MeasuringTransmittingUnit =new MeasuringTransmittingUnit();
    mtu.id = 'TEST';
    const service: MtuService = TestBed.get(MtuService);
    service.updateSettings(mtu);
    const req = httpTestingController.expectOne('/api/mtu');
    expect(req.request.method).toEqual('POST');
    req.flush(mtu);
    httpTestingController.verify();
  });


});


