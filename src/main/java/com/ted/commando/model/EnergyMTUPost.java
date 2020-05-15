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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * This is the MTU element grouping on a XML post from an ECC or MTU
 */
@XmlRootElement(name = "MTU")
public class EnergyMTUPost implements Serializable {

    private String mtuSerial;
    private int mtuTypeOrdinal;
    private List<EnergyCumulativePost> cumulativePostList;

    public EnergyMTUPost() {
    }

    @XmlAttribute(name = "ID")
    public String getMtuSerial() {
        return mtuSerial;
    }

    public void setMtuSerial(String mtuSerial) {
        this.mtuSerial = mtuSerial;
    }


    @XmlAttribute(name = "type")
    public int getMtuTypeOrdinal() {
        return mtuTypeOrdinal;
    }

    public void setMtuTypeOrdinal(int mtuTypeOrdinal) {
        this.mtuTypeOrdinal = mtuTypeOrdinal;
    }

    @XmlElement(name = "cumulative")
    public List<EnergyCumulativePost> getCumulativePostList() {
        return cumulativePostList;
    }

    public void setCumulativePostList(List<EnergyCumulativePost> cumulativePostList) {
        this.cumulativePostList = cumulativePostList;
    }

    @Override
    public String toString() {
        return "EnergyMTUPost{" +
                "mtuSerial='" + mtuSerial + '\'' +
                ", mtuTypeOrdinal=" + mtuTypeOrdinal +
                ", cumulativePostList=" + cumulativePostList +
                '}';
    }

    @JsonIgnore
    public boolean is5k() {
        return mtuSerial.startsWith("10");
    }
}