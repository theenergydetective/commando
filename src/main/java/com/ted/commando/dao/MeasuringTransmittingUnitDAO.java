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

import com.ted.commando.model.MeasuringTransmittingUnit;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MeasuringTransmittingUnitDAO extends SimpleAbstractDAO {

    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String[] FIELDS = {
            "id",
            "name",
            "timezone",
            "last_value",
            "last_post",
            "energy_rate",
            "last_day_value",
            "last_day_post",
            "enabled"
    };


    private static String BASE_QUERY = "select " + generateFields("m.", FIELDS, 0) + " from mtu m ";
    private static String FIND_ONE = BASE_QUERY + " where m.id = :id";


    private static String INSERT = "insert into mtu (" + generateFields("", FIELDS, 0) + ") VALUES (" + generateFields(":", FIELDS, 0) + ")";
    private static String UPDATE_SETTINGS = "update mtu set name=:name, energy_rate=:energy_rate,timezone=:timezone, enabled=:enabled  where id = :id";
    private static String UPDATE_LAST_POST = "update mtu set last_post=:last_post, last_value=:last_value  where id = :id";
    private static String UPDATE_LAST_DAY = "update mtu set last_day_post=:last_day_post, last_day_value=:last_day_value  where id = :id";
    private static String DELETE = "delete from mtu where id = :id";


    private RowMapper<MeasuringTransmittingUnit> rowMapper = new RowMapper<MeasuringTransmittingUnit>() {
        public MeasuringTransmittingUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
            MeasuringTransmittingUnit dto = new MeasuringTransmittingUnit();
            dto.setId(rs.getString("id"));
            dto.setLastPost(rs.getLong("last_post"));
            dto.setLastValue(rs.getBigDecimal("last_value"));
            dto.setName(rs.getString("name"));
            dto.setRate(rs.getBigDecimal("energy_rate"));
            dto.setLastDayPost(rs.getLong("last_day_post"));
            dto.setLastDayValue(rs.getBigDecimal("last_day_value"));
            dto.setTimezone(rs.getString("timezone"));
            dto.setEnabled(rs.getBoolean("enabled"));
            return dto;
        }
    };


    private MapSqlParameterSource createMap(MeasuringTransmittingUnit dto) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", dto.getId());
        map.addValue("last_post", dto.getLastPost());
        map.addValue("last_value", dto.getLastValue());
        map.addValue("name", dto.getName());
        map.addValue("energy_rate", dto.getRate());
        map.addValue("last_day_post", dto.getLastDayPost());
        map.addValue("last_day_value", dto.getLastDayValue());
        map.addValue("timezone", dto.getTimezone());
        map.addValue("enabled", dto.isEnabled());
        return map;
    }


    public MeasuringTransmittingUnit findOne(String id) {
        try {
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("id", id);
            return namedParameterJdbcTemplate.queryForObject(FIND_ONE, parameterSource, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }


    public synchronized MeasuringTransmittingUnit insert(MeasuringTransmittingUnit dto) {
        if (findOne(dto.getId()) != null) {
            updateLastPost(dto);
            updateSettings(dto);
            return dto;
        }
        LOGGER.debug("[insert] Inserting {}", dto);
        namedParameterJdbcTemplate.update(INSERT, createMap(dto));
        return dto;
    }

    public void updateLastPost(MeasuringTransmittingUnit dto) {
        LOGGER.debug("[updateLastPost] Updating {}", dto);
        namedParameterJdbcTemplate.update(UPDATE_LAST_POST, createMap(dto));
    }

    public void updateLastDayPost(MeasuringTransmittingUnit dto) {
        LOGGER.debug("[updateLastPost] Updating {}", dto);
        namedParameterJdbcTemplate.update(UPDATE_LAST_DAY, createMap(dto));
    }

    public void updateSettings(MeasuringTransmittingUnit dto) {
        LOGGER.debug("[updateLastPost] Updating {}", dto);
        namedParameterJdbcTemplate.update(UPDATE_SETTINGS, createMap(dto));
    }

    //Check if we need to process the batch
    public void delete(String id) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        namedParameterJdbcTemplate.update(DELETE, map);
    }

    public List<MeasuringTransmittingUnit> findAll(boolean enabled) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        if (enabled) query.append(" where enabled=1 ");
        query.append(" order by name asc ");
        return namedParameterJdbcTemplate.query(query.toString(),  rowMapper);
    }

}
