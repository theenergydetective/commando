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
import com.ted.commando.model.EnergyCumulativePost;
import com.ted.commando.model.EnergyMTUPost;
import com.ted.commando.model.EnergyPost;
import com.ted.commando.service.EnergyPostService;
import com.ted.commando.service.UserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    AdminController adminController;

    @Before
    public void setup() {
        reset(userDetailsService);
    }

    @Test
    public void getAdminRequestTest() throws Exception{
        when(userDetailsService.getAdminRequestRequired()).thenReturn(new AdminRequest());
        assertNotNull(adminController.getAdminRequest());
    }


    @Test
    public void setAdminRequestTest() throws Exception{
        when(userDetailsService.getAdminRequestRequired()).thenReturn(new AdminRequest());
        HttpServletResponse response =mock(HttpServletResponse.class);

        when(userDetailsService.setAdminRequest(any())).thenReturn(false);
        assertNull(adminController.setAdminRequest(new AdminRequest(),response));

        when(userDetailsService.setAdminRequest(any())).thenReturn(true);
        assertNotNull(adminController.setAdminRequest(new AdminRequest(),response));
    }



    @Test
    public void getTimeZonesTest() throws Exception{
        assertNotNull(adminController.getTimeZones());
    }
}
