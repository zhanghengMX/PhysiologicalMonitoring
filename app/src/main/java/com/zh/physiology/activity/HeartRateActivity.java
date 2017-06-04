package com.zh.physiology.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zh.physiology.PanelView.HeartRatePanelView;
import com.zh.physiology.R;
import com.zh.physiology.assist.SqliteHelper;
import com.zh.physiology.entity.HeartRateEntity;
import com.zh.physiology.linechart.HeartRateLine;

import java.util.ArrayList;
import java.util.List;

/**
 * author：heng.zhang
 * date：2016/11/20
 * description：
 */
public class HeartRateActivity extends AppCompatActivity {
    private static HeartRatePanelView panelView;

    final HeartRateLine heartRateChart = new HeartRateLine();
    private int queryYear = 0;
    private int queryMonth = 0;
    private int queryDay = 0;
    private List historyDataList = null;
    private ArrayList<Integer> heartRateValueList = new ArrayList();

    private EditText dateEditText;

    private int currentHeartRate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        dateEditText = (EditText) findViewById(R.id.heart_rate_edtitext);

        Intent intent = getIntent();
        currentHeartRate = intent.getIntExtra("heartRate", 60);
        panelView = (HeartRatePanelView) findViewById(R.id.panView_heart_rate);
        panelView.setPercent(currentHeartRate / 240f * 100);

        initChartLine(savedInstanceState);
    }

    private void initChartLine(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.heart_rate_container, heartRateChart).commit();
        }
    }
    /**
     * 更新心率仪表盘的显示
     * @param heartRate
     */
    public static void updateHeartRatePanel(int heartRate) {
        if (heartRate < 40 || heartRate > 200) {
            return;
        }
        float percent = heartRate/ 240f * 100;
        panelView.setPercent(percent);
        Log.d("HR", heartRate+ "");
        HeartRateActivity.setArcColor(heartRate);
    }

    public void onSearchClick(View view) {
        if(splitDateSuccess()) {
            historyDataList = SqliteHelper.queryHistoryData(HeartRateEntity.class, MainActivity.userName, queryYear, queryMonth, queryDay);
            if(historyDataList.size() == 0) {
                Toast.makeText(HeartRateActivity.this, "没有该日期的数据！", Toast.LENGTH_SHORT).show();
            }
            selectData();
            getHeartRateValues(historyDataList);
            heartRateChart.recievedMessage("START", heartRateValueList);
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dateEditText.getWindowToken(),0);
    }

    private void selectData() {
        if(historyDataList.size() > 24) {
            while(historyDataList.size() > 24){
                historyDataList.remove(0);
            }
        }
    }

    /**
     * 用查询到的数据List得到体温值
     * @param dataList
     */
    private void getHeartRateValues(List dataList) {
        heartRateValueList.clear();
        HeartRateEntity heartRateEntity = null;
        for(int i=0; i < dataList.size(); i++) {
            heartRateEntity = (HeartRateEntity) dataList.get(i);
            heartRateValueList.add(heartRateEntity.getValue());
        }
    }
    private static void setArcColor(int heartRate) {
        if (heartRate < 40 || heartRate > 200) {
            return;
        }
        if((heartRate >= 60) && (heartRate <= 100)) {
            panelView.setThickArcColor(Color.GREEN);
        } else if ((heartRate > 100 && heartRate <= 140) || (heartRate > 45 && heartRate < 60)) {
            panelView.setThickArcColor(0xffffbc02);
        } else if(heartRate>140 || heartRate<45) {
            panelView.setThickArcColor(Color.RED);
        }
    }

    /**
     * 拆分日期字符串，
     * @return
     */
    private boolean splitDateSuccess() {
        String dateString = dateEditText.getText().toString();
        String[] dateArray = dateString.split("-");

        if(isCurrectData(dateArray)) {
            return true;
        } else {
            Toast.makeText(HeartRateActivity.this, "请输入正确的日期", Toast.LENGTH_SHORT).show();
            dateEditText.setText("");
            return false;
        }
    }

    /**
     * 判断输入的查询日期格式、数值是否正确
     * @param datas
     * @return
     */
    private boolean isCurrectData(String[] datas) {
        if(datas.length != 3) {
            Log.e("HR", "datas.length != 3");
            return false;
        }
        queryYear = Integer.parseInt(datas[0]);
        queryMonth = Integer.parseInt(datas[1]);
        queryDay = Integer.parseInt(datas[2]);

        if(queryYear < 2000 || queryYear > 2017 || queryMonth > 12
                || queryMonth <= 0 || queryDay <= 0 || queryDay > 31) {
            Log.e("HR", "queryYear < 2000");
            return false;
        }
        if(queryMonth == 2) {//判断闰年情况下输入的月份和day是否正确
            if((queryYear % 4 == 0 && queryYear % 100 != 0) || (queryYear % 400 == 0)) {
                if(queryDay > 29) {
                    Log.e("HR", "queryYear % 4 == 0");
                    return false;
                }
            } else {
                if(queryDay > 28) {
                    return false;
                }
            }
        }
        if(queryMonth == 4 || queryMonth == 6 || queryMonth == 9 || queryMonth == 11) {
            Log.e("HR", "queryMonth == 4");
            if(queryDay > 30) {
                return false;
            }
        }
        return true;
    }
}
