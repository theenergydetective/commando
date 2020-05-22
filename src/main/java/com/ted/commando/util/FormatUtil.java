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

import java.util.Calendar;
import java.util.TimeZone;

public class FormatUtil {

    public static final Long parseStringDate(String stringDate, TimeZone timeZone){
        String s[] = stringDate.split("-");
        Calendar calendar = Calendar.getInstance(timeZone);

        //Zero out the calendar to prevent it from adjusting for end of month, etc.
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //Set the parsed values
        calendar.set(Calendar.YEAR, Integer.parseInt(s[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(s[1])-1);
        calendar.set(Calendar.DATE, Integer.parseInt(s[2]));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis()/1000;
    }
}
