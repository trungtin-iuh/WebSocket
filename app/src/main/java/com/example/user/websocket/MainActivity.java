package com.example.user.websocket;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.user.websocket.circledisplay.CircleDisplay;
import com.example.user.websocket.model.AddressParameterRealTimeData;
import com.example.user.websocket.model.ParameterObjectRealTimeData;
import com.example.user.websocket.model.RealTimeStreamingData;
import com.example.user.websocket.utils.GsonUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketOptions;

public class MainActivity extends AppCompatActivity implements CircleDisplay.SelectionListener, OnChartValueSelectedListener {
    private Button btnConnect;
    private Button btnDisConnect;
    private LinearLayout lineColumn;
//    private Typeface mTfLight;
    private String val1,val2,val3,val4,val5,val6 = "";
    private int startId = 0;

    private Map<String, Integer> mIdMapping = new HashMap<String, Integer>();
    private WebSocketConnection mWebSocketConnection = new WebSocketConnection();
    private static final String TAG =
            MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnDisConnect = findViewById(R.id.btnDisConnect);
        lineColumn = findViewById(R.id.lineColumn);


//        lnContent = findViewById(R.id.lineCircle);

//        lineCircle = findViewById(R.id.lineCircle);
//        lineButton = findViewById(R.id.lineButton);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer("");
            }
        });

        btnDisConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disConnectToServer();
            }
        });
    }

    private void disConnectToServer() {
        if (mWebSocketConnection.isConnected()) {
            mWebSocketConnection.disconnect();
        }
    }

