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

import java.sql.Connection;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseFunctionsTest {

    @Test
    public void getBillingCycleMonthTest(){
        Connection connection = mock(Connection.class);
        Long date = 20200420L;
        assertEquals(4, DatabaseFunctions.getBillingCycleMonth(connection, date, 20));
        assertEquals(4, DatabaseFunctions.getBillingCycleMonth(connection, date, 1));
        assertEquals(3, DatabaseFunctions.getBillingCycleMonth(connection, date, 21));
    }

    @Test
    public void getBillingCycleYearTest(){
        Connection connection = mock(Connection.class);
        Long date = 20200120L;
        assertEquals(2020, DatabaseFunctions.getBillingCycleYear(connection, date, 20));
        assertEquals(2020, DatabaseFunctions.getBillingCycleYear(connection, date, 1));
        assertEquals(2019, DatabaseFunctions.getBillingCycleYear(connection, date, 21));
    }


    @Test
    public void getBillingCycleKeyTest(){
        Connection connection = mock(Connection.class);
        Long date = 20200120L;
        assertEquals(202001, DatabaseFunctions.getBillingCycleKey(connection, date, 20));
        assertEquals(202001, DatabaseFunctions.getBillingCycleKey(connection, date, 1));
        assertEquals(201912, DatabaseFunctions.getBillingCycleKey(connection, date, 21));
    }

}
