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

import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';

import {AppRoutingModule} from './app-routing.module';
import {LogInComponent} from "./components/LogInComponent";
import {AppComponent} from "./components/AppComponent/app.component";
import {HomeComponent} from "./components/HomeComponent";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {FlexLayoutModule} from "@angular/flex-layout";
import {LayoutModule} from "@angular/cdk/layout";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {HttpClientModule} from "@angular/common/http";
import {LoggerModule, NgxLoggerLevel} from "ngx-logger";
import {MatDialogModule} from "@angular/material/dialog";
import {MatListModule} from "@angular/material/list";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatSelectModule} from "@angular/material/select";
import {MatRadioModule} from "@angular/material/radio";
import {MatRippleModule} from "@angular/material/core";
import {MatChipsModule} from "@angular/material/chips";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AdminComponent} from "./components/AdminComponent";
import {MatSidenavModule} from "@angular/material/sidenav";
import {ConfirmDialogComponent} from "./components/confirm-dialog";
import {MenuComponent} from "./components/menu/menu.component";
import {ServerSettingsComponent} from "./components/ServerSettingsComponent";


@NgModule({
  entryComponents:[
    ConfirmDialogComponent
  ],
  declarations: [
    AppComponent,
    LogInComponent,
    HomeComponent,
    AdminComponent,
    ServerSettingsComponent,
    ConfirmDialogComponent,
    MenuComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        FlexLayoutModule,
        LayoutModule,
        MatToolbarModule,
        MatButtonModule,
        MatIconModule,
        MatInputModule,
        MatCheckboxModule,
        MatFormFieldModule,
        MatSnackBarModule,
        MatListModule,
        MatDialogModule,
        MatCardModule,
        MatTableModule,
        MatSelectModule,
        MatRadioModule,
        MatRippleModule,
        MatChipsModule,
        HttpClientModule,
        LoggerModule.forRoot({
            // serverLoggingUrl: '/api/logs',
            level: NgxLoggerLevel.DEBUG, // TODO: Switch to WARN on production
            serverLogLevel: NgxLoggerLevel.ERROR,
            disableConsoleLogging: false, // TODO: enable in production
            enableSourceMaps: true // TODO: Disable in production
        }),
        BrowserAnimationsModule,
        ReactiveFormsModule,
        MatSidenavModule,


    ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
