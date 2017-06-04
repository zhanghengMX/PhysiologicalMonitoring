package com.zh.physiology.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * author：heng.zhang
 * date：2017/2/21
 * description：
 */
@Table(name = "blood_pressure")
public class BloodPressureEntity {
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

    @Column(name="high")
    private int high;

    @Column(name="low")
    private int low;

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

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    @Override
    public String toString() {
        return "BloodPressure{" + "id=" + id + ", user='" + user + '\'' + ", year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", high=" + high + ", low=" + low + '}';
    }
}
