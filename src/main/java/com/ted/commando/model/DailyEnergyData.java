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

import java.math.BigDecimal;

public class DailyEnergyData {
    private String mtuId;
    private Long epochDate;
    private BigDecimal energyValue;

    public DailyEnergyData() {
    }

    public DailyEnergyData(String mtuId, Long epochDate, BigDecimal energyValue) {
        this.mtuId = mtuId;
        this.epochDate = epochDate;
        this.energyValue = energyValue;
    }

    public String getMtuId() {
        return mtuId;
    }

    public void setMtuId(String mtuId) {
        this.mtuId = mtuId;
    }

    public Long getEpochDate() {
        return epochDate;
    }

    public void setEpochDate(Long epochDate) {
        this.epochDate = epochDate;
    }

    public BigDecimal getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(BigDecimal energyValue) {
        this.energyValue = energyValue;
    }

    @Override
    public String toString() {
        return "DailyEnergyData{" +
                "mtuId='" + mtuId + '\'' +
                ", epochDate=" + epochDate +
                ", energyValue=" + energyValue +
                '}';
    }
}
