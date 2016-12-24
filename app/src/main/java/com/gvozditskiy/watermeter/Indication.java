package com.gvozditskiy.watermeter;

/**
 * Created by Alexey on 24.12.2016.
 */

public class Indication {
    private int year;
    private int month;
    private int cold;
    private int hot;

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

    public int getCold() {
        return cold;
    }

    public void setCold(int cold) {
        this.cold = cold;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }
}
