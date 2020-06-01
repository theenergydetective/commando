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

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

public class DayBillingDataCallbackHandler extends ExcelRowCallbackHandler implements RowCallbackHandler {
    final static Logger LOGGER = LoggerFactory.getLogger(DayBillingDataCallbackHandler.class);


    final BillingFormParameters billingFormParameters;
    final static String DAY_HEADER[] = {"Device Id", "Device Name", "Start Date", "End Date", "Usage (kWh)", "Cost"};
    final Date startDate;
    final Date endDate;

    DecimalFormat usageFormat = new DecimalFormat("0.0");
    DecimalFormat currencyFormat = new DecimalFormat("0.00");

    //Handle to the output stream
    final OutputStream outputStream;
    int rowNumber = 1;


    /**
     * Constructs a new callback
     *
     * @param billingFormParameters
     * @param outputStream
     *
     *
     */
    public DayBillingDataCallbackHandler(BillingFormParameters billingFormParameters, OutputStream outputStream) {
        super(DAY_HEADER);
        this.outputStream = outputStream;
        this.billingFormParameters = billingFormParameters;
        this.startDate = FormatUtil.convertEnergyDateToDate(billingFormParameters.getStartDate());
        this.endDate = FormatUtil.convertEnergyDateToDate(billingFormParameters.getEndDate());
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        //Create the DTO
        DayBillingData billingData = BillingDataDAO.DAY_ROW_MAPPER.mapRow(resultSet, 0);


        int columnNumber = 0;
        writeStringCell(rowNumber, columnNumber++, billingData.getId());
        writeStringCell(rowNumber, columnNumber++, billingData.getMtuName());
        writeDateCell(rowNumber, columnNumber++, startDate);
        writeDateCell(rowNumber, columnNumber++, endDate);
        writeUsageCell(rowNumber, columnNumber++, billingData.getKwhUsage());
        writeCostCell(rowNumber, columnNumber++, billingData.getKwhCost());
        rowNumber++;

    }
}

