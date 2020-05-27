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

import com.ted.commando.dao.BillingDataDAO;
import com.ted.commando.dao.DailyEnergyDataDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.enums.ExportType;
import com.ted.commando.model.BillingFormParameters;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.model.MeasuringTransmittingUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DailyEnergyDataServiceTest {


    @Mock
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;

    @Mock
    BillingDataDAO billingDataDAO;

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
        when(userDetailsService.getTimezone()).thenReturn(NYTZ);

        MeasuringTransmittingUnit mtu = new MeasuringTransmittingUnit();
        mtu.setId("TESTMTU");
        mtu.setLastDayPost(1589860740L);
        mtu.setLastDayValue(new BigDecimal(100000));
        mtu.setLastPost(1589860800L);
        mtu.setLastValue(new BigDecimal(150000));
        mtu.setTimezone(NYTZ);

        dailyEnergyDataService.processDailyEnergyData(mtu);
        verify(measuringTransmittingUnitDAO).updateLastDayPost(any());
        verify(dailyEnergyDataDAO).insert(any());

        mtu.setTimezone(LATZ);
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
        dailyEnergyData.setEnergyDate(20200523L);

        mtu.setTimezone(NYTZ);
        reset(measuringTransmittingUnitDAO);
        reset(dailyEnergyDataDAO);
        dailyEnergyDataService.processDailyEnergyData(mtu);
        verify(measuringTransmittingUnitDAO).updateLastDayPost(expectedFinalMTU);
        verify(dailyEnergyDataDAO, times(5)).insert(any());
        verify(dailyEnergyDataDAO, times(1)).insert(dailyEnergyData);
    }


    @Test
    public void writeDataTest(){
        HttpServletResponse response = mock(HttpServletResponse.class);
        BillingFormParameters parameters = new BillingFormParameters();
        parameters.setExportType(ExportType.DAY);
    }

    @Test
    public void findByIdDateTest(){
        DailyEnergyData dailyEnergyData = new DailyEnergyData();
        dailyEnergyData.setMtuId("TEST");
        dailyEnergyData.setEnergyDate(20200420L);
        dailyEnergyData.setEnergyValue(new BigDecimal(10000));

        List<DailyEnergyData> dailyEnergyDataList = new ArrayList<>();
        dailyEnergyDataList.add(dailyEnergyData);

        when(dailyEnergyDataDAO.findByMtu("TEST")).thenReturn(dailyEnergyDataList);
        when(dailyEnergyDataDAO.findByIdDate("TEST", 20200401L, 20200430L)).thenReturn(dailyEnergyDataList);
        List<DailyEnergyData> results = dailyEnergyDataService.findByIdDate("TEST", null, null);
        verify(dailyEnergyDataDAO).findByMtu("TEST");
        assertEquals("Apr 20, 2020", results.get(0).getFormattedDate());

        results = dailyEnergyDataService.findByIdDate("TEST", "2020-04-01", "2020-04-30");
        verify(dailyEnergyDataDAO).findByIdDate("TEST",20200401L, 20200430L);
        assertEquals("Apr 20, 2020", results.get(0).getFormattedDate());

    }

    @Test
    public void updateTest(){
        dailyEnergyDataService.update(new DailyEnergyData());
        verify(dailyEnergyDataDAO).update(any());
    }

    @Test
    public void writeDailyDataTest(){
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        dailyEnergyDataService.writeDailyData(new BillingFormParameters(), outputStream);
        verify(billingDataDAO).exportDailyData(any(),any());
    }

    @Test
    public void writeBillingCycleDataTest(){
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        dailyEnergyDataService.writeBillingCycleData(new BillingFormParameters(), outputStream);
        verify(billingDataDAO).exportBillingCycleData(any(),any());
    }


}
