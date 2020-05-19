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

export enum UserSessionState {
  NO_AUTH,
  AUTH_FAILED,
  AUTHENTICATED,
}

export class UserSession {

  public refreshToken: string = null;
  public accessToken: string = null;
  public expiration = 0;

  public state: UserSessionState = UserSessionState.NO_AUTH;

  constructor() {
  }

  public isExpired(): boolean {
    const currentTime = new Date().getTime() / 1000;
    return (this.expiration - 30) < currentTime;
  }

  public isAuthenticated(): boolean {
    return this.state === UserSessionState.AUTHENTICATED;
  }

}

