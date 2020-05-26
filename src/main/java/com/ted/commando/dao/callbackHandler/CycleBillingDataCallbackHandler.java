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
import com.ted.commando.model.CycleBillingData;
import com.ted.commando.model.DayBillingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class CycleBillingDataCallbackHandler implements RowCallbackHandler {
    final static Logger LOGGER = LoggerFactory.getLogger(CycleBillingDataCallbackHandler.class);

    final PrintWriter printWriter;
    final BillingFormParameters billingFormParameters;
    final static String CYCLE_HEADER = "Device Id, Device Name, Billing Cycle Month, Billing Cycle Year, Usage (kwh), KWH Cost";
    final static String MONTHS[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    DecimalFormat usageFormat = new DecimalFormat("0.0");
    DecimalFormat currencyFormat = new DecimalFormat("0.00");


    /**
     * Constructs a new callback
     *
     * @param billingFormParameters
     * @param printWriter
     */
    public CycleBillingDataCallbackHandler(BillingFormParameters billingFormParameters, PrintWriter printWriter) {
        this.printWriter = printWriter;
        this.billingFormParameters = billingFormParameters;
        LOGGER.debug("[constructor] Writing {}", CYCLE_HEADER);
        printWriter.println(CYCLE_HEADER);

    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //Create the DTO
        CycleBillingData billingData = BillingDataDAO.CYCLE_ROW_MAPPER.mapRow(resultSet, 0);

        String monthString = MONTHS[billingData.getBillingCycleMonth()-1];

        StringBuilder row = new StringBuilder(billingData.getId()).append(",")
                .append("\"").append(billingData.getMtuName()).append("\"").append(",")
                .append(monthString).append(",")
                .append(billingData.getBillingCycleYear()).append(",")
                .append(usageFormat.format(billingData.getKwhUsage())).append(",")
                .append(currencyFormat.format(billingData.getKwhCost()));
        LOGGER.debug("[processRow] Writing:{}", row);
        printWriter.println(row.toString());
    }
}

