package com.zh.physiology.linechart;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zh.physiology.R;

public class HeartSoundLine extends Fragment {

    private static String TAG = HeartSoundLine.class.getSimpleName();
    private static final int MAX_POINT = 100;
    /* 呼吸波形的相关参数 */
    private Handler handler;
    private String title = "Cardiograph";
    private XYSeries series;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    XYSeriesRenderer r = new XYSeriesRenderer();
    private Context context;
    RelativeLayout breathWave;
    int i = 0;

    private final int POINT_GENERATE_PERIOD=0; //单位是ms

    private int currentValue;//当前传进来的心音值

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            ArrayList<Integer> datas = new ArrayList<>();
            datas.add(currentValue);
            updateCharts(datas);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linechart, container, false);
        breathWave = (RelativeLayout) view.findViewById(R.id.line_chart);
        initCardiograph();

        return view;
    }

    /**
     * @Title revcievedMessage
     * @Description Fragment接收Activity中消息的第一种方式，直接在Fragment中提供一个public方法，供Activity调用
     * @param action
     * @return void
     */
    public void recievedMessage(String action, int heartRateValue){
        switch(action){
            case "START" :
                currentValue = heartRateValue;
                handler.postDelayed(runnable, POINT_GENERATE_PERIOD);
                break;
            case "STOP" :
                Log.w(TAG, "recieved Stop !");
                handler.removeCallbacksAndMessages(null);
                break;
        }
    }

    public void initCardiograph() {
        context = getActivity().getApplicationContext();
        // 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        series = new XYSeries(title);
        // 创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();
        // 将点集添加到这个数据集中
        mDataset.addSeries(series);
        // 以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
		/* int color = Color.parseColor("#08145e"); */
        int color = getResources().getColor(R.color.cardio_color3);
        PointStyle style = PointStyle.CIRCLE;
        buildRenderer(color, style, true);
        // 设置好图表的样式
        setChartSettings(renderer, "X", "Y", 0, MAX_POINT, 0, 3000, color,
                color);
        // 生成图表
        chart = ChartFactory.getLineChartView(context, mDataset, renderer);
        chart.setBackgroundColor(getResources().getColor(
                R.color.cardio_bg_color));
        breathWave.removeAllViews();
        breathWave.addView(chart);

    }

    protected void buildRenderer(int color, PointStyle style, boolean fill) {

        // 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String xTitle, String yTitle, double xMin, double xMax,
                                    double yMin, double yMax, int axesColor, int labelsColor) {
        // 有关对图表的渲染可参看api文档
        renderer.setBackgroundColor(getResources().getColor(
                R.color.cardio_bg_color));
        renderer.setChartTitle(title);
        renderer.setChartTitleTextSize(20);
        renderer.setDisplayValues(true);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        //renderer.setYAxisAlign(Align.RIGHT, 0);//用来调整Y轴放置的位置，表示将第一条Y轴放在右侧
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGridX(true);
        renderer.setGridColor(Color.GRAY);
        renderer.setXLabels(0);//若不想显示X标签刻度，设置为0 即可
        renderer.setYLabels(10);
        renderer.setLabelsTextSize(12);// 设置坐标轴标签文字的大小
        renderer.setXLabelsColor(labelsColor);
        renderer.setYLabelsColor(0, labelsColor);
        renderer.setYLabelsVerticalPadding(-5);
        renderer.setXTitle("");
        renderer.setYTitle("");
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setAxisTitleTextSize(20);
        renderer.setPointSize((float) 1);
        renderer.setShowLegend(false);
        renderer.setFitLegend(true);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(false,false);
        renderer.setMargins(new int[] { 30, 45, 10, 20 });// 设置图表的外边框(上/左/下/右)
        renderer.setMarginsColor(getResources().getColor(
                R.color.cardio_bg_color));
    }

    /**
     * @Title updateCharts
     * @Description 新生成的点一直在右侧，产生向左平移的效果，基于X轴坐标从0开始，然后递加的思想处理
     * @param datas
     * @return void
     */
    protected void updateCharts(ArrayList<Integer> datas) {
        for (int addY : datas) {
            series.add(i, addY);
            i++;
        }
        if (i < MAX_POINT) {
            renderer.setXAxisMin(0);
            renderer.setXAxisMax(MAX_POINT);
        } else {
            renderer.setXAxisMin(series.getItemCount() - MAX_POINT);
            renderer.setXAxisMax(series.getItemCount());
        }

        chart.repaint();
    }
}
