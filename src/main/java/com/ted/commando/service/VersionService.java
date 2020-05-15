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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Simple service to pull the version from the git commit
 *
 * @author Pete Arvanitis (pete@petecode.com)
 */

@Service
public class VersionService {
    final static Logger LOGGER = LoggerFactory.getLogger(VersionService.class);

    private String version;


    @PostConstruct
    public void init() {
        version = readGitProperties();
        LOGGER.info("[init] Server Version: {}", version);
    }

    public String getVersion() {
        return version;
    }

    private String readGitProperties() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("git.properties");
        try {
            return readFromInputStream(inputStream);
        } catch (Exception e) {
            return "Version information could not be retrieved";
        }
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {

        if (inputStream == null) {
            return "Version information could not be retrieved";
        }
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


}
