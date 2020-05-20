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

import {AuthService} from './auth.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {UserSessionState} from '../models/user-session';
import {LoggerConfig, NGXLogger, NGXLoggerHttpService, NgxLoggerLevel, NGXMapperService} from 'ngx-logger';
import {NGXLoggerHttpServiceMock, NGXMapperServiceMock} from 'ngx-logger/testing';
import {DatePipe} from "@angular/common";
import {AdminRequest} from "../models/admin-request";

describe('AuthService', () => {
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
    const service: AuthService = TestBed.get(AuthService);
    expect(service).toBeTruthy();
  });


  it('can log on', () => {
    const validOAuthResponse = {
      access_token: 'fe9b37d8-0dc0-4214-ab88-ac4782cf2769',
      token_type: 'bearer',
      refresh_token: '07749831-aa53-4f34-84d2-aa45b25f73c9',
      expires_in: 43199,
      scope: 'api'
    };
    const service: AuthService = TestBed.get(AuthService);
    service.login('testUser', 'testPassword');
    const req = httpTestingController.expectOne('/oauth/token');
    expect(req.request.method).toEqual('POST');
    req.flush(validOAuthResponse);
    httpTestingController.verify();
    expect(service.userSession.state).toBe(UserSessionState.AUTHENTICATED);
    expect(service.userSession.isAuthenticated()).toBe(true);
  });

  it('can handle invalid credentials', () => {
    const validOAuthResponse = {error: 'invalid_grant', error_description: 'Bad credentials'};
    const service: AuthService = TestBed.get(AuthService);
    service.login('testUser', 'testPassword');
    const req = httpTestingController.expectOne('/oauth/token');
    expect(req.request.method).toEqual('POST');
    req.flush(validOAuthResponse);
    httpTestingController.verify();
    expect(service.userSession.state).toBe(UserSessionState.AUTH_FAILED);
    expect(service.userSession.isAuthenticated()).toBe(false);
  });

  it('can refresh token', () => {
    const validOAuthResponse = {
      access_token: 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
      refresh_token: 'eeeeeeee-dddd-cccc-bbbb-aaaaaaaaaaaa',
      expires_in: 43199,
      scope: 'api'
    };

    const service: AuthService = TestBed.get(AuthService);

    service.refreshAccessToken();
    const req = httpTestingController.expectOne('/oauth/token');
    expect(req.request.method).toEqual('POST');
    req.flush(validOAuthResponse);
    httpTestingController.verify();

    expect(service.userSession.refreshToken).toBe('eeeeeeee-dddd-cccc-bbbb-aaaaaaaaaaaa');
    expect(service.userSession.accessToken).toBe('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
  });


  it('can log out', () => {
    const service: AuthService = TestBed.get(AuthService);
    service.userSession.state = UserSessionState.AUTHENTICATED;
    service.logOut();
    console.error(JSON.stringify(service.userSession.state, null, 2));
    expect(service.userSession.state).toBe(0); // NO_AUTH. Workaround for a known Jasmine typescript bug.
  });

  it('can get timezones', () => {
    const tz:Array<string> = ['1','2','3'];
    const service: AuthService = TestBed.get(AuthService);
    service.getTimeZones();
    const req = httpTestingController.expectOne('/api/admin/tz');
    expect(req.request.method).toEqual('GET');
    req.flush(tz);
    httpTestingController.verify();
  });


  it('can get admin request', () => {
    const ar:AdminRequest = new AdminRequest();
    ar.adminSetup = true;

    const service: AuthService = TestBed.get(AuthService);
    service.getAdminRequest()
    const req = httpTestingController.expectOne('/api/admin');
    expect(req.request.method).toEqual('GET');
    req.flush(ar);
    httpTestingController.verify();
  });
});


