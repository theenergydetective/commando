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

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {LoggerConfig, NGXLogger, NGXLoggerHttpService, NgxLoggerLevel, NGXMapperService} from 'ngx-logger';
import {NGXLoggerHttpServiceMock, NGXMapperServiceMock} from 'ngx-logger/testing';
import {AuthService} from '../../services/auth.service';

import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {MatCardModule} from '@angular/material/card';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {LayoutModule} from '@angular/cdk/layout';
import {FlexLayoutModule} from '@angular/flex-layout';
import {HttpClientModule} from '@angular/common/http';
import {Component} from '@angular/core';
import {LogInComponent} from "./log-in.component";
import {DatePipe} from "@angular/common";
import {MockAuthService} from "../../mocks/mock-auth-service";
import {MockMatIconComponent} from "../../mocks/mock-mat-icon-component";


describe('LogInComponent', () => {

  let component: LogInComponent;
  let fixture: ComponentFixture<LogInComponent>;


  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LogInComponent],
      imports: [
        // LoggerTestingModule
        HttpClientModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        LayoutModule,
      ],
      providers: [
        DatePipe,
        NGXLogger,
        FormBuilder,
        {provide: AuthService, useClass: MockAuthService},
        {provide: NGXLoggerHttpService, useClass: NGXLoggerHttpServiceMock},
        {provide: NGXMapperService, useClass: NGXMapperServiceMock},
        {provide: LoggerConfig, useValue: {level: NgxLoggerLevel.TRACE}}
      ]
    })
      .overrideModule(MatIconModule, {
        remove: {
          declarations: [MatIcon],
          exports: [MatIcon]
        },
        add: {
          declarations: [MockMatIconComponent],
          exports: [MockMatIconComponent]
        }
      });


    fixture = TestBed.createComponent(LogInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    // Inject the mocks for testing
    // httpClient = TestBed.get(HttpClient);
    // httpTestingController = TestBed.get(HttpTestingController);

  });


  it('should compile', () => {
    expect(component).toBeTruthy();
  });

});
