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
import com.ted.commando.enums.ExportType;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingFormParameters {
    private String accessToken;
    private ExportType exportType;
    private Integer billingCycleStart;
    private MonthYearRange range;
    private List<String> selectedDevices = new ArrayList<>();

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ExportType getExportType() {
        return exportType;
    }

    public void setExportType(ExportType exportType) {
        this.exportType = exportType;
    }

    public Integer getBillingCycleStart() {
        return billingCycleStart;
    }

    public void setBillingCycleStart(Integer billingCycleStart) {
        this.billingCycleStart = billingCycleStart;
    }

    public MonthYearRange getRange() {
        return range;
    }

    public void setRange(MonthYearRange range) {
        this.range = range;
    }

    public List<String> getSelectedDevices() {
        return selectedDevices;
    }

    public void setSelectedDevices(List<String> selectedDevices) {
        this.selectedDevices = selectedDevices;
    }

    @Override
    public String toString() {
        return "BillingFormParameters{" +
                "accessToken='" + accessToken + '\'' +
                ", exportType=" + exportType +
                ", billingCycleStart=" + billingCycleStart +
                ", range=" + range +
                ", selectedDevices=" + selectedDevices +
                '}';
    }
}
