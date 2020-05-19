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

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';

import {AuthService} from '../services/auth.service';
import {NGXLogger} from 'ngx-logger';


@Injectable({
  providedIn: 'root',
})
export class UserManagementAuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
    private logger: NGXLogger) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    const url: string = state.url;
    return this.checkLogin(url);
  }

  checkLogin(url: string): boolean {
    this.logger.trace('[checkLogin} Checking: ' + url);

    if (this.authService.userSession.isAuthenticated()) {
      this.logger.trace('[checkLogin} User Session is authenticated');
      return true;
    }

    this.logger.trace('[checkLogin} User Session is NOT authenticated');
    // Store the attempted URL for redirecting
    this.authService.redirectUrl = url;

    // Navigate to the login page with extras
    this.logger.trace('[checkLogin} Redirecting to login');
    this.router.navigate(['/login']);
    return false;

  }
}
