package com.gvozditskiy.watermeter;

/**
 * Created by Alexey on 24.12.2016.
 */

public class Indication {
    private int year;
    private int month;
    private int value;
    private String meterUuid;
    private String uuid;

    public Indication() {
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
