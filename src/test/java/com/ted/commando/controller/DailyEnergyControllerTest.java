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

import com.ted.commando.model.AdminRequest;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.service.DailyEnergyDataService;
import com.ted.commando.service.UserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DailyEnergyControllerTest {

    @Mock
    DailyEnergyDataService dailyEnergyDataService;

    @InjectMocks
    DailyEnergyDataController dailyEnergyDataController;

    @Before
    public void setup() {
        reset(dailyEnergyDataService);
    }

    @Test
    public void getAdminRequestTest() throws Exception{
        when(dailyEnergyDataService.findByIdDate(anyString(),anyString(),anyString())).thenReturn(new ArrayList<>());
        assertNotNull(dailyEnergyDataService.findByIdDate("TEST", "2020-01-01", "2020-01-31"));
        verify(dailyEnergyDataService).findByIdDate("TEST","2020-01-01", "2020-01-31");
    }

    @Test
    public void updateTest() throws Exception{
        DailyEnergyData dailyEnergyData = new DailyEnergyData();
        dailyEnergyData.setMtuId("TEST");
        dailyEnergyData.setEpochDate(1L);
        dailyEnergyData.setEnergyValue(BigDecimal.TEN);
        dailyEnergyDataController.updateRecord(dailyEnergyData);
        verify(dailyEnergyDataService).update(dailyEnergyData);
    }

}
