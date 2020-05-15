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
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/*
 * This is a single energy reading from the ECC/MTU
 * @author Pete Arvanitis (pete@petecode.com)
 */

@XmlRootElement(name = "cumulative")
public class EnergyCumulativePost implements Serializable {


    private long timestamp;
    private Double watts;
    private Double powerFactor;
    private Double voltage;

    public EnergyCumulativePost() {
    }

    ;

    public EnergyCumulativePost(long timestamp, Double watts, Double powerFactor, Double voltage) {
        this.timestamp = timestamp;
        this.watts = watts;
        this.powerFactor = powerFactor;
        this.voltage = voltage;
    }

    @XmlAttribute(name = "timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @XmlAttribute(name = "watts")
    public Double getWatts() {
        return watts;
    }

    public void setWatts(Double watts) {
        this.watts = watts;
    }

    @XmlAttribute(name = "pf")
    public Double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(Double powerFactor) {
        this.powerFactor = powerFactor;
    }

    @XmlAttribute(name = "voltage")
    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    @Override
    public String toString() {
        return "EnergyCumulativePost{" +
                "timestamp=" + timestamp +
                ", watts=" + watts +
                ", powerFactor=" + powerFactor +
                ", voltage=" + voltage +
                '}';
    }
}