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
import com.ted.commando.model.BillingFormParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
@WebAppConfiguration
public class BillingDataDAOTest {

    static String TEST_PREFIX = "FAKEBC";
    

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BillingDataDAO billingDataDAO;

    @Before
    public void setup(){
        cleanUpData();
        createTestData();
    }

    @After
    public void tearDown(){
        cleanUpData();
    }

    private void cleanUpData(){
        jdbcTemplate.update("DELETE FROM daily_energy_data where mtu_id like '" + TEST_PREFIX + "%'");
        jdbcTemplate.update("DELETE FROM mtu where id like '" + TEST_PREFIX + "%'");
    }

    private void createTestData(){
        for (int m=1; m < 5; m++) {
            String mtuId = TEST_PREFIX + m;
            jdbcTemplate.update("INSERT INTO mtu(id,name,timezone) values(?,?,?)",
                    new Object[]{mtuId, mtuId,"America/New_York"});

            for (int d = 1; d < 30; d++) {
                long baseDate = 20200400L;
                baseDate += d;
                jdbcTemplate.update("INSERT INTO daily_energy_data(mtu_id,energy_date,energy_value) values(?,?,?)",
                        new Object[]{mtuId, baseDate, 10000});
            }
        }
    }

    @Test
    public void exportDailyDataTest(){
        PrintWriter printWriter = mock(PrintWriter.class);
        BillingFormParameters billingFormParameters = new BillingFormParameters();
        billingFormParameters.getSelectedDevices().add(TEST_PREFIX + 1);
        billingFormParameters.getSelectedDevices().add(TEST_PREFIX + 2);
        billingFormParameters.setStartDate("2020-04-20");
        billingFormParameters.setEndDate("2020-04-25");

        billingDataDAO.exportDailyData(billingFormParameters, printWriter);
        verify(printWriter, times(3)).println(anyString());


    }



    @Test
    public void exportCycleDataTest(){
        PrintWriter printWriter = mock(PrintWriter.class);
        BillingFormParameters billingFormParameters = new BillingFormParameters();
        billingFormParameters.getSelectedDevices().add(TEST_PREFIX + 1);
        billingFormParameters.getSelectedDevices().add(TEST_PREFIX + 2);
        billingFormParameters.setStartDate("2020-03-01");
        billingFormParameters.setEndDate("2020-05-01");
        billingFormParameters.setMeterReadDate(15);

        billingDataDAO.exportBillingCycleData(billingFormParameters, printWriter);
        verify(printWriter, times(5)).println(anyString());


    }

}


