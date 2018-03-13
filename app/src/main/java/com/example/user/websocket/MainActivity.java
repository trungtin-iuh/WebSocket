package com.example.user.websocket;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketOptions;

public class MainActivity extends AppCompatActivity implements CircleDisplay.SelectionListener {
    private Button btnConnect;
    private Button btnDisConnect;
    private LinearLayout lineColumn;
    private String val = "";
    private int startId = 0 ;
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
        if(mWebSocketConnection.isConnected()){
            mWebSocketConnection.disconnect();
        }
    }


    private void connectToServer(String wsUri) {

        String webSocketRealTimeDataUri = "wss://dataengine.globiots.com:443/data-engine/mobile/realtime";
        final String messageTogetDataRealTime = "{\"objects\": [{\"addresses\": [{\"address\":\"3000\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\":\"3002\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\":\"3004\",\"dataType\":\"integer_16\",\"length\":2,\"value\":\"\"},{\"address\": \"201A\",\"dataType\": \"float\",\"length\": 4},{\"address\": \"2020\",\"dataType\": \"float\",\"length\": 4},{\"address\": \"2000\",\"dataType\": \"float\",\"length\": 4}],\"hostname\": \"0.0.0.254\"}],\"sessionId\": \"\",\"timezone\": \"GMT+07:00\",\"updateTime\": 3}";

//        // start init data - build layout
        RealTimeStreamingData realTimeStreamingData = GsonUtil.getInstance()
                .fromJson (messageTogetDataRealTime, RealTimeStreamingData.class);

        lineColumn.removeAllViews();

        for(ParameterObjectRealTimeData parameterObjectRealTimeData : realTimeStreamingData.getObjects()){
            String hostName =  parameterObjectRealTimeData.getHostname();

            for(AddressParameterRealTimeData addressParameterRealTimeData: parameterObjectRealTimeData.getAddresses()){
                mIdMapping.put(hostName+":"+addressParameterRealTimeData.getAddress(), startId);
                
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                barEntries.add(new BarEntry(startId, Float.parseFloat(va)));

//                CircleDisplay circleDisplay = new CircleDisplay(getApplicationContext());
//                circleDisplay.setId(startId);
//                int width=350;
//                int height=350;
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
//                params.setMargins(0,8,0,8);
//                circleDisplay.setLayoutParams(params);
//                lnContent.addView(circleDisplay);
                startId++;
            }
        }

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
                                    .fromJson (dataRealTime, RealTimeStreamingData.class);

                            for(ParameterObjectRealTimeData parameterObjectRealTimeData : realTimeStreamingData.getObjects()){
                                String hostName =  parameterObjectRealTimeData.getHostname();
                                for(AddressParameterRealTimeData addressParameterRealTimeData: parameterObjectRealTimeData.getAddresses()){

                                    int data = mIdMapping.get(hostName+":"+addressParameterRealTimeData.getAddress());

//                                    CircleDisplay circleDisplay = lnContent.findViewById(mIdMapping.get(hostName+":"+addressParameterRealTimeData.getAddress()));
                                    val = addressParameterRealTimeData.getValue();

                                    displayRealTimeInBarChart(data, val);

//                                    displayRealTimeInCircleChart(circleDisplay, val);

                                }
                            }

                        }

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

    private void displayRealTimeInBarChart(int data, String values) {

        BarChart barChart = new BarChart(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        barChart.setLayoutParams(params);
        lineColumn.addView(barChart);


        ArrayList<BarEntry> valueSet = new ArrayList<BarEntry>();

        valueSet.add(new BarEntry(data, Float.parseFloat(values)));


//        valueSet.add(new BarEntry(0, Float.parseFloat(values)));
//        valueSet.add(new BarEntry(1, Float.parseFloat(values)));
//        valueSet.add(new BarEntry(2, Float.parseFloat(values)));
//        valueSet.add(new BarEntry(3, Float.parseFloat(values)));
//        valueSet.add(new BarEntry(4, Float.parseFloat(values)));
//        valueSet.add(new BarEntry(5, Float.parseFloat(values)));

        BarDataSet set1 = new BarDataSet(valueSet, "VALUES REALTIME");
        set1.setDrawIcons(false);
        set1.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData datas = new BarData(dataSets);
        datas.setValueTextSize(10f);
        datas.setBarWidth(0.9f);

        barChart.setData(datas);

        barChart.animateY(1000);
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
}
