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

package com.ted.commando.dao;

import com.ted.commando.dao.callbackHandler.CycleBillingDataCallbackHandler;
import com.ted.commando.dao.callbackHandler.DayBillingDataCallbackHandler;
import com.ted.commando.model.BillingFormParameters;
import com.ted.commando.model.CycleBillingData;
import com.ted.commando.model.DayBillingData;
import com.ted.commando.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class BillingDataDAO extends SimpleAbstractDAO {
    final static Logger LOGGER = LoggerFactory.getLogger(BillingDataDAO.class);



    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static String DAY_QUERY = "select  m.id, " +
            "       m.name, " +
            "       m.ENERGY_RATE, " +
            "       sum(ed.ENERGY_VALUE) as usage " +
            "from DAILY_ENERGY_DATA ed " +
            "join MTU m on m.id = ed.mtu_id " +
            "where ed.ENERGY_DATE >= :start_date " +
            "and ed.ENERGY_DATE <= :end_date " +
            "and ed.MTU_ID in (:mtuList) " +
            "group by m.id, m.name, m.ENERGY_RATE " +
            "order by m.name";


    public static String CYCLE_QUERY = "select id, name, ENERGY_RATE, month, year, sum(usage) as usage " +
            "from ( " +
            "select m.id, " +
            "       m.name, " +
            "       m.ENERGY_RATE, " +
            "       BILLINGCYCLEMONTH(ENERGY_DATE, :meter_read_date) as month, " +
            "       BILLINGCYCLEYEAR(ENERGY_DATE, :meter_read_date) as year, " +
            "       ENERGY_VALUE as usage " +
            "from DAILY_ENERGY_DATA d " +
            "join MTU m on m.ID = d.MTU_ID " +
            "where BILLINGCYCLEKEY(ENERGY_DATE, :meter_read_date)  >= :start_date " +
            "  and BILLINGCYCLEKEY(ENERGY_DATE, :meter_read_date)  <= :end_date " +
            "  and MTU_ID in (:mtuList)) q " +
            "group by id, name, month, year, ENERGY_RATE " +
            "order by name, year, month";


    public static RowMapper<DayBillingData> DAY_ROW_MAPPER = new RowMapper<DayBillingData>() {
        public DayBillingData mapRow(ResultSet rs, int rowNum) throws SQLException {
            BigDecimal rate = rs.getBigDecimal("energy_rate");

            DayBillingData dto = new DayBillingData();
            dto.setId(rs.getString("id"));
            dto.setMtuName(rs.getString("name"));

            BigDecimal usage = rs.getBigDecimal("usage").divide(new BigDecimal(1000.0));
            dto.setKwhUsage(usage);
            dto.setKwhCost(usage.multiply(rate));
            return dto;
        }
    };

    public static RowMapper<CycleBillingData> CYCLE_ROW_MAPPER = new RowMapper<CycleBillingData>() {
        public CycleBillingData mapRow(ResultSet rs, int rowNum) throws SQLException {
            BigDecimal rate = rs.getBigDecimal("energy_rate");

            CycleBillingData dto = new CycleBillingData();
            dto.setId(rs.getString("id"));
            dto.setMtuName(rs.getString("name"));
            dto.setBillingCycleMonth(rs.getInt("month"));
            dto.setBillingCycleYear(rs.getInt("year"));
            BigDecimal usage = rs.getBigDecimal("usage").divide(new BigDecimal(1000.0));
            dto.setKwhUsage(usage);
            dto.setKwhCost(usage.multiply(rate));
            return dto;
        }
    };


    public void exportDailyData(BillingFormParameters billingFormParameters, PrintWriter printWriter){
        DayBillingDataCallbackHandler callbackHandler = new DayBillingDataCallbackHandler(billingFormParameters, printWriter);
        MapSqlParameterSource map = new MapSqlParameterSource();
        Long startDate = FormatUtil.parseEnergyDateString(billingFormParameters.getStartDate());
        Long endDate = FormatUtil.parseEnergyDateString(billingFormParameters.getEndDate());
        map.addValue("mtuList", billingFormParameters.getSelectedDevices());
        map.addValue("start_date", startDate);
        map.addValue("end_date", endDate);
        namedParameterJdbcTemplate.query(DAY_QUERY, map, callbackHandler);
    }


    public void exportBillingCycleData(BillingFormParameters billingFormParameters, OutputStream outputStream) {
        CycleBillingDataCallbackHandler callbackHandler = new CycleBillingDataCallbackHandler(billingFormParameters, outputStream);
        MapSqlParameterSource map = new MapSqlParameterSource();
        Long startDate = FormatUtil.parseCycleDateString(billingFormParameters.getStartDate());
        Long endDate = FormatUtil.parseCycleDateString(billingFormParameters.getEndDate());
        map.addValue("mtuList", billingFormParameters.getSelectedDevices());
        map.addValue("meter_read_date", billingFormParameters.getMeterReadDate());
        map.addValue("start_date", startDate);
        map.addValue("end_date", endDate);
        namedParameterJdbcTemplate.query(CYCLE_QUERY, map, callbackHandler);
        callbackHandler.writeToOutputStream(outputStream);

    }
}
