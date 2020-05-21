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
import {Component, Input} from '@angular/core';

import {DatePipe} from "@angular/common";
import {MatDialogModule} from "@angular/material/dialog";
import {MockAuthService} from "../../mocks/mock-auth-service";
import {MtuService} from "../../services/mtu.service";
import {MockMtuService} from "../../mocks/mock-mtu-service";
import {MenuComponent} from "../menu/menu.component";
import {ServerSettingsComponent} from "./server-settings.component";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";


describe('ServerSettingsComponent', () => {

  @Component({
    selector: 'mat-icon',
    template: '<span></span>'
  })

  class MockMatIconComponent {
    @Input() svgIcon: any;
    @Input() fontSet: any;
    @Input() fontIcon: any;
  }

  let component: ServerSettingsComponent;
  let fixture: ComponentFixture<ServerSettingsComponent>;


  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServerSettingsComponent,MenuComponent],
      imports: [
        // LoggerTestingModule
        HttpClientModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatDialogModule,
        MatInputModule,
        MatToolbarModule,
        MatListModule,
        MatSidenavModule,
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
        {provide: MtuService, useClass: MockMtuService},
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


    fixture = TestBed.createComponent(ServerSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // Inject the mocks for testing
    // httpClient = TestBed.get(HttpClient);
    // httpTestingController = TestBed.get(HttpTestingController);

  });


  it('should compile', () => {
    expect(component).toBeTruthy();
  });

  it('can show last post', () => {
    let lastPostBad = component.formatLastPost(0);
    expect('Never Posted' == lastPostBad).toBeTruthy();

    let lastPostGood = component.formatLastPost(new Date().getTime()/1000);
    expect('Never Posted' != lastPostGood).toBeTruthy();
  });



});
