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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivationControllerTest {

    private static String TEST_ACTIVATION_KEY = "12345";
    private static String TEST_SECURITY_KEY = "ABCDE";
    private static String TEST_ECC_ID = "TESTECCID";

    @Mock
    EnergyControlCenterDAO energyControlCenterDAO;

    @Mock
    KeyService keyService;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    Environment environment;

    @InjectMocks
    ActivationController activationController;


    @Before
    public void setup() {
        reset(keyService);
        reset(energyControlCenterDAO);
    }

    @Test
    public void activateTest() throws Exception{
        when(userDetailsService.getActivationKey()).thenReturn(TEST_ACTIVATION_KEY);
        when(energyControlCenterDAO.findOne(TEST_ECC_ID)).thenReturn(null);
        when(environment.getProperty("local.server.port")).thenReturn("1234");

        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.gateway = TEST_ECC_ID;
        activationRequest.unique = TEST_ACTIVATION_KEY;
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        //Test adding a new ECC (No port Specified)
        when(userDetailsService.getServerName()).thenReturn("127.0.0.1");
        ActivationResponse goodActivationResponse = activationController.activate(activationRequest, httpServletResponse);
        assertNotNull(goodActivationResponse);
        assertEquals(80, goodActivationResponse.PostPort);
        verify(energyControlCenterDAO).findOne(TEST_ECC_ID);
        verify(energyControlCenterDAO).insert(any());

        //Port Specified
        when(userDetailsService.getServerName()).thenReturn("127.0.0.1:8888");
        reset(energyControlCenterDAO);
        goodActivationResponse = activationController.activate(activationRequest, httpServletResponse);
        assertNotNull(goodActivationResponse);
        assertEquals(8888, goodActivationResponse.PostPort);
        verify(energyControlCenterDAO).findOne(TEST_ECC_ID);
        verify(energyControlCenterDAO).insert(any());

        //Test re-adding an existing ECC
        reset(energyControlCenterDAO);
        EnergyControlCenter existingECC = new EnergyControlCenter();
        existingECC.setId(TEST_ECC_ID);
        existingECC.setSecurityKey(TEST_SECURITY_KEY);
        when(energyControlCenterDAO.findOne(TEST_ECC_ID)).thenReturn(existingECC);
        ActivationResponse existingActivationResponse = activationController.activate(activationRequest, httpServletResponse);
        assertNotNull(existingActivationResponse);
        verify(energyControlCenterDAO).findOne(TEST_ECC_ID);
        verify(energyControlCenterDAO, times(0)).insert(any());

        //Test a bad activation key.
        reset(energyControlCenterDAO);
        when(userDetailsService.getActivationKey()).thenReturn("BADACTIVATIONKEY");
        ActivationResponse badKeyActivationResponse = activationController.activate(activationRequest, httpServletResponse);
        assertNull(badKeyActivationResponse);
        verify(energyControlCenterDAO, times(0)).findOne(TEST_ECC_ID);
        verify(energyControlCenterDAO, times(0)).insert(any());


        //Test a critical exception
        reset(energyControlCenterDAO);
        when(userDetailsService.getActivationKey()).thenThrow(new RuntimeException("Fake Exception"));
        ActivationResponse exceptionActivationResponse = activationController.activate(activationRequest, httpServletResponse);
        assertNull(exceptionActivationResponse);

    }

    @Test
    public void getActivationDetailsTest(){
        when(userDetailsService.getActivationDetails()).thenReturn(new ActivationDetails());
        assertNotNull(activationController.getActivationDetails());
        verify(userDetailsService).getActivationDetails();
    }
}
