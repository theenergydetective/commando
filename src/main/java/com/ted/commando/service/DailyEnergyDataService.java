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


import com.ted.commando.dao.BillingDataDAO;
import com.ted.commando.dao.DailyEnergyDataDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.BillingFormParameters;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.model.MeasuringTransmittingUnit;
import com.ted.commando.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Inject
    BillingDataDAO billingDataDAO;


    //@Async
    public void processDailyEnergyData(MeasuringTransmittingUnit mtu) {
        TimeZone timeZone = TimeZone.getTimeZone(userDetailsService.getTimezone());
        if (mtu.getTimezone() != null && !mtu.getTimezone().isEmpty()) {
            timeZone = TimeZone.getTimeZone(mtu.getTimezone());
        }
        LOGGER.debug("[processDailyEnergyData] Using timezone {}", timeZone.getDisplayName());

        boolean isGreater = (mtu.getLastPost().longValue() > mtu.getLastDayPost().longValue());
        //Make sure the clock has not jumped back giving us a weird timestamp (DST)
        LOGGER.debug("[processDailyEnergyData] isGreater:{}", isGreater);
        if (isGreater) {


            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(mtu.getLastDayPost() * 1000);
            int lastDay = calendar.get(Calendar.DAY_OF_YEAR);
            int lastYear = calendar.get(Calendar.YEAR);

            calendar.setTimeInMillis(mtu.getLastPost() * 1000);
            int today = calendar.get(Calendar.DAY_OF_YEAR);
            int thisYear = calendar.get(Calendar.YEAR);
            if (lastYear != thisYear) today+=365;


            boolean nextDay = (today > lastDay);
            LOGGER.debug("[processDailyEnergyData] nextDay:{} today:{} lastDay:{}", nextDay, today, lastDay);
            if (nextDay) {
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

                    //Create a date key in the format of YYYYMMDD using a 1-based index.
                    long date = calendar.get(Calendar.YEAR) * 10000;
                    date += (calendar.get(Calendar.MONTH)+1) * 100;;
                    date += calendar.get(Calendar.DATE);
                    dailyEnergyData.setEnergyDate(date);

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
        LOGGER.debug("[findByIdDate] mtu: {}, startDate:{}, endDate:{}", mtuId, startDate, endDate);

        List<DailyEnergyData>  dailyEnergyData = new ArrayList<>();
        if (startDate == null || endDate == null) {
             dailyEnergyData = dailyEnergyDataDAO.findByMtu(mtuId);
        } else {
            dailyEnergyData= dailyEnergyDataDAO.findByIdDate(mtuId,
                    FormatUtil.parseEnergyDateString(startDate),
                    FormatUtil.parseEnergyDateString(endDate));
        }
        LOGGER.trace("[findByIdDate] Found: {} records", dailyEnergyData.size());


        for (DailyEnergyData data: dailyEnergyData){
            data.setFormattedDate(FormatUtil.prettyFormatEnergyDate(data.getEnergyDate()));
            LOGGER.trace("[findByIdDate] Updating:{}", data);
        }

        return  dailyEnergyData;
    }

    public DailyEnergyData update(DailyEnergyData dailyEnergyData) {
        MeasuringTransmittingUnit mtu = measuringTransmittingUnitDAO.findOne(dailyEnergyData.getMtuId());
        if (mtu == null) mtu = measuringTransmittingUnitDAO.findByName(dailyEnergyData.getMtuId());
        if (mtu == null){
            LOGGER.warn("[update] Attempted to import a record for a non-existant mtu: {}", dailyEnergyData);
            return dailyEnergyData;
        }
        dailyEnergyData.setMtuId(mtu.getId());
        LOGGER.info("[update] Updating {}", dailyEnergyData);

        if (null == dailyEnergyDataDAO.findOne(dailyEnergyData.getMtuId(), dailyEnergyData.getEnergyDate())){
            LOGGER.info("[update] Creating new record: {}", dailyEnergyData);
            dailyEnergyDataDAO.insert(dailyEnergyData);
        } else {
            LOGGER.info("[update] Updating existing record: {}", dailyEnergyData);
            dailyEnergyDataDAO.update(dailyEnergyData);
        }


        return dailyEnergyData;
    }

    public void writeBillingCycleData(BillingFormParameters billingFormParameters, ServletOutputStream outputStream) {
        LOGGER.debug("[writeBillingCyleData] Generating Data for {}", billingFormParameters);
        billingDataDAO.exportBillingCycleData(billingFormParameters, outputStream);;

    }

    public void writeDailyData(BillingFormParameters billingFormParameters, ServletOutputStream outputStream) {
        LOGGER.debug("[writeDailyData] Generating Data for {}", billingFormParameters);
        try (PrintWriter p = new PrintWriter(outputStream)) {
            billingDataDAO.exportDailyData(billingFormParameters, outputStream);;
        } catch (Exception ex) {
            LOGGER.error("[writeDailyData] Error writing data for: {}", billingFormParameters, ex);
        }

    }


}
