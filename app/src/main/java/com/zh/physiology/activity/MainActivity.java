package com.zh.physiology.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zh.physiology.R;
import com.zh.physiology.assist.BluetoothConnect;
import com.zh.physiology.assist.Constants;
import com.zh.physiology.assist.SqliteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * author：heng.zhang
 * date：2016/11/20
 * description：
 */
public class MainActivity extends AppCompatActivity {

    public static Handler mainHandler;
    public static String userName = "张恒";
    private ImageView user_iv = null;
    private TextView user_tv = null;
    private TextView health_score_tv = null;

    private TextView temperature_tv = null;
    private TextView high_pressure_tv = null;
    private TextView low_pressure_tv = null;
    private TextView heart_rate_tv = null;

    private ImageView weather_iv = null;
    private TextView city_tv = null;
    private TextView weather_tv = null;
    private TextView airTemperature_tv = null;

    private RelativeLayout temperature_rl = null;
    private RelativeLayout blood_press_rl = null;
    private RelativeLayout heart_rate_rl = null;
    private RelativeLayout heart_sounds_rl = null;

    private BluetoothConnect mBluetoothConnect = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private String key; //接受的数据的种类标志位
    private String value;//接收的数据的值

    private int blood_pressure_count = 0;
    private int highPressure = 120;
    private int lowPressure = 70;
    private int heartRate = 70;
    private float temperature = 36.4f;
    private int heartSound;
    private Boolean isSkipToBloodPressActivtiy = false;
    private Boolean isSkipToHeartRateActivtiy = false;
    private Boolean isSkipToHeartSoundActivtiy = false;
    private Boolean isSkipToTemperatureActivtiy = false;

    private String weather;
    private String airTemperature;

    private int temperatureScore = 20;
    private int bloodPressScore = 30;
    private int heartRateScore = 50;

