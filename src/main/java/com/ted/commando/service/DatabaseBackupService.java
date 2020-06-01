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


import com.ted.commando.dao.DailyEnergyDataDAO;
import com.ted.commando.dao.MeasuringTransmittingUnitDAO;
import com.ted.commando.model.DailyEnergyData;
import com.ted.commando.model.MeasuringTransmittingUnit;
import com.ted.commando.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

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

    @Inject
    DailyEnergyDataDAO dailyEnergyDataDAO;

    @Autowired
    MeasuringTransmittingUnitDAO measuringTransmittingUnitDAO;


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


    /**
     * This imports data from a csv exported from a TED Pro ECC. It requires that the name of the device EXACTLY MATCH
     * the name of the exported Spyder/MTU.
     * @param csvFile
     */
    public void import6KCSV(File csvFile){
        LOGGER.debug("[import6KCSV] Opening File: {}");
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))){
            String line = reader.readLine();
            while (line != null){
                if (!line.trim().isEmpty()) {
                    LOGGER.debug("[import6K] Importing {}", line);
                    String fields[] = line.split(",");


                    MeasuringTransmittingUnit mtu = measuringTransmittingUnitDAO.findByName(fields[0]);
                    if (mtu != null) {
                        DailyEnergyData dailyEnergyData = new DailyEnergyData();
                        dailyEnergyData.setMtuId(mtu.getId());
                        dailyEnergyData.setEnergyDate(FormatUtil.convertSimpleDateToEnergyDate(fields[1]));
                        dailyEnergyData.setEnergyValue(new BigDecimal(fields[2]).multiply(new BigDecimal(1000.0)));

                        if (null == dailyEnergyDataDAO.findOne(mtu.getId(), dailyEnergyData.getEnergyDate())) {
                            LOGGER.debug("[import6K] Inserting new record: {}", dailyEnergyData);
                            dailyEnergyDataDAO.insert(dailyEnergyData);
                        } else {
                            LOGGER.debug("[import6K] Overwriting existing record: {}", dailyEnergyData);
                            dailyEnergyDataDAO.update(dailyEnergyData);
                        }

                    }
                }
                line = reader.readLine();
            }


        } catch (IOException e) {
            LOGGER.error("[import6KCSV] Error importing {}", csvFile, e);
        }
    }
}
