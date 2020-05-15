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

import com.ted.commando.model.EnergyControlCenter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class EnergyControlCenterDAO extends SimpleAbstractDAO {

    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public static final String[] FIELDS = {
            "id",
            "security_key",
            "version"
    };


    private static String BASE_QUERY = "select " + generateFields("e.", FIELDS, 0) + " from ecc e ";
    private static String FIND_ONE = BASE_QUERY + " where e.id = :id";

    private static String INSERT = "insert into ecc (" + generateFields("", FIELDS, 0) + ") VALUES (" + generateFields(":", FIELDS, 0) + ")";
    private static String UPDATE = "update ecc set " + generateSetFields(FIELDS, 1) + " where id = :id";
    private static String DELETE = "delete from ecc where id = :id";


    private RowMapper<EnergyControlCenter> rowMapper = new RowMapper<EnergyControlCenter>() {
        public EnergyControlCenter mapRow(ResultSet rs, int rowNum) throws SQLException {
            EnergyControlCenter dto = new EnergyControlCenter();
            dto.setId(rs.getString("id"));
            dto.setSecurityKey(rs.getString("security_key"));
            dto.setVersion(rs.getString("version"));
            return dto;
        }
    };


    private MapSqlParameterSource createMap(EnergyControlCenter dto) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", dto.getId());
        map.addValue("security_key", dto.getSecurityKey());
        map.addValue("version", dto.getVersion());
        return map;
    }


    public EnergyControlCenter findOne(String id) {
        try {
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("id", id);
            return namedParameterJdbcTemplate.queryForObject(FIND_ONE, parameterSource, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }


    public synchronized EnergyControlCenter insert(EnergyControlCenter dto) {
        if (findOne(dto.getId()) != null) {
            update(dto);
            return dto;
        }

        LOGGER.debug("[insert] Inserting {}", dto);
        namedParameterJdbcTemplate.update(INSERT, createMap(dto));
        return dto;
    }

    public void update(EnergyControlCenter dto) {
        LOGGER.debug("[update] Updating {}", dto);
        namedParameterJdbcTemplate.update(UPDATE, createMap(dto));
    }

    //Check if we need to process the batch
    public void delete(String id) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        namedParameterJdbcTemplate.update(DELETE, map);
    }

}
