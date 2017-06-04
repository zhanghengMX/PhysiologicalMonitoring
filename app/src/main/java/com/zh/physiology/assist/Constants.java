package com.zh.physiology.assist;

import java.util.UUID;
/**
 * author：heng.zhang
 * date：2016/12/23
 * description：
 */
public class Constants {
    public static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;

    public static final String USER = "0";
    public static final String MARK_HEAT = "1";
    public static final String MARK_HIGH_PRESSURE = "2";
    public static final String MARK_LOW_PRESSURE = "3";
    public static final String MARK_HEART_RATE = "4";
    public static final String MARK_HEART_SOUNDS = "5";

    public static final int BLUETOOTH_CONNECTED = 100;
}
