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

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EnergyPostServiceTest {


    @Mock
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;

    @Mock
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Mock
    EnergyControlCenterDAO energyControlCenterDAO;

    @InjectMocks
    EnergyPostService energyPostService;

    @Before
    public void setup(){
        reset(measuringTransmittingUnitDAO);
        reset(dailyEnergyDataDAO);
        reset(energyControlCenterDAO);
    }

    @Test
    public void validateECCTest() {
        EnergyPost energyPost = new EnergyPost();
        energyPost.setGateway("TEST1");
        energyPost.setSecurityKey("ABCDE");
        energyPost.setVersion("v1");

        EnergyControlCenter ecc = new EnergyControlCenter();
        ecc.setSecurityKey("12345");
        ecc.setVersion("");


        when(energyControlCenterDAO.findOne(anyString())).thenReturn(null);
        assertFalse(energyPostService.validateECC(energyPost));

        when(energyControlCenterDAO.findOne(anyString())).thenReturn(ecc);
        assertFalse(energyPostService.validateECC(energyPost));

        energyPost.setSecurityKey("12345");
        assertTrue(energyPostService.validateECC(energyPost));
        verify(energyControlCenterDAO).update(ecc);

    }

    @Test
    public void findMTUTest(){
        MeasuringTransmittingUnit mtu = new MeasuringTransmittingUnit();
        mtu.setId("TESTMTU");

        when(measuringTransmittingUnitDAO.findOne("TESTMTU")).thenReturn(null);

        assertNotNull(energyPostService.findMTU("TESTMTU"));
        verify(measuringTransmittingUnitDAO).insert(any());

        reset(measuringTransmittingUnitDAO);
        when(measuringTransmittingUnitDAO.findOne("TESTMTU")).thenReturn(mtu);
        assertNotNull(energyPostService.findMTU("TESTMTU"));
        verify(measuringTransmittingUnitDAO, times(0)).insert(any());

    }


    @Test
    public void processMTUPostTest(){
        MeasuringTransmittingUnit mtu = new MeasuringTransmittingUnit();
        mtu.setId("TESTMTU");
        when(measuringTransmittingUnitDAO.findOne("TESTMTU")).thenReturn(null);

        EnergyMTUPost mtuPost = new EnergyMTUPost();
        mtuPost.setMtuSerial("TESTMTU");
        mtuPost.setCumulativePostList(new ArrayList<>());
        EnergyCumulativePost cumulativePost =new EnergyCumulativePost();
        mtuPost.getCumulativePostList().add(cumulativePost);
        cumulativePost.setWatts(100000.0);
        cumulativePost.setTimestamp(123456789L);

        energyPostService.processMTUPost(mtuPost);
        verify(measuringTransmittingUnitDAO).updateLastPost(any());
        verify(measuringTransmittingUnitDAO).updateLastDayPost(any());



    }
}
