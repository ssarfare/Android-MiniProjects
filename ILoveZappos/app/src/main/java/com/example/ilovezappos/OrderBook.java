package com.example.ilovezappos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OrderBook extends AppCompatActivity {
    RecyclerView rv_bids, rv_asks;
    String api="https://www.bitstamp.net/api/v2/order_book/btcusd";
    private RecyclerView.Adapter bidsAdapter, asksAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_book);
        rv_bids=findViewById(R.id.recyclerView_bids);
        rv_asks=findViewById(R.id.recyclerView_asks);

        if(ConnectionManager.isConnected(this)){
            new GetDataAsync().execute(api);
        }else{
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }



    }



    private class GetDataAsync extends AsyncTask<String, Integer, List<List<OrderData>>> {
        ProgressDialog pb;
        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(OrderBook.this);
            pb.setMessage("Loading Order Book Data...");
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
        protected void onPostExecute(List<List<OrderData>> result) {
            pb.dismiss();
            if(result==null){
                Toast.makeText(OrderBook.this, "No data Found", Toast.LENGTH_SHORT).show();
            }
            else {

                rv_bids.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(OrderBook.this);
                rv_bids.setLayoutManager(layoutManager);
                bidsAdapter=new DataAdapter(result.get(0),"Bids");
                rv_bids.setAdapter(bidsAdapter);
                rv_asks.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(OrderBook.this);
                rv_asks.setLayoutManager(layoutManager);
                asksAdapter=new DataAdapter(result.get(1),"Asks");
                rv_asks.setAdapter(asksAdapter);


            }
        }

        @Override
        protected List<List<OrderData>> doInBackground(String... params) {
            HttpURLConnection connection = null;

            ArrayList<List<OrderData>> result= new ArrayList();
            ArrayList<OrderData> bidsArray=new ArrayList<>();
            ArrayList<OrderData> asksArray=new ArrayList<>();
            result.add(bidsArray);
            result.add(asksArray);
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject rootObject = new JSONObject(json);

                    JSONArray bids=rootObject.getJSONArray("bids");
                    JSONArray asks=rootObject.getJSONArray("asks");
                    if (bids.length()>0) {
                        for (int i = 0; i < bids.length(); i++) {
                            publishProgress(i);
                            JSONArray data = bids.getJSONArray(i);
                            OrderData orderData=new OrderData();
                            orderData.amount=data.getDouble(0);
                            orderData.value=data.getDouble(1);
                            bidsArray.add(orderData);
                        }
                    }
                    if (asks.length()>0) {
                        for (int i = 0; i < asks.length(); i++) {
                            publishProgress(i);
                            JSONArray data = asks.getJSONArray(i);
                            OrderData orderData=new OrderData();
                            orderData.amount=data.getDouble(0);
                            orderData.value=data.getDouble(1);
                            asksArray.add(orderData);
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
            return result;
        }
    }




}