    public static boolean isEnableVibrate = true;
    private float temperaturebuffer = 36.4f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HR", "onCreate");
        setContentView(R.layout.activity_main);
        requestWeatherInfo();
        uiInit();//通过id找到控件
        enableBluetoothConnect();
        refreshUI();
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Constants.MESSAGE_READ) {
                    splitData((String) msg.obj);
                    assignmentWithMark();
                    Log.d("HR", "handler接收到了消息 " + key + " " + value);
                    MainActivity.this.refreshUI();
                    MainActivity.this.saveData();
                } else if(msg.what == Constants.BLUETOOTH_CONNECTED) {
                    Toast.makeText(MainActivity.this, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void vibrate(long milliseconds) {
        Vibrator vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    private void showInputUserNameDialog() {
        final EditText userNameED = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入姓名")
                .setView(userNameED)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userName = userNameED.getText().toString().trim();
                        user_tv.setText(userName);
                        SqliteHelper.saveUser(value,userName);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void healthIndexOnClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("temperatureScore", temperatureScore);
        intent.putExtra("bloodPressScore", bloodPressScore);
        intent.putExtra("heartRateScore", heartRateScore);
        intent.setClass(MainActivity.this, HealthIndexActivity.class);
        this.startActivity(intent);
    }

    public void bloodPressOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, BloodPressActivity.class);
        this.startActivity(intent);
    }

    public void heartRateOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, HeartRateActivity.class);
        intent.putExtra("heartRate", heartRate);
        this.startActivity(intent);
        isSkipToHeartRateActivtiy = true;
    }

    public void heartSoundOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, HeartSoundActivity.class);
        this.startActivity(intent);
        isSkipToHeartSoundActivtiy = true;
    }

    public void temperatureOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, TemperatureActivity.class);
        intent.putExtra("temperature", temperaturebuffer);
        this.startActivity(intent);
        isSkipToTemperatureActivtiy = true;
    }

    private void uiInit() {
        user_iv = (ImageView) findViewById(R.id.user_iv);
        user_tv = (TextView) findViewById(R.id.user_tv);
        health_score_tv = (TextView) findViewById(R.id.health_score_tv);

        temperature_tv = (TextView) findViewById(R.id.temperature_tv);
        high_pressure_tv = (TextView) findViewById(R.id.high_pressure_tv);
        low_pressure_tv = (TextView) findViewById(R.id.low_pressure_tv);
        heart_rate_tv = (TextView) findViewById(R.id.heart_rate_tv);

        weather_iv = (ImageView) findViewById(R.id.weather_iv);
        weather_tv = (TextView) findViewById(R.id.weather_tv);
        city_tv = (TextView) findViewById(R.id.city_tv);
        airTemperature_tv = (TextView) findViewById(R.id.airtemperature_tv);

        temperature_rl = (RelativeLayout) findViewById(R.id.temperature_rl);
        blood_press_rl = (RelativeLayout) findViewById(R.id.blood_press_rl);
        heart_rate_rl = (RelativeLayout) findViewById(R.id.heart_rate_rl);
        heart_sounds_rl = (RelativeLayout) findViewById(R.id.heart_sounds_rl);

    }

    private void enableBluetoothConnect() {
        registerBroadcastReceiver();
        if (judgeSupportBluetooth()) {
            return;
        }
        ensurebluetoothIsStart();
        mBluetoothAdapter.startDiscovery();
        /*创建BluetoothConnect对象用于调用内部类*/
        mBluetoothConnect = new BluetoothConnect();
    }

    /**
     * 检测蓝牙是否启动，如果没有就启动蓝牙
     */
    private void ensurebluetoothIsStart() {
        int REQUEST_ENABLE_BT = 1;
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            Log.e("HR", "蓝牙已经启动");
        }
    }

    /**
     * 判断手机手否支持蓝牙功能
     */
    private boolean judgeSupportBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "您的手机不支持蓝牙功能", Toast.LENGTH_SHORT).show();
            this.finish();
            return true;
        }
        return false;
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
    }

    /**
     * 拆分蓝牙接收到的数据，一个键，一个值
     *
     * @param data
     */
    private void splitData(String data) {
        String[] results = data.split(" ");
        key = results[0];
        value = results[1];
    }

    /**
     * 根据mark给对应的参数变量赋值
     */
    private void assignmentWithMark() {
        switch (key) {
            case Constants.MARK_HEAT:
                temperature = Float.parseFloat(value);
                if (temperature > 34.0 && temperature <= 43.0) {
                    temperaturebuffer = temperature;
                }
                break;
            case Constants.MARK_HIGH_PRESSURE:
                highPressure = Integer.parseInt(value);
                break;
            case Constants.MARK_LOW_PRESSURE:
                lowPressure = Integer.parseInt(value);
                break;
            case Constants.MARK_HEART_RATE:
                heartRate = Integer.parseInt(value);
                break;
            case Constants.MARK_HEART_SOUNDS:
                heartSound = Integer.parseInt(value);
                break;
            case Constants.USER:
                formatUser(value);
                break;
            default:
                break;
        }
    }

    private void formatUser(String userNum) {
        String name = SqliteHelper.queryUser(userNum);
        if(name == null) {
            showInputUserNameDialog();
        } else {
            userName = name;
            user_tv.setText(userName);
        }

    }

    private void refreshUI() {
        refreshTemperatureUi();
        refreshBloodPressUi();
        refreshHeartRateUi();
        refreshHealthIndex();
    }

    private void refreshHealthIndex(){
        int grossScore = temperatureScore + bloodPressScore + heartRateScore;
        health_score_tv.setText("健康指数 " + grossScore);
        if(grossScore > 90) {
            health_score_tv.setBackgroundColor(Color.GREEN);
        } else if(grossScore >= 80 && grossScore <= 90) {
            health_score_tv.setBackgroundColor(0xffffbc02);
        } else if(grossScore < 80 && grossScore >= 70) {
            health_score_tv.setBackgroundColor(0xffff7744);
        }else if(grossScore < 70) {
            health_score_tv.setBackgroundColor(Color.RED);
        }
    }

    private void refreshHeartRateUi() {
        if (heartRate < 40 || heartRate > 200) {
            return;
        }
        heart_rate_tv.setText(heartRate + "");
        //判断心率的值来改变该区域的背景颜色
        if ((heartRate > 60) && (heartRate <= 100)) {
            heart_rate_rl.setBackgroundColor(Color.GREEN);
            heartRateScore = 50;
        } else if ((heartRate > 100 && heartRate <= 110) || (heartRate > 50 && heartRate <= 60)) {
            heart_rate_rl.setBackgroundColor(0xffffbc02);
            heartRateScore = 30;
        } else if ((heartRate > 110 && heartRate <= 130) || (heartRate > 40 && heartRate <= 50)) {
            heart_rate_rl.setBackgroundColor(0xffff7744);
            heartRateScore = 10;
            if(isEnableVibrate) {
                vibrate(2000);
            }
        } else if (heartRate > 130 || heartRate <= 40) {
            heart_rate_rl.setBackgroundColor(Color.RED);
            heartRateScore = 0;
            if(isEnableVibrate) {
                vibrate(2000);
            }
        }

        if (isSkipToHeartRateActivtiy) {
            HeartRateActivity.updateHeartRatePanel(heartRate);
        }
    }

    private void refreshBloodPressUi() {
        if (highPressure <= 70 || highPressure > 250 || lowPressure < 40 || lowPressure > 150) {
            return;
        }
        high_pressure_tv.setText(highPressure + "");
        low_pressure_tv.setText(lowPressure + "");
        //判断血压的值来改变该区域的背景颜色
        if ((highPressure >= 130 && highPressure < 140)
                || (highPressure >= 90 && highPressure < 100)
                || (lowPressure >= 85 && lowPressure < 90)
                || (lowPressure >= 60 && lowPressure < 65)) {
            blood_press_rl.setBackgroundColor(0xffffbc02);
            bloodPressScore = 20;
        }
        if((highPressure >= 140 && highPressure < 150)
                || (highPressure < 90 && highPressure >= 80)
                || (lowPressure >= 90 && lowPressure < 100)
                || (lowPressure >= 50 && lowPressure < 60)) {
            blood_press_rl.setBackgroundColor(0xffff7744);
            bloodPressScore = 10;
            if(isEnableVibrate) {
                vibrate(2000);
            }
        }
        if (highPressure >= 150 || highPressure < 80 || lowPressure >= 100 || lowPressure < 50) {
            blood_press_rl.setBackgroundColor(Color.RED);
            bloodPressScore = 0;
            if(isEnableVibrate) {
                vibrate(2000);
            }
        }
        if((highPressure >= 100 && highPressure < 130)
                && (lowPressure >= 65 && lowPressure < 85)){
            blood_press_rl.setBackgroundColor(Color.GREEN);
            bloodPressScore = 30;
        }
    }

    private void refreshTemperatureUi() {
        if (temperature <= 34.0 || temperature > 43.0) {
            return;
        }
        temperature_tv.setText(temperature + "");
        //判断体温的值来改变该区域的背景颜色
        if (temperature > 36.0 && temperature <= 37.0) {
            temperature_rl.setBackgroundColor(Color.GREEN);
            temperatureScore = 20;
        } else if ((temperature > 37.0 && temperature <= 38.0) || (temperature > 35.0 && temperature <= 36.0)) {
            temperature_rl.setBackgroundColor(0xffffbc02);
            temperatureScore = 10;
        } else if (temperature > 38.0 && temperature <= 39.0) {
            temperature_rl.setBackgroundColor(0xffff7744);
            temperatureScore = 5;
            if(isEnableVibrate) {
                vibrate(2000);
            }
        } else {
            temperature_rl.setBackgroundColor(Color.RED);
            temperatureScore = 0;
            if(isEnableVibrate) {
                vibrate(2000);
            }
        }
        if (isSkipToTemperatureActivtiy) {
            TemperatureActivity.updateTempturePanel(temperature);
        }
    }

    private void saveData() {
        switch (key) {
            case Constants.MARK_HEAT:
                SqliteHelper.saveTemperature(userName, temperature);
                break;
            case Constants.MARK_HIGH_PRESSURE:
                blood_pressure_count++;
                break;
            case Constants.MARK_LOW_PRESSURE:
                blood_pressure_count++;
                if (blood_pressure_count >= 2) {
                    blood_pressure_count = 0;
                    SqliteHelper.saveBloodPressure(userName, highPressure, lowPressure);
                }
                break;
            case Constants.MARK_HEART_RATE:
                SqliteHelper.saveHeartRate(userName, heartRate);
                break;
            case Constants.MARK_HEART_SOUNDS:
                if (isSkipToHeartSoundActivtiy) {
                    HeartSoundActivity.heartSoundValueList.add(heartSound);
                }
                break;
            default:
                break;
        }
    }

    private void requestWeatherInfo() {
        String url = "http://api.jirengu.com/weather.php?city=%E6%88%90%E9%83%BD";
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                doWeatherInfoAnalysis(result);
                refreshAirTemperatureUi();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void doWeatherInfoAnalysis(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray results_array = json.getJSONArray("results");
            JSONArray weather_data = results_array.getJSONObject(0).getJSONArray("weather_data");
            weather = weather_data.getJSONObject(0).getString("weather");
            airTemperature = weather_data.getJSONObject(0).getString("temperature");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshAirTemperatureUi() {
        weather_tv.setText(weather);
        city_tv.setText("成都");
        airTemperature_tv.setText(airTemperature);
        showWeatherPicture();
    }

    private void showWeatherPicture() {
        if(weather.startsWith("晴")) {
            weather_iv.setImageResource(R.drawable.fine);
        } else if(weather.startsWith("多云")) {
            weather_iv.setImageResource(R.drawable.cloudy);
        } else if(weather.startsWith("阴")) {
            weather_iv.setImageResource(R.drawable.cloudy2);
        } else if(weather.startsWith("小雨")) {
            weather_iv.setImageResource(R.drawable.small_rain);
        } else if(weather.startsWith("中雨")) {
            weather_iv.setImageResource(R.drawable.milldle_rain);
        } else if(weather.startsWith("大雨")) {
            weather_iv.setImageResource(R.drawable.big_rain);
        } else if(weather.startsWith("雷")) {
            weather_iv.setImageResource(R.drawable.thunder);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName().contains("HC-05")) {
                    mBluetoothConnect.new ConnectThread(device, mBluetoothAdapter).start();
                }
            }
        }
    };
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d("HR", "onStop");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d("HR", "onDestroy");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d("HR", "onPause");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d("HR", "onResume");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d("HR", "onStart");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d("HR", "onRestart");
//    }
}
