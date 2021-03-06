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
import java.util.Objects;

public class MeasuringTransmittingUnit {
    private String id;
    private String name;
    private BigDecimal rate = new BigDecimal(0.1);
    private BigDecimal lastValue = new BigDecimal(0L);
    private Long created = System.currentTimeMillis()/1000L;
    private Long lastPost = 0L;
    private BigDecimal lastDayValue = new BigDecimal(0L);
    private Long lastDayPost = 0L;
    private boolean enabled = true;
    private String timezone = "America/New_York";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getLastValue() {
        return lastValue;
    }

    public void setLastValue(BigDecimal lastValue) {
        this.lastValue = lastValue;
    }

    public Long getLastPost() {
        return lastPost;
    }

    public void setLastPost(Long lastPost) {
        this.lastPost = lastPost;
    }

    public BigDecimal getLastDayValue() {
        return lastDayValue;
    }

    public void setLastDayValue(BigDecimal lastDayValue) {
        this.lastDayValue = lastDayValue;
    }

    public Long getLastDayPost() {
        return lastDayPost;
    }

    public void setLastDayPost(Long lastDayPost) {
        this.lastDayPost = lastDayPost;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "MeasuringTransmittingUnit{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                ", lastValue=" + lastValue +
                ", created=" + created +
                ", lastPost=" + lastPost +
                ", lastDayValue=" + lastDayValue +
                ", lastDayPost=" + lastDayPost +
                ", enabled=" + enabled +
                ", timezone='" + timezone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasuringTransmittingUnit)) return false;
        MeasuringTransmittingUnit that = (MeasuringTransmittingUnit) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(lastValue, that.lastValue) &&
                Objects.equals(lastPost, that.lastPost) &&
                Objects.equals(lastDayValue, that.lastDayValue) &&
                Objects.equals(lastDayPost, that.lastDayPost) &&
                Objects.equals(timezone, that.timezone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, rate, lastValue, lastPost, lastDayValue, lastDayPost, timezone);
    }
}
