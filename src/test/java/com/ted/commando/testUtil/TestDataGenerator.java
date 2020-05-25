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

package com.ted.commando.testUtil;

import com.ted.commando.Application;
import com.ted.commando.controller.PostDataController;
import com.ted.commando.dao.EnergyControlCenterDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.*;
import com.ted.commando.service.UserDetailsService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is used to generate a bunch of test data for UI dev and testing. Normally this test
 * is marked as ignored.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
@WebAppConfiguration
public class TestDataGenerator {

    static final String ECC_ID = "TESTECC";
    static final String TEST_KEY = "TESTKEY";
    //static final int MAX_MTU_COUNT = (4 * 32) + 4;
    static final int MAX_MTU_COUNT = 16;

    @Autowired
    PostDataController postDataController;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    EnergyControlCenterDAO energyControlCenterDAO;


    @Autowired
    JdbcTemplate jdbcTemplate;


    private void cleanUpData(){
        jdbcTemplate.update("DELETE FROM daily_energy_data where mtu_id like 'TEST%'");
        jdbcTemplate.update("DELETE FROM mtu where id like 'TEST%'");
        jdbcTemplate.update("DELETE FROM ecc where id like 'TEST%'");
    }

    private void zeroOutCalendar(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    //@Ignore
    @Test
    public void generateTestData() throws Exception{
        cleanUpData();

        //Stubbed out
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(servletResponse.getWriter()).thenReturn(printWriter);


        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(userDetailsService.getTimezone()));
        calendar.setTimeInMillis(new Date().getTime());
        zeroOutCalendar(calendar);
        long startTime = calendar.getTimeInMillis();


        //Create the ecc
        EnergyControlCenter ecc = new EnergyControlCenter();
        ecc.setId(ECC_ID);
        ecc.setSecurityKey(TEST_KEY);
        ecc.setVersion("100");
        energyControlCenterDAO.insert(ecc);

        //Create MTU and Spyders
        calendar.add(Calendar.DATE, -365); //last several montns
        zeroOutCalendar(calendar);

        long wattsPerDay = 0;
        long wattsIncrement = 30000;

        while (calendar.getTimeInMillis() <= startTime){
            wattsIncrement += 10000;

            EnergyPost energyPost = new EnergyPost();
            energyPost.setSecurityKey(TEST_KEY);
            energyPost.setGateway(ECC_ID);
            energyPost.setMtuList(new ArrayList<>());
            energyPost.setSpyderList(new ArrayList<>());


            for (int i=0; i < MAX_MTU_COUNT; i++){
                EnergyMTUPost mtuPost = new EnergyMTUPost();
                mtuPost.setCumulativePostList(new ArrayList<>());
                mtuPost.setMtuSerial("TESTDEV" + i);
                mtuPost.setMtuTypeOrdinal(0);
                EnergyCumulativePost energyCumulativePost = new EnergyCumulativePost();
                energyCumulativePost.setTimestamp(calendar.getTimeInMillis()/1000);
                energyCumulativePost.setVoltage(120.0);
                energyCumulativePost.setPowerFactor(0.98);

                long watts = 1000000000L * i;
                watts += wattsPerDay;

                energyCumulativePost.setWatts((double)watts);
                mtuPost.getCumulativePostList().add(energyCumulativePost);
                energyPost.getMtuList().add(mtuPost);
            }



            postDataController.postData(energyPost, servletResponse);

            wattsPerDay += wattsIncrement;
            calendar.add(Calendar.DATE, 1);
            zeroOutCalendar(calendar);
        }








    }
}
