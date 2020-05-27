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

package com.ted.commando.dao.callbackHandler;

import com.ted.commando.dao.BillingDataDAO;
import com.ted.commando.model.BillingFormParameters;
import com.ted.commando.model.DayBillingData;
import com.ted.commando.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class DayBillingDataCallbackHandler implements RowCallbackHandler {
    final static Logger LOGGER = LoggerFactory.getLogger(DayBillingDataCallbackHandler.class);

    final PrintWriter printWriter;
    final BillingFormParameters billingFormParameters;
    final static String DAY_HEADER = "Device Id, Device Name, Start Date, End Date, Usage (kwh), KWH Cost";
    final String startDate;
    final String endDate;

    DecimalFormat usageFormat = new DecimalFormat("0.0");
    DecimalFormat currencyFormat = new DecimalFormat("0.00");


    /**
     * Constructs a new callback
     *
     * @param billingFormParameters
     * @param printWriter
     */
    public DayBillingDataCallbackHandler(BillingFormParameters billingFormParameters, PrintWriter printWriter) {
        this.printWriter = printWriter;
        this.billingFormParameters = billingFormParameters;
        this.startDate = FormatUtil.simpleFormatEnergyDate(billingFormParameters.getStartDate());
        this.endDate = FormatUtil.simpleFormatEnergyDate(billingFormParameters.getEndDate());
        LOGGER.debug("[constructor] Writing {}", DAY_HEADER);
        printWriter.println(DAY_HEADER);

    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //Create the DTO
        DayBillingData billingData = BillingDataDAO.DAY_ROW_MAPPER.mapRow(resultSet, 0);

        StringBuilder row = new StringBuilder(billingData.getId()).append(",")
                .append("\"").append(billingData.getMtuName()).append("\"").append(",")
                .append(startDate).append(",")
                .append(endDate).append(",")
                .append(usageFormat.format(billingData.getKwhUsage())).append(",")
                .append(currencyFormat.format(billingData.getKwhCost()));
        LOGGER.debug("[processRow] Writing:{}", row);
        printWriter.println(row.toString());
    }
}