//    public void resetYLeftMinMax() {
//        mYMinValue = 0;
//        mYMaxValue = Float.MIN_VALUE;
//    }
//
//    private List<IBarDataSet> createBarDataSet(
//            Boolean isCreateBarDataSet,
//            Map<String, Object> parameterMapping) {
//
//        List<IBarDataSet> barDataSets = null;
//
//        if (isCreateBarDataSet) {
//            barDataSets = new ArrayList<>();
//        }
//
//        final String messageTogetDataRealTime = "{\"objects\": [{\"addresses\": [{\"address\":\"3000\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\":\"3002\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\":\"3004\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\": \"201A\",\"dataType\": \"float\",\"length\": 4},{\"address\": \"2020\",\"dataType\": \"float\",\"length\": 4},{\"address\": \"2000\",\"dataType\": \"float\",\"length\": 4}],\"hostname\": \"0.0.0.254\"}],\"sessionId\": \"\",\"timezone\": \"GMT+07:00\",\"updateTime\": 3}";
//
//        RealTimeStreamingData realTimeStreamingData = GsonUtil.getInstance()
//                .fromJson (messageTogetDataRealTime, RealTimeStreamingData.class);
//
//        for(ParameterObjectRealTimeData parameterObjectRealTimeData : realTimeStreamingData.getObjects()) {
//            String hostName = parameterObjectRealTimeData.getHostname();
//
//            for (AddressParameterRealTimeData addressParameterRealTimeData : parameterObjectRealTimeData.getAddresses()) {
//                String parameterAddress = addressParameterRealTimeData.getAddress();
//
//                if (parameterMapping != null) {
//                    Object objectMapping = parameterMapping.get(String.format("%s:%s", hostName, parameterAddress));
//                }
//            }
//
//            if(isCreateBarDataSet){
//                BarDataSet barDataSet = createBarDataSet();
//                float spaceColumn = mSpaceColumn + (startId * 0.5f);
//                barDataSet.addEntry(new BarEntry(startId + spaceColumn,
//                        0.000001f));
//                barDataSets.add(barDataSet);
//            }
//
//        }
//
//        return barDataSets;
//
//    }
//
//    private BarDataSet createBarDataSet(int color,
//                                        float textSize,
//                                        boolean isVisible){
//        BarDataSet barDataSet = new BarDataSet(new ArrayList<BarEntry>(), null);
//
//        barDataSet.setVisible(isVisible);
//        barDataSet.setDrawValues(true);
//        barDataSet.setColor(color);
//        barDataSet.setValueTextColor(color);
//        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        barDataSet.setValueFormatter(new IValueFormatter() {
//            @Override
//            public String getFormattedValue(
//                    float value,
//                    Entry entry,
//                    int dataSetIndex,
//                    ViewPortHandler viewPortHandler) {
//                return "";
//            }
//        });
//
//        return barDataSet;
//    }


    BarChart barChart;
    private void connectToServer(String wsUri) {

        String webSocketRealTimeDataUri = "wss://dataengine.globiots.com:443/data-engine/mobile/realtime";
//        String webSocketRealTimeDataUri = "ws://192.168.1.105:7080/data-engine/mobile/realtime";
        final String messageTogetDataRealTime = "{\"objects\": [{\"addresses\": [{\"address\":\"3000\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\":\"3002\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\":\"3004\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\": \"201A\",\"dataType\": \"float\",\"length\": 4},{\"address\": \"2020\",\"dataType\": \"float\",\"length\": 4},{\"address\": \"2000\",\"dataType\": \"float\",\"length\": 4}],\"hostname\": \"0.0.0.254\"}],\"sessionId\": \"\",\"timezone\": \"GMT+07:00\",\"updateTime\": 3}";

        // start init data - build layout
        barChart = new BarChart(getApplicationContext());

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        barChart.setLayoutParams(params);
        lineColumn.addView(barChart);

        RealTimeStreamingData realTimeStreamingData = GsonUtil.getInstance()
                .fromJson (messageTogetDataRealTime, RealTimeStreamingData.class);

        for(ParameterObjectRealTimeData parameterObjectRealTimeData : realTimeStreamingData.getObjects()) {

            String hostName = parameterObjectRealTimeData.getHostname();

            List<IBarDataSet> iBarDataSetList = new ArrayList<>();

            for (AddressParameterRealTimeData addressParameterRealTimeData : parameterObjectRealTimeData.getAddresses()) {
                mIdMapping.put(hostName + ":" + addressParameterRealTimeData.getAddress(), startId);

                BarDataSet barDataSet = new BarDataSet(new ArrayList<>(), "test");

                iBarDataSetList.add(barDataSet);

                startId++;
            }

            BarData barData = new BarData(iBarDataSetList);
            barChart.setData(barData);
        }

//                CircleDisplay circleDisplay = new CircleDisplay(getApplicationContext());
//                circleDisplay.setId(startId);
//                int width=350;
//                int height=350;
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
//                params.setMargins(0,8,0,8);
//                circleDisplay.setLayoutParams(params);
//                lnContent.addView(circleDisplay);
//                startId++;
//            }
//        }

        // end init data - transport data to layout
        try{
            WebSocket.WebSocketConnectionObserver realTimeRetrieveHandle =
                    new WebSocket.WebSocketConnectionObserver() {

                        @Override
                        public void onOpen() {
                            mWebSocketConnection.sendTextMessage(messageTogetDataRealTime);
                        }

                        @Override
                        public void onTextMessage(final String dataRealTime) {

                            RealTimeStreamingData realTimeStreamingData = GsonUtil.getInstance()
                                    .fromJson(dataRealTime, RealTimeStreamingData.class);
                            int index = 0;
                            for (ParameterObjectRealTimeData parameterObjectRealTimeData : realTimeStreamingData.getObjects()) {
                                for (AddressParameterRealTimeData addressParameterRealTimeData : parameterObjectRealTimeData.getAddresses()) {
                                    Float value = null;
                                    try {
                                        value = new Float(addressParameterRealTimeData.getValue());
                                    } catch (Exception e) {

                                    }

                                    BarData barData = barChart.getBarData();
                                    IBarDataSet iBarDataSet = barData.getDataSetByIndex(index);
                                    BarDataSet barDataSet = (BarDataSet) iBarDataSet;
                                    if (barDataSet != null) {
                                        float spaceColumn = 1 + (index * 0.5f);
                                    }

                                    BarEntry entry = null;
                                    if (barDataSet.getValues().size() > 0) {
                                        entry = barDataSet.getEntryForIndex(0);
                                    }
                                    if (entry == null) {
                                        entry = new BarEntry(
                                                index + 1,
                                                value,
                                                true);
                                        List<Object> listData = new ArrayList<>();
                                        barDataSet.addEntry(entry);
                                    } else {
                                        entry.setData(null);
                                        entry.setY(value);
                                    }
                                    barData.notifyDataChanged();

                                    displayRealTimeInBarChart(value);
                                }
                            }
                        }

//                                if (TextUtils.equals("0.0.0.254", parameterObjectRealTimeData.getHostname())) {
//                                    String hostName = parameterObjectRealTimeData.getHostname();
//
//
//                                    for (AddressParameterRealTimeData addressParameterRealTimeData : parameterObjectRealTimeData.getAddresses()) {
//
//                                        if (TextUtils.equals("3000", addressParameterRealTimeData.getAddress())) {
//                                            val1 = addressParameterRealTimeData.getValue();
//                                        }
//
//                                        if (TextUtils.equals("3002", addressParameterRealTimeData.getAddress())) {
//                                            val2 = addressParameterRealTimeData.getValue();
//                                        }
//
//                                        if (TextUtils.equals("3004", addressParameterRealTimeData.getAddress())) {
//                                            val3 = addressParameterRealTimeData.getValue();
//                                        }
//
//                                        if (TextUtils.equals("201A", addressParameterRealTimeData.getAddress())) {
//                                            val4 = addressParameterRealTimeData.getValue();
//                                        }
//
//                                        if (TextUtils.equals("2020", addressParameterRealTimeData.getAddress())) {
//                                            val5 = addressParameterRealTimeData.getValue();
//                                        }
//
//                                        if (TextUtils.equals("2000", addressParameterRealTimeData.getAddress())) {
//                                            val6 = addressParameterRealTimeData.getValue();
//                                        }
//
//                                    }
//
//                                    displayRealTimeInBarChart(val1, val2, val3, val4, val5, val6);
//                                }
//                            }
//                        }
//                                    Log.d("aaa","val: "+val1+"\n"+"va2: "+val2+"\n"+"va3: "+val3+"\n"+"va4: "+val4+"\n"+"va5: "+val5+"\n"+"va6: "+val6);
//                                }
//
//                            }
//                        }
//
//                                    CircleDisplay circleDisplay = lnContent.findViewById(mIdMapping.get(hostName+":"+addressParameterRealTimeData.getAddress()));
//                                    val = addressParameterRealTimeData.getValue();
//
//                                    displayRealTimeInBarChart(val);
//
////                                    displayRealTimeInCircleChart(circleDisplay, val);
//
//                                }

                        @Override
                        public void onRawTextMessage(byte[] payload) {

                        }

                        @Override
                        public void onBinaryMessage(byte[] payload) {

                        }

                        @Override
                        public void onClose(final WebSocketCloseNotification code, final String reason) {

                        }
                    };
            WebSocketOptions websocketOptions = new WebSocketOptions();
            websocketOptions.setSocketConnectTimeout(15000);//ms ~ 15 s
            websocketOptions.setSocketReceiveTimeout(15000);//ms ~ 15 s

            mWebSocketConnection.connect(
                    new URI(webSocketRealTimeDataUri),
                    realTimeRetrieveHandle,
                    websocketOptions);

        } catch (final Exception e){
            Log.w("WEB_SOCKET", e.toString());

        }
    }

    private void displayRealTimeInBarChart(Float value) {

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        ArrayList<BarEntry> valueSet3 = new ArrayList<>();
        ArrayList<BarEntry> valueSet4 = new ArrayList<>();
        ArrayList<BarEntry> valueSet5 = new ArrayList<>();
        ArrayList<BarEntry> valueSet6 = new ArrayList<>();

        valueSet1.add(new BarEntry(0,value));
        valueSet2.add(new BarEntry(1,value));
        valueSet3.add(new BarEntry(2,value));
        valueSet4.add(new BarEntry(3,value));
        valueSet5.add(new BarEntry(4,value));
        valueSet6.add(new BarEntry(5,value));


        BarDataSet dataSet1,dataSet2,dataSet3,dataSet4,dataSet5,dataSet6;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {

            dataSet1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            dataSet2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            dataSet3 = (BarDataSet) barChart.getData().getDataSetByIndex(2);
            dataSet4 = (BarDataSet) barChart.getData().getDataSetByIndex(3);
            dataSet5 = (BarDataSet) barChart.getData().getDataSetByIndex(4);
            dataSet6 = (BarDataSet) barChart.getData().getDataSetByIndex(5);

            dataSet1.setValues(valueSet1);
            dataSet2.setValues(valueSet2);
            dataSet3.setValues(valueSet3);
            dataSet4.setValues(valueSet4);
            dataSet5.setValues(valueSet5);
            dataSet6.setValues(valueSet6);

            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            dataSet1 = new BarDataSet(valueSet1,"3000");
            dataSet1.setDrawIcons(false);
            dataSet1.setColor(Color.GRAY);

            dataSet2 = new BarDataSet(valueSet2,"3002");
            dataSet2.setDrawIcons(false);
            dataSet2.setColor(Color.CYAN);

            dataSet3 = new BarDataSet(valueSet3,"3004");
            dataSet3.setDrawIcons(false);
            dataSet3.setColor(Color.GREEN);

            dataSet4 = new BarDataSet(valueSet4,"201A");
            dataSet4.setDrawIcons(false);
            dataSet4.setColor(Color.RED);

            dataSet5 = new BarDataSet(valueSet5,"2020");
            dataSet5.setDrawIcons(false);
            dataSet5.setColor(Color.YELLOW);

            dataSet6 = new BarDataSet(valueSet6,"2000");
            dataSet6.setDrawIcons(false);
            dataSet6.setColor(Color.BLUE);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(dataSet1);
            dataSets.add(dataSet2);
            dataSets.add(dataSet3);
            dataSets.add(dataSet4);
            dataSets.add(dataSet5);
            dataSets.add(dataSet6);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            barChart.setData(data);
            barChart.animateY(1000);

        }

        barChart.invalidate();


    }

