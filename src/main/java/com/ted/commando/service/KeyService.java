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

import java.security.SecureRandom;

@Service
public class KeyService {

    public static final char[] SECURITY_KEY_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9'};

    static final Logger LOGGER = LoggerFactory.getLogger(KeyService.class);


    /**
     * Generates a random series of letters and numbers for the given length.
     *
     * @param length
     * @return
     */
    public String generateSecurityKey(int length) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            key.append(SECURITY_KEY_CHARS[Math.abs(bytes[i]) % SECURITY_KEY_CHARS.length]);
        }
        return key.toString();
    }

}
