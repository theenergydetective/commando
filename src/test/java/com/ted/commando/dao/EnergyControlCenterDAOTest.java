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
import com.ted.commando.service.VersionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
@WebAppConfiguration
public class EnergyControlCenterDAOTest {

    static String TEST_ID = "FAKE1234";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EnergyControlCenterDAO energyControlCenterDAO;

    @Before
    public void setup(){
        cleanUpData();
    }

    @After
    public void tearDown(){
        cleanUpData();
    }

    private void cleanUpData(){
        jdbcTemplate.update("DELETE FROM ecc where id = ?", new Object[]{TEST_ID});
    }

    @Test
    public void insertFindUpdateDeleteTest(){
        EnergyControlCenter ecc = new EnergyControlCenter();
        ecc.setId(TEST_ID);
        ecc.setVersion("TEST");
        ecc.setSecurityKey("FAKEKEY");
        energyControlCenterDAO.insert(ecc);
        assertEquals(TEST_ID, energyControlCenterDAO.findOne(TEST_ID).getId());

        ecc.setVersion("TEST2");
        energyControlCenterDAO.insert(ecc);
        assertEquals("TEST2", energyControlCenterDAO.findOne(TEST_ID).getVersion());

        ecc.setSecurityKey("NEW KEY");
        energyControlCenterDAO.update(ecc);
        assertEquals("NEW KEY", energyControlCenterDAO.findOne(TEST_ID).getSecurityKey());

        energyControlCenterDAO.delete(ecc.getId());
        assertNull(energyControlCenterDAO.findOne(TEST_ID));
    }
}


