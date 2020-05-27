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

package com.ted.commando.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Service for performing nightly backups of the database
 *
 * @author PeteArvanitis (pete@petecode.com)
 */

@Service
public class DatabaseBackupService {
    final static Logger LOGGER = LoggerFactory.getLogger(DatabaseBackupService.class);

    private static final String PROP_BACKUP_DIR = "spring.datasource.backup.directory";
    private static final String PROP_BACKUP_COUNT = "spring.datasource.backup.count";


    @Autowired
    Environment environment;

    @Autowired
    JdbcTemplate jdbcTemplate;


    protected void rollBackupFiles(String backupFile, int count){
        //Rename existing files with extensions
        for (int i=(count-1); i > 0; i--){
            String archivePath = backupFile + "." + i;
            String newArchivePath = backupFile + "." + (i+1);
            File archivedFile = new File(archivePath);
            if (archivedFile.exists()) {
                LOGGER.debug("[doBackup] Renaming {} to {}", archivePath, newArchivePath);
                archivedFile.renameTo(new File(newArchivePath));
            }
        }
        //Delete the max file if it exists
        File oldestFile = new File(backupFile + "." + count);
        if (oldestFile.exists()) {
            LOGGER.info("[doBackup] Deleting {}", oldestFile.getAbsolutePath());
            oldestFile.delete();
        }

        //Make a copy of the existing database
        File existingDB = new File(backupFile);
        if (existingDB.exists()) {
            existingDB.renameTo(new File(backupFile + ".1"));
        }
    }

    /**
     * Performas a backup of the database.
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void doBackup(){
        try {
            String backupDir = environment.getProperty(PROP_BACKUP_DIR);
            Integer backupCount = Integer.parseInt(environment.getProperty(PROP_BACKUP_COUNT));

            File directory = new File(backupDir);
            if (!directory.exists()) {
                LOGGER.info("[doBackup] Creating directory: {}", directory);
                directory.mkdirs();
            }

            String backupFile = backupDir;
            if (!backupFile.endsWith("/")) backupFile += "/";
            backupFile += "commando.db.bak";

            rollBackupFiles(backupFile, backupCount);

            LOGGER.info("[doBackup] Starting Database Backup: {}", directory.getAbsolutePath());
            jdbcTemplate.update("BACKUP TO '" + backupFile + "'");
        } catch (Exception ex){
            LOGGER.error("[doBackup] ERROR CREATING BACKUP: {}", ex);
        }
    }
}
