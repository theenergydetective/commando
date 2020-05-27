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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseBackupServiceTest {


    @Mock
    Environment environment;

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    DatabaseBackupService databaseBackupService;

    private static final String PROP_BACKUP_DIR = "spring.datasource.backup.directory";
    private static final String PROP_BACKUP_COUNT = "spring.datasource.backup.count";
    private static final String TEST_DIRECTORY = "/opt/testDirectory";
    private static final String TEST_BACKUP_FILE = "/opt/testDirectory/commando.db.bak";



    @Before
    public void setup() throws Exception{
        reset(jdbcTemplate);
        reset(environment);
        deleteDirectoryRecursion(new File(TEST_DIRECTORY).toPath());
    }

    @After
    public void cleanup() throws Exception{
       deleteDirectoryRecursion(new File(TEST_DIRECTORY).toPath());
    }


    void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    @Test
    public void doBackupTest() throws Exception{
        when(environment.getProperty(PROP_BACKUP_DIR)).thenReturn(TEST_DIRECTORY);
        when(environment.getProperty(PROP_BACKUP_COUNT)).thenReturn("3");

        //Test that the command is called
        databaseBackupService.doBackup();
        verify(jdbcTemplate).update("BACKUP TO '" + TEST_BACKUP_FILE + "'");


        //Test the rolling file backup
        touch(new File(TEST_BACKUP_FILE));
        databaseBackupService.doBackup();
        assertTrue(new File(TEST_BACKUP_FILE + ".1").exists());
        touch(new File(TEST_BACKUP_FILE));
        databaseBackupService.doBackup();
        touch(new File(TEST_BACKUP_FILE));
        databaseBackupService.doBackup();
        assertTrue(new File(TEST_BACKUP_FILE + ".1").exists());
        assertTrue(new File(TEST_BACKUP_FILE + ".2").exists());
        assertFalse(new File(TEST_BACKUP_FILE + ".3").exists());


    }


    public static void touch(File file) throws IOException{
        long timestamp = System.currentTimeMillis();
        touch(file, timestamp);
    }

    public static void touch(File file, long timestamp) throws IOException{
        if (!file.exists()) {
            new FileOutputStream(file).close();
        }

        file.setLastModified(timestamp);
    }


}
