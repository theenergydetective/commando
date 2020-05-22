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

package com.ted.commando.service;


import com.ted.commando.dao.DailyEnergyDataDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.model.MeasuringTransmittingUnit;
import com.ted.commando.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Service for handling the access to daily energy data
 *
 * @author PeteArvanitis (pete@petecode.com)
 */

@Service
public class DailyEnergyDataService {
    final static Logger LOGGER = LoggerFactory.getLogger(DailyEnergyDataService.class);

    @Inject
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Inject
    UserDetailsService userDetailsService;

    @Inject
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;


    @Async
    public void processDailyEnergyData(MeasuringTransmittingUnit mtu) {
        TimeZone timeZone = TimeZone.getTimeZone(userDetailsService.getTimezone());
        if (mtu.getTimezone() != null && !mtu.getTimezone().isEmpty()) {
            timeZone = TimeZone.getTimeZone(mtu.getTimezone());
        }
        LOGGER.debug("[processDailyEnergyData] Using timezone {}", timeZone);

        //Make sure the clock has not jumped back giving us a weird timestamp (DST)
        if (mtu.getLastPost().longValue() > mtu.getLastDayValue().longValue()) {

            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(mtu.getLastDayPost() * 1000);
            int lastDay = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTimeInMillis(mtu.getLastPost() * 1000);
            int today = calendar.get(Calendar.DAY_OF_YEAR);

            if (today > lastDay) {
                int days = today - lastDay;
                BigDecimal energyValue = mtu.getLastValue().subtract(mtu.getLastDayValue()).divide(new BigDecimal(days));
                LOGGER.debug("[processDailyEnergyData] Daily Energy Value {} smoothing:{}", energyValue, days > 1);

                calendar.setTimeInMillis(mtu.getLastDayPost() * 1000);

                for (int i = 0; i < days; i++) {
                    calendar.add(Calendar.DATE, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    DailyEnergyData dailyEnergyData = new DailyEnergyData();
                    dailyEnergyData.setMtuId(mtu.getId());
                    dailyEnergyData.setEnergyValue(energyValue);
                    dailyEnergyData.setEpochDate(calendar.getTimeInMillis() / 1000);
                    LOGGER.debug("[processDailyEnergyData] inserting daily data: {}", dailyEnergyData);
                    dailyEnergyDataDAO.insert(dailyEnergyData);
                }

                //Update the last post value.
                calendar.setTimeInMillis(mtu.getLastPost() * 1000);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                mtu.setLastDayPost(calendar.getTimeInMillis() / 1000);
                mtu.setLastDayValue(mtu.getLastValue());
                measuringTransmittingUnitDAO.updateLastDayPost(mtu);

            }

        }

    }

    public List<DailyEnergyData> findByIdDate(String mtuId, String startDate, String endDate) {
        if (startDate == null || endDate == null) return dailyEnergyDataDAO.findByMtu(mtuId);

        MeasuringTransmittingUnit mtu = measuringTransmittingUnitDAO.findOne(mtuId);
        TimeZone mtuTimeZone = TimeZone.getTimeZone(mtu.getTimezone());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        simpleDateFormat.setTimeZone(mtuTimeZone);

        List<DailyEnergyData>  dailyEnergyData= dailyEnergyDataDAO.findByIdDate(mtuId,
                FormatUtil.parseStringDate(startDate, mtuTimeZone ),
                FormatUtil.parseStringDate(endDate, mtuTimeZone));

        for (DailyEnergyData data: dailyEnergyData){
            long epochMs = data.getEpochDate() * 1000L;
            data.setFormattedDate(simpleDateFormat.format(new Date(epochMs)));
        }

        return  dailyEnergyData;
    }

    public DailyEnergyData update(DailyEnergyData dailyEnergyData) {
        LOGGER.info("[update] Updating {}", dailyEnergyData);
        dailyEnergyDataDAO.update(dailyEnergyData);
        return dailyEnergyData;
    }
}
