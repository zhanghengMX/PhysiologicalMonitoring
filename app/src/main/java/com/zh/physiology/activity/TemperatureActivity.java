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

import com.zh.physiology.PanelView.TemperaturePanelView;
import com.zh.physiology.R;
import com.zh.physiology.assist.SqliteHelper;
import com.zh.physiology.entity.TemperatureEntity;
import com.zh.physiology.linechart.TemperatureLine;

import java.util.ArrayList;
import java.util.List;

/**
 * author：heng.zhang
 * date：2016/11/20
 * description：
 */
public class TemperatureActivity extends AppCompatActivity {

    private static TemperaturePanelView panelView;
    TemperatureLine temperatureChart = new TemperatureLine();
    private int queryYear = 0;
    private int queryMonth = 0;
    private int queryDay = 0;
    private List historyDataList = null;
    private ArrayList<Float> temperatureValueList = new ArrayList();

    private EditText dateEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        dateEditText = (EditText) findViewById(R.id.temp_edtitext);
        panelView = (TemperaturePanelView) findViewById(R.id.panView_temperature);

        firstRefreshPanel(getIntent());

        initChartLine(savedInstanceState);
    }

    private void initChartLine(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.temperature_container, temperatureChart).commit();
        }
    }

    private void firstRefreshPanel(Intent intent) {
        float temperature = intent.getFloatExtra("temperature", 36.5f);
        float percent = (temperature-34f) / 12f * 100;
        panelView.setPercent(percent);
        TemperatureActivity.setArcColor(temperature);
    }

    /**
     * 更新体温仪表盘的显示
     * @param temperature
     */
    public static void updateTempturePanel(float temperature) {
        float percent = (temperature-34f) / 12f * 100;
        panelView.setPercent(percent);
        Log.d("HR", temperature+ "");
        TemperatureActivity.setArcColor(temperature);
    }

    /**
     * 根据体温值设置仪表盘的颜色
     * @param temperature
     */
    private static void setArcColor(float temperature) {
        if(temperature <= 37.0) {
            panelView.setThickArcColor(Color.GREEN);
        } else if (temperature > 37.0 && temperature<=38.0) { //低热状态
            panelView.setThickArcColor(-43759);
        } else if(temperature > 38.0 && temperature<=39.0) {//中度发热
            panelView.setThickArcColor(-52429);
        } else if(temperature > 39.0) { //发高烧
            panelView.setThickArcColor(Color.RED);
        }
    }

    /**
     * 查询历史数据的按钮点击时间监听
     */
public void onSearchClick(View view) {
    if(splitDateSuccess()) {//日期的int数值，年，月，日
        historyDataList = SqliteHelper.queryHistoryData(TemperatureEntity.class, MainActivity.userName, queryYear, queryMonth, queryDay);
        if(historyDataList.size() == 0) {
            Toast.makeText(TemperatureActivity.this, "没有该日期的数据！", Toast.LENGTH_SHORT).show();
        }
        selectData();
        getTemperatureValues(historyDataList);
        temperatureChart.recievedMessage("START", temperatureValueList);
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
    private void getTemperatureValues(List dataList) {
        temperatureValueList.clear();
        TemperatureEntity temperatureEntity = null;
        for(int i=0; i < dataList.size(); i++) {
            temperatureEntity = (TemperatureEntity) dataList.get(i);
            temperatureValueList.add(temperatureEntity.getValue());
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
            Toast.makeText(TemperatureActivity.this, "请输入正确的日期", Toast.LENGTH_SHORT).show();
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
