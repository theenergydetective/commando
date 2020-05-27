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
import com.ted.commando.model.DailyEnergyData;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
@WebAppConfiguration
public class DailyEnergyDataDAOTest {

    static String TEST_ID = "FAKE1234";
    

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Before
    public void setup(){
        cleanUpData();
    }

    @After
    public void tearDown(){
        cleanUpData();
    }

    private void cleanUpData(){
        jdbcTemplate.update("DELETE FROM daily_energy_data where mtu_id = ?", new Object[]{TEST_ID});
    }

    @Test
    public void insertFindUpdateDeleteTest(){


        dailyEnergyDataDAO.insert(new DailyEnergyData(TEST_ID, 1L, new BigDecimal(1000.0)));
        assertNotNull(dailyEnergyDataDAO.findOne(TEST_ID, 1L));

        dailyEnergyDataDAO.insert(new DailyEnergyData(TEST_ID, 2L, new BigDecimal(2000.0)));
        dailyEnergyDataDAO.insert(new DailyEnergyData(TEST_ID, 3L, new BigDecimal(3000.0)));
        dailyEnergyDataDAO.insert(new DailyEnergyData(TEST_ID, 4L, new BigDecimal(4000.0)));
        dailyEnergyDataDAO.insert(new DailyEnergyData(TEST_ID, 5L, new BigDecimal(5000.0)));

        assertEquals(5, dailyEnergyDataDAO.findByMtu(TEST_ID).size());
        assertEquals(6000.0 , dailyEnergyDataDAO.sumTotalEnergy(TEST_ID, 1, 4).doubleValue(), 1.0);

        assertEquals(3, dailyEnergyDataDAO.findByIdDate(TEST_ID, 1L, 4L).size());

        DailyEnergyData dailyEnergyData = dailyEnergyDataDAO.findOne(TEST_ID, 1L);
        dailyEnergyData.setEnergyValue(new BigDecimal(100000.0));
        dailyEnergyDataDAO.update(dailyEnergyData);
        assertEquals(100000.0d , dailyEnergyDataDAO.findOne(TEST_ID, 1L).getEnergyValue().doubleValue(), 1.0);

        dailyEnergyData.setEnergyValue(new BigDecimal(200000.0));
        dailyEnergyDataDAO.insert(dailyEnergyData);
        assertEquals(200000.0d , dailyEnergyDataDAO.findOne(TEST_ID, 1L).getEnergyValue().doubleValue(), 1.0);

    }
}


