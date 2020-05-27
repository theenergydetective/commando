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

package com.ted.commando.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "ted5000ActivationResponse")
public class ActivationResponse {
    @XmlElement
    public String PostServer;

    @XmlElement
    public String UseSSL = "F";

    @XmlElement
    public int PostPort = 80;

    @XmlElement
    public String PostURL = "/api/postData";

    @XmlElement
    public String AuthToken;

    @XmlElement
    public int PostRate = 1;

    @XmlElement
    public String HighPrec = "T";

    @XmlElement
    public String Spyder = "T";

    @Override
    public String toString() {
        return "ActivationResponse{" +
                "PostServer='" + PostServer + '\'' +
                ", UseSSL='" + UseSSL + '\'' +
                ", PostPort=" + PostPort +
                ", PostURL='" + PostURL + '\'' +
                ", AuthToken='" + AuthToken + '\'' +
                ", PostRate=" + PostRate +
                ", HighPrec='" + HighPrec + '\'' +
                ", Spyder='" + Spyder + '\'' +
                '}';
    }
}

