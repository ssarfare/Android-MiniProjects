package com.example.ilovezappos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionHistory extends AppCompatActivity {

    String api="https://www.bitstamp.net/api/v2/transactions/btcusd";
    LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        mChart = findViewById(R.id.mchart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        IMarker mv = new CustomMarkerView(this, R.layout.marker);

        //setMarkerView(MarkerView mv)
       // mChart.animateXY(1500,1500,Easing.EaseInBounce,Easing.EaseInBounce);

        mChart.setMarker(mv);
        mChart.setHighlightPerTapEnabled(false);
        mChart.getDescription().setEnabled(false);

        if(ConnectionManager.isConnected(this)){
            new GetDataAsync().execute(api);
        }else{
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private class GetDataAsync extends AsyncTask<String, Integer, ArrayList<Entry>> {
        ProgressDialog pb;
        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(TransactionHistory.this);
            pb.setMessage("Loading Transaction History...");
            pb.setMax(100);
            pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pb.setCancelable(false);
            pb.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pb.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> result) {
            pb.dismiss();
            if(result==null){
                Toast.makeText(TransactionHistory.this, "No data Found", Toast.LENGTH_SHORT).show();
            }
            else {
                setData(result);
            }
        }


        public void setData(ArrayList<Entry> data){
            LineDataSet set;
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set.setValues(data);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set = new LineDataSet(data, "Price v/s Time");
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setHighlightEnabled(false);
                set.setDrawCircles(false);
                set.setLineWidth(2);
                set.setColor(Color.parseColor("#1a66ff"));
                set.setValueTextSize(12);
                set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                set.setValueTextColor(Color.parseColor("#00b300"));
                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                YAxis left = mChart.getAxisLeft();
                left.setValueFormatter(new MyYAxisValueFormatter());
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new MyXAxisValueFormatter());
                YAxis yAxisRight =  mChart.getAxisRight();
                yAxisRight.setEnabled(false);
                YAxis yAxisLeft = mChart.getAxisLeft();
                yAxisLeft.setGranularity(1f);
                set.setFillAlpha(110);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set);
                LineData dataSet = new LineData(dataSets);
                mChart.setData(dataSet);
                mChart.invalidate();
            }

        }




        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            HttpURLConnection connection = null;

            ArrayList<Entry> transactions= new ArrayList();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONArray dataArray = new JSONArray(json);
                    if (dataArray.length()>0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            publishProgress(i);
                            JSONObject trans = dataArray.getJSONObject(i);

                           Entry entry=new Entry(trans.getInt("date"),trans.getInt("price"));
                           transactions.add(entry);
                        }
                    }
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return transactions;
        }
    }

    private class MyYAxisValueFormatter extends ValueFormatter{
        @Override
        public String getFormattedValue(float value) {
            return value+"$";
        }
    }


    private class MyXAxisValueFormatter extends ValueFormatter{
        String pattern = "hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        @Override
        public String getFormattedValue(float value) {

            return simpleDateFormat.format(new java.util.Date((long)value*1000));
        }
    }

    private class CustomMarkerView extends MarkerView {

        private TextView tvContent;
        private MPPointF mOffset;

       public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = (TextView) findViewById(R.id.tvContent);

        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText("" + e.getX()+" " + e.getY());
            super.refreshContent(e, highlight);
        }


        @Override
        public MPPointF getOffset() {

            if (mOffset == null) {
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }

    }

}
