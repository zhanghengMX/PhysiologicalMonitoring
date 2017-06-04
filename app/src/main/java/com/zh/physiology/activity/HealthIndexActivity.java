package com.zh.physiology.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zh.physiology.R;

public class HealthIndexActivity extends AppCompatActivity {
    private ProgressBar temperatureProgressBar;
    private ProgressBar bloodPressProgressBar;
    private ProgressBar heartRateProgressBar;

    private TextView grossScore_tv;
    private TextView temperature_score_tv;
    private TextView blood_press_score_tv;
    private TextView heart_rate_score_tv;

    private int temperatureScore;
    private int bloodPressScore;
    private int heartRateScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_index);

        temperatureProgressBar = (ProgressBar) findViewById(R.id.temperature_pb);
        bloodPressProgressBar = (ProgressBar) findViewById(R.id.blood_press_pb);
        heartRateProgressBar = (ProgressBar) findViewById(R.id.heart_rate_pb);

        grossScore_tv = (TextView) findViewById(R.id.gross_score_tv);
        temperature_score_tv = (TextView) findViewById(R.id.temperature_score_tv);
        blood_press_score_tv = (TextView) findViewById(R.id.blood_press_score_tv);
        heart_rate_score_tv = (TextView) findViewById(R.id.heart_rate_score_tv);

        Intent intent = getIntent();

        temperatureScore = intent.getIntExtra("temperatureScore", 20);
        bloodPressScore = intent.getIntExtra("bloodPressScore", 30);
        heartRateScore = intent.getIntExtra("heartRateScore", 50);

        String grossScore = (temperatureScore + bloodPressScore + heartRateScore) + "";
        grossScore_tv.setText(grossScore);
        temperature_score_tv.setText(temperatureScore+"");
        blood_press_score_tv.setText(bloodPressScore + "");
        heart_rate_score_tv.setText(heartRateScore + "");

        temperatureProgressBar.setProgress(temperatureScore);
        bloodPressProgressBar.setProgress(bloodPressScore);
        heartRateProgressBar.setProgress(heartRateScore);

        Log.e("HR", "健康指数" +temperatureScore + "   " + bloodPressScore + "   " +heartRateScore);
    }
}
