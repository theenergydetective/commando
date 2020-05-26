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

import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.service.AuthorizationService;
import com.ted.commando.service.DailyEnergyDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DailyEnergyControllerTest {

    @Mock
    DailyEnergyDataService dailyEnergyDataService;

    @Mock
    AuthorizationService authorizationService;

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
        dailyEnergyData.setEnergyDate(1L);
        dailyEnergyData.setEnergyValue(BigDecimal.TEN);
        dailyEnergyDataController.updateRecord(dailyEnergyData);
        verify(dailyEnergyDataService).update(dailyEnergyData);
    }

    @Test
    public void getRecordsTest(){
        when(dailyEnergyDataService.findByIdDate(anyString(), anyString(),anyString())).thenReturn(new ArrayList<>());
        assertNotNull(dailyEnergyDataController.getRecords("TESTMTU", "2020-01-01", "2020-02-01"));
        verify(dailyEnergyDataService).findByIdDate("TESTMTU", "2020-01-01", "2020-02-01");
    }

    @Test
    public void exportDataTest() throws IOException {
        String dailyFormParamters = "{\"selectedDevices\":[\"TESTDEV0\",\"TESTDEV1\",\"TESTDEV10\",\"TESTDEV11\",\"TESTDEV12\",\"TESTDEV13\",\"TESTDEV14\",\"TESTDEV15\",\"TESTDEV2\",\"TESTDEV3\",\"TESTDEV4\",\"TESTDEV5\",\"TESTDEV6\",\"TESTDEV7\",\"TESTDEV8\",\"TESTDEV9\"],\"accessToken\":\"zIPfpIENzDNrL+Oacwzb5blrflQ=\",\"billingCycleStart\":1,\"exportType\":\"DAY\",\"range\":{\"start\":{\"selected\":true,\"month\":4,\"year\":2020},\"end\":null}}";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("formData")).thenReturn(dailyFormParamters);
        when(authorizationService.isAuthorized(anyString())).thenReturn(true);

        dailyEnergyDataController.exportData(request, response);
        verify(response).setStatus(200);
        verify(dailyEnergyDataService).writeDailyData(any(), any());
        reset(dailyEnergyDataService);
        reset(response);

        String billingFormParameters = "{\"selectedDevices\":[\"TESTDEV0\",\"TESTDEV1\",\"TESTDEV10\",\"TESTDEV11\",\"TESTDEV12\",\"TESTDEV13\",\"TESTDEV14\",\"TESTDEV15\",\"TESTDEV2\",\"TESTDEV3\",\"TESTDEV4\",\"TESTDEV5\",\"TESTDEV6\",\"TESTDEV7\",\"TESTDEV8\",\"TESTDEV9\"],\"accessToken\":\"zIPfpIENzDNrL+Oacwzb5blrflQ=\",\"billingCycleStart\":1,\"exportType\":\"BILLING\",\"range\":{\"start\":{\"selected\":true,\"month\":4,\"year\":2020},\"end\":null}}";
        when(request.getParameter("formData")).thenReturn(billingFormParameters);
        dailyEnergyDataController.exportData(request, response);
        verify(response).setStatus(200);
        verify(dailyEnergyDataService).writeBillingCycleData(any(), any());
        reset(dailyEnergyDataService);
        reset(response);

        when(authorizationService.isAuthorized(anyString())).thenReturn(false);
        dailyEnergyDataController.exportData(request, response);
        verify(response).sendError(401);
        verify(dailyEnergyDataService, times(0)).writeDailyData(any(), any());

        reset(dailyEnergyDataService);
        reset(response);
        when(authorizationService.isAuthorized(anyString())).thenReturn(true);
        doThrow(new IOException()).when(response).flushBuffer();
        dailyEnergyDataController.exportData(request, response);
        verify(response).sendError(401);
        verify(dailyEnergyDataService, times(1)).writeBillingCycleData(any(), any());


    }

    @Test
    public void parseFormParametersTest(){
        String formParameters = "{\"selectedDevices\":[\"TESTDEV0\",\"TESTDEV1\",\"TESTDEV10\",\"TESTDEV11\",\"TESTDEV12\",\"TESTDEV13\",\"TESTDEV14\",\"TESTDEV15\",\"TESTDEV2\",\"TESTDEV3\",\"TESTDEV4\",\"TESTDEV5\",\"TESTDEV6\",\"TESTDEV7\",\"TESTDEV8\",\"TESTDEV9\"],\"accessToken\":\"zIPfpIENzDNrL+Oacwzb5blrflQ=\",\"billingCycleStart\":1,\"exportType\":\"DAY\",\"range\":{\"start\":{\"selected\":true,\"month\":4,\"year\":2020},\"end\":null}}";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("formData")).thenReturn(formParameters);
        assertNotNull(dailyEnergyDataController.parseFormParameters(request));

        when(request.getParameter("formData")).thenReturn("BAD JSON DATA");
        assertNull(dailyEnergyDataController.parseFormParameters(request));
    }
}
