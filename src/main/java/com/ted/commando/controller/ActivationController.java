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

package com.ted.commando.controller;

import com.ted.commando.dao.EnergyControlCenterDAO;
import com.ted.commando.model.ActivationDetails;
import com.ted.commando.model.ActivationRequest;
import com.ted.commando.model.ActivationResponse;
import com.ted.commando.model.EnergyControlCenter;
import com.ted.commando.service.KeyService;
import com.ted.commando.service.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

/***
 * Controller that implements the endpoint for dealing with new ECC/MTU3K Activations
 * @author Pete Arvanitis (pete@petecode.com)
 */
@RequestMapping("/api/activate")
@RestController
public class ActivationController {

    final static Logger LOGGER = LoggerFactory.getLogger(ActivationController.class);

    @Inject
    UserDetailsService userDetailsService;

    @Inject
    EnergyControlCenterDAO energyControlCenterDAO;

    @Inject
    KeyService keyService;

    /**
     * Allows and ECC or  MTU to get its security code so it can begin posting to the system,
     *
     * @param activationRequest
     * @param response
     * @return the activation response in xml
     * @throws Exception
     */
    @RequestMapping(consumes = "application/xml",
            method = RequestMethod.POST)
    public
    @ResponseBody
    ActivationResponse activate(@RequestBody ActivationRequest activationRequest,
                                HttpServletResponse response) throws Exception {
        try {
            LOGGER.info("[activate] Processing Request {}", activationRequest);

            String activationKey = userDetailsService.getActivationKey();

            boolean validActivationKey = activationKey == null || activationKey.isEmpty() || activationKey.equals(activationRequest.unique);

            if (validActivationKey) {
                EnergyControlCenter ecc = energyControlCenterDAO.findOne(activationRequest.gateway);
                if (null != ecc) {
                    LOGGER.info("[activate] ECC already activated: {}", ecc);
                } else {
                    ecc = new EnergyControlCenter();
                    ecc.setId(activationRequest.gateway);
                    ecc.setSecurityKey(keyService.generateSecurityKey(20));
                    ecc.setVersion("");
                    energyControlCenterDAO.insert(ecc);
                    LOGGER.info("[activate] Activated new ECC in system: {}", ecc);
                }

                ActivationResponse activationResponse = new ActivationResponse();
                activationResponse.PostServer = userDetailsService.getServerName();
                activationResponse.AuthToken = ecc.getSecurityKey();
                activationResponse.PostRate = 1;
                LOGGER.debug("[activate] Returning activation response:{}", activationResponse);
                return activationResponse;

            } else {
                LOGGER.warn("[activate] Invalid activation key specified. Expected:{} Received:{}", activationKey, activationRequest.unique);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return null;
            }

        } catch (Exception ex) {
            LOGGER.error("[activate] Critical Exception", ex);
            response.sendError(500);
            return null;
        }
    }


    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    ActivationDetails getActivationDetails(){
        return userDetailsService.getActivationDetails();
    }

}
