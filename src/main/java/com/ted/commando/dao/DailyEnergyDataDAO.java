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

import com.ted.commando.model.DailyEnergyData;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DailyEnergyDataDAO extends SimpleAbstractDAO {

    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String[] FIELDS = {
            "mtu_id",
            "energy_date",
            "energy_value"
    };

    private static String BASE_QUERY = "select " + generateFields("ed.", FIELDS, 0) + " from daily_energy_data ed ";
    private static String FIND_ONE = BASE_QUERY + " where ed.mtu_id=:mtu_id and ed.energy_date=:energy_date";
    private static String FIND_BY_MTU = BASE_QUERY + " where mtu_id = :mtu_id order by energy_date desc";
    private static String FIND_BY_MTU_DATE = BASE_QUERY + " where mtu_id = :mtu_id and energy_date >= :start_date and energy_date < :end_date order by energy_date desc";

    private static String FIND_TOTAL_ENERGY = "select sum(energy_value) from daily_energy_data where mtu_id = :mtu_id and energy_date >= :start and energy_date < :end";

    private static String INSERT = "insert into daily_energy_data (" + generateFields("", FIELDS, 0) + ") VALUES (" + generateFields(":", FIELDS, 0) + ")";
    private static String UPDATE = "update daily_energy_data set energy_value=:energy_value where mtu_id=:mtu_id and energy_date=:energy_date";

    public static RowMapper<DailyEnergyData> ROW_MAPPER = new RowMapper<DailyEnergyData>() {
        public DailyEnergyData mapRow(ResultSet rs, int rowNum) throws SQLException {
            DailyEnergyData dto = new DailyEnergyData();
            dto.setMtuId(rs.getString("mtu_id"));
            dto.setEnergyDate(rs.getLong("energy_date"));
            dto.setEnergyValue(rs.getBigDecimal("energy_value"));
            return dto;
        }
    };

    private MapSqlParameterSource createMap(DailyEnergyData dto) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("mtu_id", dto.getMtuId());
        map.addValue("energy_date", dto.getEnergyDate());
        map.addValue("energy_value", dto.getEnergyValue());
        return map;
    }


    public List<DailyEnergyData> findByMtu(String mtuId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("mtu_id", mtuId);
        return namedParameterJdbcTemplate.query(FIND_BY_MTU, parameterSource, ROW_MAPPER);
    }

    /***
     * Returns the total usage beginning with the start date up to but not including the end date
     * @param mtuId
     * @param startEnergyDate YYYYMMDD format
     * @param endEnergyDate YYYYMMDD format
     * @return
     */
    public BigDecimal sumTotalEnergy(String mtuId, long startEnergyDate, long endEnergyDate) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("mtu_id", mtuId);
        parameterSource.addValue("start", startEnergyDate);
        parameterSource.addValue("end", endEnergyDate);
        return namedParameterJdbcTemplate.queryForObject(FIND_TOTAL_ENERGY, parameterSource, BigDecimal.class);
    }

    /**
     * Returns a single energy data record.
     *
     * @param mtuId
     * @param energyDate
     * @return
     */
    public DailyEnergyData findOne(String mtuId, Long energyDate) {
        try {
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("mtu_id", mtuId);
            parameterSource.addValue("energy_date", energyDate);
            return namedParameterJdbcTemplate.queryForObject(FIND_ONE, parameterSource, ROW_MAPPER);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public synchronized DailyEnergyData insert(DailyEnergyData dto) {
        if (null != findOne(dto.getMtuId(), dto.getEnergyDate())) {
            LOGGER.debug("[insert] Already exists: {}", dto);
            update(dto);
        } else {
            LOGGER.debug("[insert] Inserting {}", dto);
            namedParameterJdbcTemplate.update(INSERT, createMap(dto));

        }
        return dto;
    }

    public void update(DailyEnergyData dto) {
        LOGGER.debug("[update] Updating {}", dto);
        namedParameterJdbcTemplate.update(UPDATE, createMap(dto));
    }

    public List<DailyEnergyData> findByIdDate(String mtuId, Long startDate, Long endDate) {
        LOGGER.debug("[findByIdDate] mtuId:{} startDate:{} endDate:{}", mtuId, startDate, endDate);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("mtu_id", mtuId);
        parameterSource.addValue("start_date", startDate);
        parameterSource.addValue("end_date", endDate);
        return namedParameterJdbcTemplate.query(FIND_BY_MTU_DATE, parameterSource, ROW_MAPPER);
    }


}
