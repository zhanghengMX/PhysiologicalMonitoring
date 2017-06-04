package com.zh.physiology.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * author：heng.zhang
 * date：2017/2/21
 * description：
 */
@Table(name = "temperature")
public class TemperatureEntity {
    @Column(name="id", isId=true, autoGen=true)
    private int id;

    @Column(name="user")
    private String user;

    @Column(name="year")
    private int year;

    @Column(name="month")
    private int month;

    @Column(name="day")
    private int day;

    @Column(name="hour")
    private int hour;

    @Column(name="value")
    private float value;

    @Override
    public String toString() {
        return "Temperature{" + "id=" + id + ", user='" + user + '\'' + ", year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour  + ", value=" + value + '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
