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
import {LoggerConfig, NGXLogger, NGXLoggerHttpService, NgxLoggerLevel, NGXMapperService} from 'ngx-logger';
import {NGXLoggerHttpServiceMock, NGXMapperServiceMock} from 'ngx-logger/testing';
import {DatePipe} from "@angular/common";
import {DailyEnergyService} from "./daily-energy.service";
import {DailyEnergyData} from "../models/daily-energy-data";

describe('DailyEnergyService', () => {
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
    const service: DailyEnergyService = TestBed.get(DailyEnergyService);
    expect(service).toBeTruthy();
  });


  it('can find dailyenergydata by range', () => {
    let ded:Array<DailyEnergyData> = [];
    const service: DailyEnergyService = TestBed.get(DailyEnergyService);
    let startDate:Date = new Date(2020, 1, 1);
    let endDate:Date = new Date(2020, 1, 28);
    service.findByIdDate('TEST', startDate, endDate);
    const req = httpTestingController.expectOne('/api/dailyEnergyData/TEST?startDate=2020-02-01&endDate=2020-02-28');
    expect(req.request.method).toEqual('GET');
    req.flush(ded);
    httpTestingController.verify();
  });

  it('can post dailyenergydata', () => {
    const service: DailyEnergyService = TestBed.get(DailyEnergyService);
    service.update(new DailyEnergyData());
    const req = httpTestingController.expectOne('/api/dailyEnergyData');
    expect(req.request.method).toEqual('POST');
    req.flush(new DailyEnergyData());
    httpTestingController.verify();
  });

});


