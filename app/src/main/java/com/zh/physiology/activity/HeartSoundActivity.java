package com.zh.physiology.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zh.physiology.R;
import com.zh.physiology.linechart.HeartSoundLine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * author：heng.zhang
 * date：2016/11/20
 * description：
 */
public class HeartSoundActivity extends AppCompatActivity {
    private HeartSoundLine heartSoundLine = new HeartSoundLine();
    private int heartSoundValue = 0;
    private Random random = new Random();
    static ArrayList<Integer> heartSoundValueList = new ArrayList<>();
    private DrawLineThread drawLineThread = null;
    private boolean isDrawLine = false;

    public static OutputStream commandOutputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isEnableVibrate = false;
        setContentView(R.layout.activity_heart_sound);
        initChartLine(savedInstanceState);
        heartSoundValueList.add(1000);
        drawLineThread = new DrawLineThread();
        drawLineThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if(commandOutputStream != null) {
                commandOutputStream.write(1);
                Log.d("HR", "发送指令1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(commandOutputStream != null) {
                commandOutputStream.write(0);
                Log.d("HR", "发送指令0");
                MainActivity.isEnableVibrate = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initChartLine(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.heart_sound_container, heartSoundLine).commit();
        }
    }

    public void startDrawLine(View view) {
        isDrawLine = true;
    }

    public void stopDrawLine(View view) {
        isDrawLine = false;
    }

    class DrawLineThread extends Thread {
        @Override
        public void run() {
            super.run();

            while(true) {
                try {
                    if(isDrawLine) {
                        sleep(100);
                        heartSoundValue = heartSoundValueList.get(heartSoundValueList.size() - 1);
                        heartSoundLine.recievedMessage("START", heartSoundValue);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
