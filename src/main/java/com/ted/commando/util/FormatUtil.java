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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FormatUtil {
    final static Logger LOGGER = LoggerFactory.getLogger(FormatUtil.class);

    static SimpleDateFormat prettyFormat = new SimpleDateFormat("MMM dd, YYYY");
    static SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/YYYY");

    public static final Long parseStringDate(String stringDate, TimeZone timeZone) {
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
        calendar.set(Calendar.MONTH, Integer.parseInt(s[1]) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(s[2]));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() / 1000;
    }


    public static final Long parseEnergyDateString(String stringDate) {
        if (stringDate == null) return 0L;
        try {
            stringDate = stringDate.replace("-", "").trim();
            return Long.parseLong(stringDate);
        } catch (NumberFormatException ex) {
            LOGGER.error("[parseEnergyDateString] Error parsing {}", stringDate, ex);
            return 0L;
        }
    }


    public static final Long parseCycleDateString(String stringDate) {
        if (stringDate == null) return 0L;
        try {
            stringDate = stringDate.replace("-", "").trim();
            stringDate = stringDate.substring(0, 6); //Drop the day field
            return Long.parseLong(stringDate);
        } catch (NumberFormatException ex) {
            LOGGER.error("[parseEnergyDateString] Error parsing {}", stringDate, ex);
            return 0L;
        }
    }

    public static String prettyFormatEnergyDate(Long energyDate) {
        String energyDateString = energyDate.toString();
        if (energyDateString.length() == 8) {
            int year = Integer.parseInt(energyDateString.substring(0, 4));
            int month = Integer.parseInt(energyDateString.substring(4, 6));
            int date = Integer.parseInt(energyDateString.substring(6, 8));
            Date parsedDate = new Date(year - 1900, month - 1, date, 0, 0, 0);
            return prettyFormat.format(parsedDate);
        }
        return "";
    }

    public static String simpleFormatEnergyDate(String energyDateString) {
        Date parsedDate = convertEnergyDateToDate(energyDateString);
        if (parsedDate != null) return simpleFormat.format(parsedDate);
        return "";
    }


    public static Date convertEnergyDateToDate(String energyDateString) {
        energyDateString = energyDateString.replace("-", "").trim();
        if (energyDateString.length() == 8) {
            int year = Integer.parseInt(energyDateString.substring(0, 4));
            int month = Integer.parseInt(energyDateString.substring(4, 6));
            int date = Integer.parseInt(energyDateString.substring(6, 8));
            Date parsedDate = new Date(year - 1900, month - 1, date, 0, 0, 0);
            return parsedDate;
        }
        return null;

    }

    public static Long convertSimpleDateToEnergyDate(String field) {
        try {
            String fields[] = field.split("/");
            if (fields.length == 3) {
                StringBuilder value = new StringBuilder(fields[2]).append(fields[0]).append(fields[1]);
                return Long.parseLong(value.toString());
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            LOGGER.error("[convertSimpleDateToEnergyDate] Parse Exception:{}", field, e);
            return null;
        }
    }
}
