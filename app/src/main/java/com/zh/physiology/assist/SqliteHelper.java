package com.zh.physiology.assist;

import android.util.Log;

import com.zh.physiology.entity.BloodPressureEntity;
import com.zh.physiology.entity.HeartRateEntity;
import com.zh.physiology.entity.TemperatureEntity;
import com.zh.physiology.entity.UserEntity;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * author：heng.zhang
 * date：2017/2/21
 * description：
 */
public class SqliteHelper {
    private static MyApp myApp = new MyApp();
    static final DbManager db = x.getDb(myApp.getDaoConfig());
    static Calendar calendar = Calendar.getInstance(Locale.CHINA);
    public static void saveTemperature(String user, float temperature) {
        if(temperature <= 34.0 || temperature > 43.0) {
            return;
        }
        deleteSameDayData(TemperatureEntity.class);
        TemperatureEntity temperatureEntity = new TemperatureEntity();
        temperatureEntity.setUser(user);
        temperatureEntity.setValue(temperature);
        temperatureEntity.setYear(calendar.get(Calendar.YEAR));
        temperatureEntity.setMonth(calendar.get(Calendar.MONTH)+1);
        temperatureEntity.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        temperatureEntity.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        try {
            db.save(temperatureEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveBloodPressure(String user, int high, int low) {
        if(high <= 70 || high > 250 || low < 40 || low > 150) {
            return;
        }
        deleteSameDayData(BloodPressureEntity.class);
        BloodPressureEntity bloodPressureEntity = new BloodPressureEntity();
        bloodPressureEntity.setUser(user);
        bloodPressureEntity.setHigh(high);
        bloodPressureEntity.setLow(low);
        bloodPressureEntity.setYear(calendar.get(Calendar.YEAR));
        bloodPressureEntity.setMonth(calendar.get(Calendar.MONTH)+1);
        bloodPressureEntity.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        bloodPressureEntity.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        try {
            db.save(bloodPressureEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveHeartRate(String user, int heartRate) {
        if(heartRate < 40 || heartRate > 200) {
            return;
        }
        deleteSameDayData(HeartRateEntity.class);
        HeartRateEntity heartRateEntity = new HeartRateEntity();
        heartRateEntity.setUser(user);
        heartRateEntity.setValue(heartRate);
        heartRateEntity.setYear(calendar.get(Calendar.YEAR));
        heartRateEntity.setMonth(calendar.get(Calendar.MONTH)+1);
        heartRateEntity.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        heartRateEntity.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        try {
            db.save(heartRateEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static String queryUser(String userNum) {
        UserEntity quaryResult = null;
        try {
            quaryResult = db.selector(UserEntity.class)
                    .where("num", "=", userNum)
                    .findFirst();
        } catch (DbException e) {
        }

        if (quaryResult != null) {
            return quaryResult.getName();
        }

        return null;
    }

    public static void  saveUser(String userNum, String userName){
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userName);
        userEntity.setNum(userNum);

        try {
            db.save(userEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static List queryHistoryData(Class queryEntityClass, String userName, int year, int month, int day) {
        List list = null;
        try {
            list = db.selector(queryEntityClass)
                    .where("user", "=", userName)
                    .and("year", "=", year)
                    .and("month", "=", month)
                    .and("day", "=", day)
                    .findAll();

            Log.e("HR", "quaryResultListSize: " + list.size());

        } catch (DbException e) {
            e.printStackTrace();
        }

        Log.e("HR", "queryHistoryData: " + list);
        return list;
    }

    public static void deleteSameDayData(Class deleteEntityClass) {
        if(calendar.get(Calendar.MONTH) == 0) {
            try {
                db.delete(deleteEntityClass,WhereBuilder.b("month", "=", 12+"")
                        .and("day","=",calendar.get(Calendar.DAY_OF_MONTH)));
            } catch (DbException e) {
                e.printStackTrace();
            }

            return;
        }
        try {
            db.delete(deleteEntityClass,WhereBuilder.b("month", "=", calendar.get(Calendar.MONTH)+"")
                    .and("day","=",calendar.get(Calendar.DAY_OF_MONTH)));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
 }
