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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Abstract utility methods for the DAOs.
 */
@Repository
public abstract class SimpleAbstractDAO {

    static protected Logger LOGGER = LoggerFactory.getLogger(SimpleAbstractDAO.class);

    protected static String generateFields(String prefix, String[] fields, int offset) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = offset; i < fields.length; i++) {
            String field = fields[i];
            if (stringBuilder.length() != 0) stringBuilder.append(", ");
            stringBuilder.append(prefix).append(field);
        }
        return stringBuilder.toString();
    }

    protected static String generateSetFields(String[] fields, int offset) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = offset; i < fields.length; i++) {
            String field = fields[i];
            if (stringBuilder.length() != 0) stringBuilder.append(", ");
            stringBuilder.append(field).append("=:").append(field);
        }
        return stringBuilder.toString();
    }


}