//    private BarDataSet createDataSet() {
//
//        BarDataSet set = new BarDataSet(null, "VALUES REALTIME");
//        set.setDrawIcons(false);
//        set.setColors(ColorTemplate.COLORFUL_COLORS);
//
//        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//        dataSets.add(set);
//
//        BarData datas = new BarData(dataSets);
//        datas.setValueTextSize(10f);
//        datas.setBarWidth(0.9f);
//        return set;
//    }


    private void displayRealTimeInCircleChart(CircleDisplay circleDisplay, String values) {

        circleDisplay.setValueWidthPercent(55f);
        circleDisplay.setTextSize(14f);
        circleDisplay.setColor(Color.GREEN);
        circleDisplay.setDrawText(true);
        circleDisplay.setDrawInnerCircle(true);
        circleDisplay.setFormatDigits(1);
        circleDisplay.setTouchEnabled(true);
        circleDisplay.setSelectionListener(this);
        circleDisplay.setUnit("%");
        circleDisplay.setStepSize(0.5f);
        circleDisplay.showValue(Float.parseFloat(values), 300f, true);

    }

    private void displayRealTimeInBarChart(String val1,String val2,String val3,String val4,String val5,String val6) {

        BarChart barChart = new BarChart(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        barChart.setLayoutParams(params);
        lineColumn.addView(barChart);

        DecimalFormat df = new DecimalFormat("###.###");
        DecimalFormat dff = new DecimalFormat("1.###");

        ArrayList<BarEntry> valueSet1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> valueSet3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> valueSet4 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> valueSet5 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> valueSet6 = new ArrayList<BarEntry>();

        if (val1 == "N/A" || val2 == "N/A" || val3 == "N/A" || val4 == "N/A" || val5 == "N/A" || val6 == "N/A"){
            val1 = val2 = val3 = val4 = val5 = val6 = String.valueOf(0);
        }else {
            valueSet1.add(new BarEntry(0, Float.parseFloat(df.format(Float.parseFloat(val1)))));
            valueSet2.add(new BarEntry(1, Float.parseFloat(df.format(Float.parseFloat(val2)))));
            valueSet3.add(new BarEntry(2, Float.parseFloat(df.format(Float.parseFloat(val3)))));
            valueSet4.add(new BarEntry(3, Float.parseFloat(df.format(Float.parseFloat(val4)))));
            valueSet5.add(new BarEntry(4, Float.parseFloat(dff.format(Float.parseFloat(val5)))));
            valueSet6.add(new BarEntry(5, Float.parseFloat(df.format(Float.parseFloat(val6)))));
        }

        BarDataSet dataSet1,dataSet2,dataSet3,dataSet4,dataSet5,dataSet6;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {

            dataSet1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            dataSet2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            dataSet3 = (BarDataSet) barChart.getData().getDataSetByIndex(2);
            dataSet4 = (BarDataSet) barChart.getData().getDataSetByIndex(3);
            dataSet5 = (BarDataSet) barChart.getData().getDataSetByIndex(4);
            dataSet6 = (BarDataSet) barChart.getData().getDataSetByIndex(5);

            dataSet1.setValues(valueSet1);
            dataSet2.setValues(valueSet2);
            dataSet3.setValues(valueSet3);
            dataSet4.setValues(valueSet4);
            dataSet5.setValues(valueSet5);
            dataSet6.setValues(valueSet6);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            dataSet1 = new BarDataSet(valueSet1,"3000");
            dataSet1.setDrawIcons(false);
            dataSet1.setColor(Color.BLUE);

            dataSet2 = new BarDataSet(valueSet2,"3002");
            dataSet2.setDrawIcons(false);
            dataSet2.setColor(Color.CYAN);

            dataSet3 = new BarDataSet(valueSet3,"3004");
            dataSet3.setDrawIcons(false);
            dataSet3.setColor(Color.GREEN);

            dataSet4 = new BarDataSet(valueSet4,"201A");
            dataSet4.setDrawIcons(false);
            dataSet4.setColor(Color.RED);

            dataSet5 = new BarDataSet(valueSet5,"2020");
            dataSet5.setDrawIcons(false);
            dataSet5.setColor(Color.YELLOW);

            dataSet6 = new BarDataSet(valueSet6,"2000");
            dataSet6.setDrawIcons(false);
            dataSet6.setColor(Color.GRAY);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(dataSet1);
            dataSets.add(dataSet2);
            dataSets.add(dataSet3);
            dataSets.add(dataSet4);
            dataSets.add(dataSet5);
            dataSets.add(dataSet6);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueFormatter( new ValueFormatter());
            barChart.setData(data);

            barChart.animateY(1000);
        }


        barChart.invalidate();
//        set1.setDrawIcons(false);
//        set1.setColors(ColorTemplate.COLORFUL_COLORS);
//
//        ArrayList<IBarDataSet> dataSet = new ArrayList<IBarDataSet>();
//        dataSet.add(set1);


    }

    private class ValueFormatter implements com.example.user.websocket.formater.IValueFormatter, IValueFormatter {

        private DecimalFormat mFormat;

        public ValueFormatter() {
            mFormat = new DecimalFormat("#.000");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebSocketConnection.isConnected()){
            mWebSocketConnection.disconnect();
        }
        mWebSocketConnection = null;
    }

    @Override
    public void onSelectionUpdate(float val, float maxval) {
        Log.i("Main", "Selection update: " + val + ", max: " + maxval);
    }

    @Override
    public void onValueSelected(float val, float maxval) {
        Log.i("Main", "Selection complete: " + val + ", max: " + maxval);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
