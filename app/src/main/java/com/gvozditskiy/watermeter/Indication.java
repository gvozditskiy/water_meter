package com.gvozditskiy.watermeter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Alexey on 24.12.2016.
 */

public class Indication implements Serializable {
    private int year;
    private int month;
    private int value;
    private String meterUuid;
    private UUID uuid;

    public Indication() {
        uuid = UUID.randomUUID();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMeterUuid() {
        return meterUuid;
    }

    public void setMeterUuid(String meterUuid) {
        this.meterUuid = meterUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
