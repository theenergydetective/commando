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

package com.ted.commando.service;

import com.ted.commando.dao.DailyEnergyDataDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.model.MeasuringTransmittingUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {


    @Mock
    TokenStore authTokenStore;

    @InjectMocks
    AuthorizationService authorizationService;



    @Test
    public void isAuthorizedTest(){
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        OAuth2Authentication authentication = mock(OAuth2Authentication.class);
        when(authTokenStore.readAccessToken(anyString())).thenReturn(accessToken);
        when(authTokenStore.readAuthentication(accessToken)).thenReturn(authentication);
        assertTrue(authorizationService.isAuthorized("FAKETOKEN"));
        verify(authTokenStore).readAccessToken("FAKETOKEN");
        verify(authTokenStore).readAuthentication(accessToken);

        reset(authTokenStore);

        when(authTokenStore.readAccessToken(anyString())).thenReturn(null);
        assertFalse(authorizationService.isAuthorized("FAKETOKEN"));
        verify(authTokenStore, times(1)).readAccessToken("FAKETOKEN");
        verify(authTokenStore, times(0)).readAuthentication(accessToken);

    }


}
