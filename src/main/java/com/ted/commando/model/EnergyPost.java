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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * This is the top level of the XML that gets posted to the server.
 * @author Pete Arvanitis (pete@petecode.com)
 */
@XmlRootElement(name = "ted5000")
public class EnergyPost implements Serializable {

    private String gateway;
    private String securityKey;
    private List<EnergyMTUPost> mtuList;
    private List<EnergyMTUPost> spyderList;
    private String version;

    @XmlAttribute(name = "GWID")
    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    @XmlAttribute(name = "ver", required = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute(name = "auth")
    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @XmlElement(name = "MTU")
    public List<EnergyMTUPost> getMtuList() {
        if (mtuList == null) mtuList = new ArrayList<EnergyMTUPost>();
        return mtuList;
    }

    public void setMtuList(List<EnergyMTUPost> mtuList) {
        this.mtuList = mtuList;
    }

    @XmlElement(name = "SPY")
    public List<EnergyMTUPost> getSpyderList() {
        if (spyderList == null) spyderList = new ArrayList<EnergyMTUPost>();
        return spyderList;
    }

    public void setSpyderList(List<EnergyMTUPost> spyderList) {
        this.spyderList = spyderList;
    }

    @Override
    public String toString() {
        return "EnergyPost{" +
                "gateway='" + gateway + '\'' +
                ", securityKey='" + securityKey + '\'' +
                ", mtuList=" + mtuList +
                ", spyderList=" + spyderList +
                '}';
    }

}