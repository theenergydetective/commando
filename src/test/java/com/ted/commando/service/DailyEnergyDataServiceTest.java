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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DailyEnergyDataServiceTest {


    @Mock
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;

    @InjectMocks
    DailyEnergyDataService dailyEnergyDataService;


    @Before
    public void setup(){
        reset(dailyEnergyDataDAO);
        reset(userDetailsService);
        reset(measuringTransmittingUnitDAO);
    }


    @Test
    public void processDailyEnergyDataTest(){
        String LATZ = "America/Los_Angeles";
        String NYTZ = "America/New_York";


        MeasuringTransmittingUnit mtu = new MeasuringTransmittingUnit();
        mtu.setId("TESTMTU");
        mtu.setLastDayPost(1589860740L);
        mtu.setLastDayValue(new BigDecimal(100000));
        mtu.setLastPost(1589860800L);
        mtu.setLastValue(new BigDecimal(150000));

        when(userDetailsService.getTimezone()).thenReturn(NYTZ);
        dailyEnergyDataService.processDailyEnergyData(mtu);
        verify(measuringTransmittingUnitDAO).updateLastDayPost(any());
        verify(dailyEnergyDataDAO).insert(any());


        when(userDetailsService.getTimezone()).thenReturn(LATZ);
        reset(measuringTransmittingUnitDAO);
        reset(dailyEnergyDataDAO);
        dailyEnergyDataService.processDailyEnergyData(mtu);
        verify(measuringTransmittingUnitDAO, times(0)).updateLastDayPost(any());
        verify(dailyEnergyDataDAO, times(0)).insert(any());



        //Verify smoothing
        mtu.setLastPost(1589860800L);

        mtu.setLastDayPost(1589860740L);
        mtu.setLastDayValue(new BigDecimal(100000));
        mtu.setLastPost(1590206400L);
        mtu.setLastValue(new BigDecimal(150000));


        MeasuringTransmittingUnit expectedFinalMTU = new MeasuringTransmittingUnit();
        expectedFinalMTU.setId("TESTMTU");
        expectedFinalMTU.setLastDayPost(1590206400L);
        expectedFinalMTU.setLastDayValue(new BigDecimal(150000));
        expectedFinalMTU.setLastPost(1590206400L);
        expectedFinalMTU.setLastValue(new BigDecimal(150000));


        DailyEnergyData dailyEnergyData = new DailyEnergyData();
        dailyEnergyData.setEnergyValue(new BigDecimal(10000.0));
        dailyEnergyData.setMtuId("TESTMTU");
        dailyEnergyData.setEpochDate(1589860800L);

        when(userDetailsService.getTimezone()).thenReturn(NYTZ);
        reset(measuringTransmittingUnitDAO);
        reset(dailyEnergyDataDAO);
        dailyEnergyDataService.processDailyEnergyData(mtu);
        verify(measuringTransmittingUnitDAO).updateLastDayPost(expectedFinalMTU);
        verify(dailyEnergyDataDAO, times(5)).insert(any());
        verify(dailyEnergyDataDAO, times(1)).insert(dailyEnergyData);
    }


}
