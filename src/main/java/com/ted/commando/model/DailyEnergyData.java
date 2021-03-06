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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyEnergyData {
    private String mtuId;
    private Long energyDate;
    private BigDecimal energyValue;
    private String formattedDate;

    public DailyEnergyData() {
    }

    public DailyEnergyData(String mtuId, Long energyDate, BigDecimal energyValue) {
        this.mtuId = mtuId;
        this.energyDate = energyDate;
        this.energyValue = energyValue;
    }

    public String getMtuId() {
        return mtuId;
    }

    public void setMtuId(String mtuId) {
        this.mtuId = mtuId;
    }

    public Long getEnergyDate() {
        return energyDate;
    }

    public void setEnergyDate(Long energyDate) {
        this.energyDate = energyDate;
    }

    public BigDecimal getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(BigDecimal energyValue) {
        this.energyValue = energyValue;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyEnergyData)) return false;
        DailyEnergyData that = (DailyEnergyData) o;
        return Objects.equals(mtuId, that.mtuId) &&
                Objects.equals(energyDate, that.energyDate) &&
                Objects.equals(energyValue, that.energyValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mtuId, energyDate, energyValue);
    }

    @Override
    public String toString() {
        return "DailyEnergyData{" +
                "mtuId='" + mtuId + '\'' +
                ", epochDate=" + energyDate +
                ", energyValue=" + energyValue +
                '}';
    }
}
