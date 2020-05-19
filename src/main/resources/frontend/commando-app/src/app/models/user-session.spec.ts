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
import {UserSession, UserSessionState} from './user-session';

describe('UserSession', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should not be expired', () => {
    const userSession = new UserSession();
    const currentTime = (new Date().getTime() / 1000);
    userSession.expiration = currentTime + 1000;
    expect(userSession.isExpired()).toBe(false);
  });

  it('should be expired', () => {
    const userSession = new UserSession();
    const currentTime = (new Date().getTime() / 1000);
    userSession.expiration = currentTime - 1000;
    expect(userSession.isExpired()).toBe(true);
  });

  it('should not be authenticated', () => {
    const userSession = new UserSession();
    userSession.state = UserSessionState.NO_AUTH;
    expect(userSession.isAuthenticated()).toBe(false);
  });

  it('should be authenticated', () => {
    const userSession = new UserSession();
    userSession.state = UserSessionState.AUTHENTICATED;
    expect(userSession.isAuthenticated()).toBe(true);
  });


});

