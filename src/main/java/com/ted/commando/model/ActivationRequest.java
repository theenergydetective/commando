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


@XmlRootElement(name = "ted5000Activation")
public class ActivationRequest {

    @XmlElement(name = "Gateway")
    public String gateway;

    @XmlElement(name = "Unique")
    public String unique;

    @Override
    public String toString() {
        return "Ted5000Activation{" +
                "gateway='" + gateway + '\'' +
                ", unique='" + unique + '\'' +
                '}';
    }

}

