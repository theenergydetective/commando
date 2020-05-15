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
 * Service for handling the access to daily energy data
 *
 * @author PeteArvanitis (pete@petecode.com)
 */

@Service
public class DailyEnergyDataService {
    final static Logger LOGGER = LoggerFactory.getLogger(DailyEnergyDataService.class);

    @Inject
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Inject
    UserDetailsService userDetailsService;


    @Async
    public void processDailyEnergyData(MeasuringTransmittingUnit measuringTransmittingUnit){

    }

}
