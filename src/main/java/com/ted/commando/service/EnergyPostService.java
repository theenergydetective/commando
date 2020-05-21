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


import com.ted.commando.dao.EnergyControlCenterDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * Service for handling the specific processing of an energy post. This is asynchronous to allow a quick response back
 * to the ECC.
 *
 * @author PeteArvanitis (pete@petecode.com)
 */

@Service
public class EnergyPostService {
    final static Logger LOGGER = LoggerFactory.getLogger(EnergyPostService.class);

    @Inject
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;

    @Inject
    DailyEnergyDataService dailyEnergyDataService;

    @Inject
    EnergyControlCenterDAO energyControlCenterDAO;

    @Inject
    UserDetailsService userDetailsService;

    /***
     * Validates that the ECC exists and has a valid security key
     * @param energyPost
     * @return
     */
    public boolean validateECC(EnergyPost energyPost) {
        String eccId = energyPost.getGateway();
        String securityKey = energyPost.getSecurityKey();

        LOGGER.debug("[validateECC] Validating ECC:{} with key:{}", eccId, securityKey);

        EnergyControlCenter ecc = energyControlCenterDAO.findOne(energyPost.getGateway());

        if (null == ecc) {
            LOGGER.warn("[postData] ECC not activated: {}", energyPost.getGateway());
            return false;
        }

        if (!ecc.getSecurityKey().equals(energyPost.getSecurityKey())) {
            LOGGER.warn("[postData] Invalid Security key for ECC:{}  Expected:{}  Actual:{} ", energyPost.getGateway(), ecc.getSecurityKey(), energyPost.getSecurityKey());
            return false;
        }

        if (energyPost.getVersion() != null && ecc.getVersion() != null && !ecc.getVersion().equals(energyPost.getVersion())) {
            LOGGER.info("[postData] Updating {} version to: {}", ecc.getVersion(), energyPost.getVersion());
            ecc.setVersion(energyPost.getVersion());
            energyControlCenterDAO.update(ecc);
        }
        LOGGER.debug("[validateECC] ECC:{} is valid", eccId);
        return true;
    }

    //@Async
    public void processEnergyPost(EnergyPost energyPost) {
        LOGGER.debug("[processEnergyPost] Processing: {}", energyPost);
        for (EnergyMTUPost mtuPost : energyPost.getMtuList()) {
            processMTUPost(mtuPost);
        }
        for (EnergyMTUPost mtuPost : energyPost.getSpyderList()) {
            processMTUPost(mtuPost);
        }
    }

    public MeasuringTransmittingUnit findMTU(String serialNumber) {
        MeasuringTransmittingUnit mtu = measuringTransmittingUnitDAO.findOne(serialNumber);
        if (null == mtu) {
            LOGGER.info("[processMTUPost] MTU Not Found. Adding");
            mtu = new MeasuringTransmittingUnit();
            mtu.setId(serialNumber);
            mtu.setName(serialNumber);
            mtu.setTimezone(userDetailsService.getTimezone());
            measuringTransmittingUnitDAO.insert(mtu);
        }
        return mtu;
    }

    public void processMTUPost(EnergyMTUPost mtuPost) {
        LOGGER.debug("[processMTUPost] Processing {}", mtuPost);

        MeasuringTransmittingUnit mtu = findMTU(mtuPost.getMtuSerial());

        for (EnergyCumulativePost cumulativePost : mtuPost.getCumulativePostList()) {

            if (mtu.getLastDayPost().equals(0L)) {
                LOGGER.debug("[processMTUPost] No daily history for mtu:{}. Defaulting to current value", mtu);
                mtu.setLastDayPost(cumulativePost.getTimestamp());
                mtu.setLastDayValue(new BigDecimal(cumulativePost.getWatts()));
                measuringTransmittingUnitDAO.updateLastDayPost(mtu);
            }

            //Update the last post value
            mtu.setLastValue(new BigDecimal(cumulativePost.getWatts()));
            mtu.setLastPost(cumulativePost.getTimestamp());
            measuringTransmittingUnitDAO.updateLastPost(mtu);

            dailyEnergyDataService.processDailyEnergyData(mtu);
        }

    }
}
