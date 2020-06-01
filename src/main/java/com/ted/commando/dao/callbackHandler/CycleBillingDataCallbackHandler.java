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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CycleBillingDataCallbackHandler extends ExcelRowCallbackHandler implements RowCallbackHandler {
    final static Logger LOGGER = LoggerFactory.getLogger(CycleBillingDataCallbackHandler.class);



    final BillingFormParameters billingFormParameters;
    final static String CYCLE_HEADER[] = {"Device Id", "Device Name", "Billing Cycle Month", "Billing Cycle Year", "Usage (kWh)", "Cost"};
    final static String MONTHS[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


    //Handle to the output stream
    final OutputStream outputStream;

    int rowNumber = 1;



    /**
     * Constructs a new callback
     *
     * @param billingFormParameters
     * @param outputStream
     */
    public CycleBillingDataCallbackHandler(BillingFormParameters billingFormParameters, OutputStream outputStream) {
        super(CYCLE_HEADER);
        this.outputStream = outputStream;
        this.billingFormParameters = billingFormParameters;
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //Create the DTO
        CycleBillingData billingData = BillingDataDAO.CYCLE_ROW_MAPPER.mapRow(resultSet, 0);

        String monthString = MONTHS[billingData.getBillingCycleMonth()-1];

        int columnNumber = 0;
        writeStringCell(rowNumber, columnNumber++, billingData.getId());
        writeStringCell(rowNumber, columnNumber++, billingData.getMtuName());
        writeStringCell(rowNumber, columnNumber++, monthString);
        writeIntegerCell(rowNumber, columnNumber++, billingData.getBillingCycleYear());
        writeUsageCell(rowNumber, columnNumber++, billingData.getKwhUsage());
        writeCostCell(rowNumber, columnNumber++, billingData.getKwhCost());
        rowNumber++;
    }
}

