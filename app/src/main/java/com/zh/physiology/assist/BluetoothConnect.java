package com.zh.physiology.assist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import com.zh.physiology.activity.HeartSoundActivity;
import com.zh.physiology.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author：heng.zhang
 * date：2016/12/10
 * description：
 */
public class BluetoothConnect {
    private BluetoothAdapter mmBluetoothAdapter;

    /*发起蓝牙连接的线程*/
    public class ConnectThread extends Thread {
        BluetoothSocket mSocket;
        public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
            mmBluetoothAdapter = bluetoothAdapter;
            BluetoothSocket temp = null;
            try {
                temp = device.createRfcommSocketToServiceRecord(Constants.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mSocket = temp;
        }

        @Override
        public void run() {
            mmBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
                Log.e("HR", "连接成功");
                MainActivity.mainHandler.obtainMessage(Constants.BLUETOOTH_CONNECTED, "蓝牙连接成功").sendToTarget();
                new CommunicatThread(mSocket).start();
            } catch (IOException e) {
                Log.e("HR", "连接失败");
                MainActivity.mainHandler.obtainMessage(Constants.BLUETOOTH_CONNECTED, "蓝牙连接失败，请重启app！").sendToTarget();
                e.printStackTrace();
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

        }
    }

    /*蓝牙通信的线程*/
    public class CommunicatThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        public CommunicatThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = mmSocket.getInputStream();
                tempOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("HR","得到输入输出流失败");
            }

            mmInputStream = tempIn;
            mmOutputStream = tempOut;
            HeartSoundActivity.commandOutputStream = mmOutputStream;
        }

        @Override
        public void run() {
            String res = "";
            int dataErrorCount = 0;
//            try {
////                mmOutputStream.write(0);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            while(true) {
                try {
                    res += (char)mmInputStream.read();
                    if(res.length() == 8) {
                        res = res.trim();
                        if(isFormatMatching(res)) {
                            dataErrorCount = 0;
                            MainActivity.mainHandler.obtainMessage(Constants.MESSAGE_READ, res).sendToTarget();
                        } else {
                            dataErrorCount++;
                            if(dataErrorCount >= 5) {
//                                MainActivity.mainHandler.obtainMessage(Constants.BLUETOOTH_CONNECTED, "数据出错，请重启app和采集设备！").sendToTarget();
                            }
                            Log.e("HR", "数据格式错误    res:" + res);
                        }
                        res = "";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean isFormatMatching(String res) {
            String temperatureReg = "1 \\d{2}.\\d{1}";
            String bloodPressReg = "[2|3] \\d{2,3}";
            String heartRateReg = "[4] \\d{2,3}";
            String heartSoundReg = "[5] \\d{1,4}";
            String userReg = "[0] \\d{1,2}";

            Pattern temperaturePattern = Pattern.compile(temperatureReg);
            Pattern bloodPressPattern = Pattern.compile(bloodPressReg);
            Pattern heartRatePattern = Pattern.compile(heartRateReg);
            Pattern heartSoundPattern = Pattern.compile(heartSoundReg);
            Pattern userPattern = Pattern.compile(userReg);

            Matcher temperatureMatcher = temperaturePattern.matcher(res);
            Matcher bloodPressMatcher = bloodPressPattern.matcher(res);
            Matcher heartRateMatcher = heartRatePattern.matcher(res);
            Matcher heartSoundMatcher = heartSoundPattern.matcher(res);
            Matcher userMatcher = userPattern.matcher(res);

            return temperatureMatcher.matches()
                    || bloodPressMatcher.matches()
                    || heartRateMatcher.matches()
                    || heartSoundMatcher.matches()
                    || userMatcher.matches();
        }
    }
}
