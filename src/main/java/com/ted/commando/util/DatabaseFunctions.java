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

import java.sql.Connection;

public class DatabaseFunctions {

    public static int getBillingCycleMonth(Connection connection, Long energyDate, int meterReadDate){
        //parse the month
        String energyDateString = energyDate.toString();

        int month = Integer.parseInt(energyDateString.substring(4, 6));
        int date = Integer.parseInt(energyDateString.substring(6, 8));

        if (date < meterReadDate) {
            month--;
            if (month == 0) month = 12;
        }
        return month;
    }

    public static int getBillingCycleYear(Connection connection, Long energyDate, int meterReadDate){
        //parse the month
        String energyDateString = energyDate.toString();

        int year = Integer.parseInt(energyDateString.substring(0, 4));
        int month = Integer.parseInt(energyDateString.substring(4, 6));
        int date = Integer.parseInt(energyDateString.substring(6, 8));

        if (date < meterReadDate) {
            month--;
            if (month == 0) year--;
        }

        return year;
    }

}
