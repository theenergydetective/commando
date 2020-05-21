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

import {Component} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {ConfirmDialogComponent} from "../confirm-dialog";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})

export class HomeComponent  {
  form: FormGroup;
  hideError = true;

  constructor(private authService: AuthService,
              private formBuilder: FormBuilder,
              private router: Router,
              public dialog: MatDialog,
              private logger: NGXLogger) {

  }


  onLogout() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Logout',
        body: 'Are you sure you want to log out?'
      }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result != null && result) {
        this.authService.logOut();
        this.router.navigate(['/login']);
      }
    });

  }

  onEditClick() {
    this.router.navigate(['/edit']);
  }
}
