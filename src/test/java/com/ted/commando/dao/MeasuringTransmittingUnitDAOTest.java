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

package com.ted.commando.dao;

import com.ted.commando.Application;
import com.ted.commando.model.EnergyControlCenter;
import com.ted.commando.model.MeasuringTransmittingUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
@WebAppConfiguration
public class MeasuringTransmittingUnitDAOTest {

    static String TEST_ID = "FAKE1234";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;

    @Before
    public void setup(){
        cleanUpData();
    }

    @After
    public void tearDown(){
        cleanUpData();
    }

    private void cleanUpData(){
        jdbcTemplate.update("DELETE FROM mtu where id = ?", new Object[]{TEST_ID});
    }

    @Test
    public void insertFindUpdateDeleteTest(){
        MeasuringTransmittingUnit mtu = new MeasuringTransmittingUnit();
        mtu.setId(TEST_ID);
        mtu.setRate(BigDecimal.TEN);
        mtu.setName(TEST_ID+"Name");
        mtu.setLastValue(new BigDecimal(100000.0));
        mtu.setLastPost(System.currentTimeMillis());
        measuringTransmittingUnitDAO.insert(mtu);
        assertEquals(TEST_ID, measuringTransmittingUnitDAO.findOne(TEST_ID).getId());

        mtu.setName("TEST2");
        measuringTransmittingUnitDAO.insert(mtu);
        assertEquals("TEST2", measuringTransmittingUnitDAO.findOne(TEST_ID).getName());

        mtu.setLastPost(123456789L);
        mtu.setLastValue(new BigDecimal(200000.0));
        measuringTransmittingUnitDAO.updateLastPost(mtu);
        assertEquals(mtu.getLastPost(), measuringTransmittingUnitDAO.findOne(TEST_ID).getLastPost());

        mtu.setLastDayPost(10000L);
        mtu.setLastDayValue(new BigDecimal(300000.0));
        measuringTransmittingUnitDAO.updateLastDayPost(mtu);
        assertEquals(mtu.getLastDayPost(), measuringTransmittingUnitDAO.findOne(TEST_ID).getLastDayPost());


        assertTrue(!measuringTransmittingUnitDAO.findAll().isEmpty());

        measuringTransmittingUnitDAO.delete(mtu.getId());
        assertNull(measuringTransmittingUnitDAO.findOne(TEST_ID));
    }
}


