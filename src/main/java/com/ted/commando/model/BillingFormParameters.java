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
    private String startDate;
    private String endDate;
    private Integer meterReadDate;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getSelectedDevices() {
        return selectedDevices;
    }

    public void setSelectedDevices(List<String> selectedDevices) {
        this.selectedDevices = selectedDevices;
    }

    public Integer getMeterReadDate() {
        return meterReadDate;
    }

    public void setMeterReadDate(Integer meterReadDate) {
        this.meterReadDate = meterReadDate;
    }

    @Override
    public String toString() {
        return "BillingFormParameters{" +
                "accessToken='" + accessToken + '\'' +
                ", exportType=" + exportType +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", meterReadDate=" + meterReadDate +
                ", selectedDevices=" + selectedDevices +
                '}';
    }
}
