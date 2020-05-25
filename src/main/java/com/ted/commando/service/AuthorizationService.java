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
import com.ted.commando.model.BillingFormParameters;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.model.MeasuringTransmittingUnit;
import com.ted.commando.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Service for handling Authorization checks outside of spring boot (mostly used for downloads).
 *
 * @author PeteArvanitis (pete@petecode.com)
 */

@Service
public class AuthorizationService {
    final static Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

    @Inject
    TokenStore authTokenStore;

    /**
     * Checks to see if the specified access token is valid.
     * @param accessTokenString
     * @return true if the token is valid
     */
    public Boolean isAuthorized(String accessTokenString){
        OAuth2AccessToken accessToken = authTokenStore.readAccessToken(accessTokenString);
        if (accessToken == null) {
            LOGGER.warn("[isAuthorized] Invalid Access Token Specified", accessTokenString);
            return false;
        }
        return authTokenStore.readAuthentication(accessToken) != null;
    }
}
