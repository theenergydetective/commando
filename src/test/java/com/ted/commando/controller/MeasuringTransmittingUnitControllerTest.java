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
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.ActivationRequest;
import com.ted.commando.model.ActivationResponse;
import com.ted.commando.model.EnergyControlCenter;
import com.ted.commando.model.MeasuringTransmittingUnit;
import com.ted.commando.service.KeyService;
import com.ted.commando.service.UserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeasuringTransmittingUnitControllerTest {

    @Mock
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;

    @InjectMocks
    MeasuringTransmittingUnitController measuringTransmittingUnitController;


    @Before
    public void setup() {
        reset(measuringTransmittingUnitDAO);
    }

    @Test
    public void getMTUsTest(){
        when(measuringTransmittingUnitDAO.findAll(anyBoolean())).thenReturn(new ArrayList<>());
        assertNotNull(measuringTransmittingUnitController.getMTUs(false));
        verify(measuringTransmittingUnitDAO).findAll(false);

    }

    @Test
    public void getMTUTest(){
        when(measuringTransmittingUnitDAO.findOne(anyString())).thenReturn(new MeasuringTransmittingUnit());
        assertNotNull(measuringTransmittingUnitController.getMTU("TEST"));
        verify(measuringTransmittingUnitDAO).findOne("TEST");
    }

    @Test
    public void setMTUTest(){
        MeasuringTransmittingUnit measuringTransmittingUnit = new MeasuringTransmittingUnit();
        measuringTransmittingUnit.setId("TEST");
        when(measuringTransmittingUnitDAO.findOne(anyString())).thenReturn(measuringTransmittingUnit);
        assertNotNull(measuringTransmittingUnitController.setMTU(measuringTransmittingUnit));
        verify(measuringTransmittingUnitDAO).updateSettings(measuringTransmittingUnit);
        verify(measuringTransmittingUnitDAO).findOne("TEST");
    }

}
