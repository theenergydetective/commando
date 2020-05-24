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

CREATE TABLE IF NOT EXISTS db_version(
  version INT NOT NULL DEFAULT 0,
  PRIMARY KEY (version));

CREATE TABLE IF NOT EXISTS ecc (
    id varchar(45)  NOT NULL,
    security_key varchar(45) DEFAULT NULL,
    version varchar(45) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS mtu (
    id varchar(45)  NOT NULL,
    name varchar(45)  NOT NULL,
    timezone varchar(45)  NOT NULL,
    last_value decimal(32,4) unsigned NOT NULL DEFAULT '0',
    last_post bigint(20) unsigned NOT NULL DEFAULT '0',
    energy_rate decimal(22,19) unsigned NOT NULL DEFAULT '0.10',
    last_day_value decimal(32,4) unsigned NOT NULL DEFAULT '0',
    last_day_post bigint(20) unsigned NOT NULL DEFAULT '0',
    enabled tinyint(1) unsigned NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS daily_energy_data (
    mtu_id varchar(45)  NOT NULL,
    epoch_date bigint(20) unsigned NOT NULL DEFAULT '0',
    energy_value decimal(32,4) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (mtu_id,epoch_date)
);



