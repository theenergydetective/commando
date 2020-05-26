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

@JsonIgnoreProperties(ignoreUnknown = true)
public class CycleBillingData {
    String id;
    String mtuName;
    int billingCycleMonth;
    int billingCycleYear;
    BigDecimal kwhUsage;
    BigDecimal kwhCost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMtuName() {
        return mtuName;
    }

    public void setMtuName(String mtuName) {
        this.mtuName = mtuName;
    }

    public int getBillingCycleMonth() {
        return billingCycleMonth;
    }

    public void setBillingCycleMonth(int billingCycleMonth) {
        this.billingCycleMonth = billingCycleMonth;
    }

    public int getBillingCycleYear() {
        return billingCycleYear;
    }

    public void setBillingCycleYear(int billingCycleYear) {
        this.billingCycleYear = billingCycleYear;
    }

    public BigDecimal getKwhUsage() {
        return kwhUsage;
    }

    public void setKwhUsage(BigDecimal kwhUsage) {
        this.kwhUsage = kwhUsage;
    }

    public BigDecimal getKwhCost() {
        return kwhCost;
    }

    public void setKwhCost(BigDecimal kwhCost) {
        this.kwhCost = kwhCost;
    }
}
