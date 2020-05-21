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

import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserSession, UserSessionState} from '../../models/user-session';
import {Router} from '@angular/router';
import {AdminRequest} from "../../models/admin-request";

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.scss']
})

export class LogInComponent implements OnInit, AfterViewInit, OnDestroy  {
  form: FormGroup;
  hideError = true;

  constructor(private authService: AuthService,
              private formBuilder: FormBuilder,
              private router: Router,
              private logger: NGXLogger) {

  }




  ngOnInit() {
    this.authService.logOut();
    this.form = this.formBuilder.group({
      username: [null, [Validators.required]],
      password: [null, Validators.required],
    });
  }


  ngAfterViewInit() {
    this.form.get('username').setValue('');
    this.form.get('password').setValue('');

    this.authService.getAdminRequest().then((ar:AdminRequest)=>{
      if (ar.adminSetup){
        //Force Redirect to admin page
        this.router.navigate(['/admin']);
      }
    });
  }

  ngOnDestroy() {
  }

   submit() {
    const username = this.form.get('username').value;
    const password = this.form.get('password').value;
    this.hideError = true;
    this.logger.info('[submit] Performing login: username: ' + username);
    this.authService.login(username, password)
      .then((userSession: UserSession) => {
        this.logger.debug('[submit] Login Result: ' + JSON.stringify(userSession, null, 2));
        switch (userSession.state) {
          case UserSessionState.AUTHENTICATED:{
            this.hideError = true;
            this.router.navigate(['/']);
            break;
          }
          case UserSessionState.AUTH_FAILED:{
            // Trigger invalid username/password error
            this.logger.warn('[submit] Invalid credentials');
            this.hideError = false;
            break;
          }
        }
      });
  }


  hideErrorBox() {
    this.hideError = true;
  }

}
