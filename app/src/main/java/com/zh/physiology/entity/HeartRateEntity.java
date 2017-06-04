package com.zh.physiology.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * author：heng.zhang
 * date：2017/2/21
 * description：
 */
@Table(name = "heart_rate")
public class HeartRateEntity {
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
    private int value;

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HeartRate{" + "id=" + id + ", user='" + user + '\'' + ", year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", value=" + value + '}';
    }
}
