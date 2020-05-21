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

import {Injectable, OnDestroy} from '@angular/core';
import {UserSession, UserSessionState} from '../models/user-session';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {NGXLogger} from 'ngx-logger';
import {OAuthResponse} from '../models/oauth-response';
import {AdminRequest} from "../models/admin-request";
import {ActivationDetails} from "../models/activation-details";

@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnDestroy {

  private readonly LOCAL_STORAGE_TOKEN_SESSION: string = 'commando.usersession.1';

  private readonly FORM_HTTP_OPTIONS = {
    headers: new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: 'Basic d2ViOmNvbW1hbmRvQVBJ'
    })
  };

  private readonly JSON_HTTP_OPTIONS = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic d2ViOmNvbW1hbmRvQVBJ'
    })
  };

  public userSession: UserSession = new UserSession();
  public tzList: Array<string> = [];
  public redirectUrl: string;
  private intervalTimer: number;

  constructor(private http: HttpClient, private logger: NGXLogger) {
    // Load the User Session from the store
    const json = localStorage.getItem(this.LOCAL_STORAGE_TOKEN_SESSION);
    if (json != null) {
      const savedSession = JSON.parse(json);
      this.userSession.accessToken = savedSession.accessToken;
      this.userSession.refreshToken = savedSession.refreshToken;
      this.userSession.expiration = savedSession.expiration;
      this.userSession.state = savedSession.state;

    }
    this.logger.trace('[constructor] Loaded Session: ' + JSON.stringify(this.userSession, null, 2));
    this.intervalTimer = window.setInterval(this.checkTokenRefresh.bind(this), 1000);

  }

  ngOnDestroy() {
    window.clearInterval(this.intervalTimer);
  }

  /**
   * Saves the user session to local storage
   */
  private saveUserSession() {
    const json = JSON.stringify(this.userSession);
    this.logger.trace('[saveUserSession] Saving User Session: ' + json);
    localStorage.setItem(this.LOCAL_STORAGE_TOKEN_SESSION, json);
  }


  private failUserSession() {
    this.userSession.state = UserSessionState.AUTH_FAILED;
    this.userSession.refreshToken = '';
    this.userSession.accessToken = '';
    this.userSession.expiration = 0;
    localStorage.removeItem(this.LOCAL_STORAGE_TOKEN_SESSION);
  }

  /**
   * Authenticates the username and password. Puts the session into the request MFA State
   * @param username
   * @param password
   */
  public login(username: string, password: string) {
    this.logger.info('[login] Attempting logon for ' + username);

    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('client_id', 'web')
      .set('scope', 'api')
      .set('username', username)
      .set('password', password);

    return new Promise(loginResult => {
      const response = this.http.post('/oauth/token', body.toString(), this.FORM_HTTP_OPTIONS);
      response.subscribe((data: OAuthResponse) => {
          const oauthResponse = data;
          this.logger.trace('[login] Response: ' + JSON.stringify(oauthResponse));
          if (oauthResponse.error) {
            this.logger.warn('[login] Authorization failed: ' + JSON.stringify(oauthResponse));
            this.failUserSession();
          } else {
            this.logger.trace('[login] Authorization SUCCESS: ' + JSON.stringify(oauthResponse));
            this.userSession.state = UserSessionState.AUTHENTICATED;
            this.userSession.refreshToken = oauthResponse.refresh_token;
            this.userSession.accessToken = oauthResponse.access_token;
            this.userSession.expiration = ((new Date()).getTime() / 1000) + oauthResponse.expires_in;
            this.saveUserSession();
          }
          loginResult(this.userSession);
        },
        error => {
          this.logger.warn('[login] Authorization Error: ' + JSON.stringify(error));
          this.failUserSession();
          loginResult(this.userSession);
        });
    });


  }

  /**
   * Refreshes the existing access token.
   */
  public refreshAccessToken() {
    this.logger.info('[refreshAccessToken] Attempting token refresh for ' + this.userSession.refreshToken);

      const body = new HttpParams()
        .set('grant_type', 'refresh_token')
        .set('client_id', 'web')
        .set('scope', 'api')
        .set('refresh_token', this.userSession.refreshToken);

      const response = this.http.post('/oauth/token', body.toString(), this.FORM_HTTP_OPTIONS);
      response.subscribe((data: OAuthResponse) => {
          const oauthResponse = data;
          this.logger.debug('[refreshAccessToken] Response: ' + JSON.stringify(oauthResponse));
          if (oauthResponse.error) {
            this.logger.warn('[refreshAccessToken] Refresh failed: ' + JSON.stringify(oauthResponse));
            this.failUserSession();
          } else {
            this.logger.debug('[refreshAccessToken] Refresh Succeeded: ' + JSON.stringify(oauthResponse));
            if ((oauthResponse.refresh_token != undefined) && (oauthResponse.refresh_token != null) && (oauthResponse.refresh_token.length) != 0) {
              this.userSession.refreshToken = oauthResponse.refresh_token;
            }
            this.userSession.accessToken = oauthResponse.access_token;
            this.userSession.expiration = ((new Date()).getTime() / 1000) + oauthResponse.expires_in;
            this.saveUserSession();
          }
        },
        error => {
          this.logger.warn('[refreshAccessToken] Authorization Error: ' + JSON.stringify(error));
          this.failUserSession();
        });

  }

  /**
   * Utility method that will automatically generate the correct headers for api calls.
   */
  public getAuthorizedHttpOptions() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + this.userSession.accessToken,
        'Content-Type': 'application/json'
      })
    };
  }


  public logOut() {
    this.logger.info('[logout] Logging out user');
    this.userSession.state = UserSessionState.NO_AUTH;
    this.userSession.refreshToken = '';
    this.userSession.accessToken = '';
    this.userSession.expiration = 0;
    localStorage.removeItem(this.LOCAL_STORAGE_TOKEN_SESSION);
  }


  private checkTokenRefresh() {
    this.logger.trace('[checkTokenRefresh] Checking token refresh: ' + JSON.stringify(this.userSession));
    if (this.userSession.isAuthenticated()) {
      if (this.userSession.isExpired()) {
        this.logger.debug('[checkTokenRefresh] Refreshing OAUTH Token');
        this.refreshAccessToken();
      }
    }
  }


  /**
   * Returns a list of timezones
   */
  public getTimeZones() {
    this.logger.info('[getTimeZones] Looking up timezones');
    return new Promise(tzResult => {
      if (this.tzList.length > 0){
        //Cache the timezones locally since these won't change much.
        tzResult(this.tzList);
      } else {
        const response = this.http.get('/api/admin/tz');
        response.subscribe((data: Array<string>) => {
            this.tzList = data;
            tzResult(data);
          },
          error => {
            this.logger.warn('[getTimeZones] Get Error: ' + JSON.stringify(error));
            tzResult([]);
          });
      }
    });
  }

  /**
   * Polls the server to see if it needs to be configured for first time admin setup
   */
  public getAdminRequest() {
    this.logger.info('[getAdminRequest] Looking up admin Request');
    return new Promise(arResult => {
      const response = this.http.get('/api/admin');
      response.subscribe((data: AdminRequest) => {
          arResult(data);
        },
        error => {
          this.logger.warn('[getAdminRequest] Get Error: ' + JSON.stringify(error));
          arResult([]);
        });
    });
  }

  /***
   * Polls the server to see if it needs to be configured for first time admin setup
   * @param adminRequest
   */
  public postAdminRequest(adminRequest:AdminRequest) {
    this.logger.info('[postAdminRequest] Looking up admin Request');


    return new Promise(arResult => {

      const response = this.http.post('/api/admin',
        JSON.stringify(adminRequest),
        {headers:new HttpHeaders({'Content-Type': 'application/json'})});

      response.subscribe((data: AdminRequest) => {
          arResult(data);
        },
        error => {
          this.logger.warn('[postAdminRequest] Get Error: ' + JSON.stringify(error));
          arResult(null);
        });
    });
  }

  /**
   * Gets the activation details from the server
   */
  getActivationDetails() {
    this.logger.info('[getActivationDetails] Looking up activation details');
    return new Promise(adResult => {
      const response = this.http.get('/api/activate', this.getAuthorizedHttpOptions());
      response.subscribe((data: AdminRequest) => {
          adResult(data);
        },
        error => {
          this.logger.warn('[getActivationDetails] Get Error: ' + JSON.stringify(error));
          adResult(new ActivationDetails());

        });
    });
  }

  /**
   * Gets the activation details from the server
   */
  verifyAccessToken() {
    this.logger.info('[verifyAccessToken] Looking up activation details');
    return new Promise(adResult => {
      const response = this.http.get('/api/version/ping', this.getAuthorizedHttpOptions());
      response.subscribe(data => {
          adResult(true);
        },
        error => {
          this.logger.warn('[verifyAccessToken] Get Error: ' + JSON.stringify(error));
          adResult(false);
        });
    });
  }



}



