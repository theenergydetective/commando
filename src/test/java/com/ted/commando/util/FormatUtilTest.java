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

package com.ted.commando.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class FormatUtilTest {


    @Test
    public void parseStringDateTest(){

        String date = "2020-02-28";
        TimeZone NYTZ = TimeZone.getTimeZone("America/New_York");
        TimeZone LATZ = TimeZone.getTimeZone("America/Las_Angeles");

        assertEquals(1582866000L, FormatUtil.parseStringDate(date, NYTZ).longValue());
        assertEquals(1582848000L, FormatUtil.parseStringDate(date, LATZ).longValue());

    }

    @Test
    public void parseEnergyDateStringTest(){
        assertEquals(0L, FormatUtil.parseEnergyDateString(null).longValue());
        assertEquals(20200420L, FormatUtil.parseEnergyDateString("2020-04-20").longValue());
        assertEquals(0L, FormatUtil.parseEnergyDateString("ABCD-04-20").longValue());
    }

    @Test
    public void parseCycleDateStringTest(){
        assertEquals(0L, FormatUtil.parseCycleDateString(null).longValue());
        assertEquals(202004L, FormatUtil.parseCycleDateString("2020-04-20").longValue());
        assertEquals(0L, FormatUtil.parseCycleDateString("ABCD-04-20").longValue());
    }

    @Test
    public void prettyFormatEnergyDateTest(){
        assertEquals("Apr 20, 2020", FormatUtil.prettyFormatEnergyDate(20200420L));
        assertEquals("", FormatUtil.prettyFormatEnergyDate(202004L));
    }

    @Test
    public void simpleFormatEnergyDateTest(){
        assertEquals("04/20/2020", FormatUtil.simpleFormatEnergyDate("2020-04-20"));
        assertEquals("", FormatUtil.simpleFormatEnergyDate("2020-04-2000"));
    }
    @Test
    public void convertSimpleDateToEnergyDate(){
        assertEquals(20200420L, FormatUtil.convertSimpleDateToEnergyDate("04/20/2020").longValue());
        assertNull(FormatUtil.convertSimpleDateToEnergyDate("04/20XXXX/2020"));
        assertNull(FormatUtil.convertSimpleDateToEnergyDate("04/2020"));

    }


}
