
package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Inbox extends AppCompatActivity implements IUpdateInbox {
    ImageView logout,createNewEmail;
    TextView name;
    ListView listView;
    UserSignUpResponse userData;

    @Override
    protected void onResume() {
        super.onResume();

        if(isConnected()){
            new GetEmailsAsyncTask().execute(userData.token);
        }else{
            Toast.makeText(Inbox.this,"No internet connection",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        logout=findViewById(R.id.iv_logout);
        createNewEmail=findViewById(R.id.iv_add);
        listView=findViewById(R.id.listview);
        name=findViewById(R.id.tv_title);
        if(getIntent().getSerializableExtra(MainActivity.user_data)!=null){
            userData= (UserSignUpResponse) getIntent().getSerializableExtra(MainActivity.user_data);
            name.setText(userData.user_fname+" "+userData.user_lname);
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isConnected()){
                    SharedPreferences.Editor editor = getSharedPreferences(MainActivity.mySharedPref,MODE_PRIVATE).edit();
                    editor.remove(MainActivity.sharedPreferencesKey).commit();

                    Intent i = new Intent(Inbox.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }else{
                    Toast.makeText(Inbox.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }



            }
        });

        createNewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Inbox.this, CreateEmailActivity.class);

                startActivity(i);
            }
        });
    }

    @Override
    public void updateInbox() {

        if(isConnected()){
            new GetEmailsAsyncTask().execute(userData.token);
        }
        else Toast.makeText(Inbox.this,"No internet connection",Toast.LENGTH_SHORT).show();

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }



    private class GetEmailsAsyncTask extends AsyncTask<String, Integer, ArrayList<Email>> {
        ProgressDialog pb;

        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(Inbox.this);
            pb.setMessage("Loading");
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
        protected void onPostExecute(ArrayList<Email> result) {
            pb.dismiss();
                if(result.size()==0)
                    Toast.makeText(Inbox.this, "No emails to view", Toast.LENGTH_SHORT).show();

                //show inbox
                EmailAdapter adapter= new EmailAdapter(Inbox.this,R.layout.emailrow,result);
                listView=findViewById(R.id.listview);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //this is where the code for calling new display activity will be placed
                    }
                });



        }

        @Override
        protected ArrayList<Email> doInBackground(String... params) {

            String token = params[0];
            token="Bearer "+token;
            ArrayList<Email> resultData = new ArrayList<Email>();
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MainActivity.inbox_url)
                    .header("Authorization", token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Gson gson = new Gson();
                EmailResponse emailResponse = gson.fromJson(response.body().string(), EmailResponse.class);
                int count=0;
                for (Email e : emailResponse.messages) {
                    e.created_at=formatDateTime(e.created_at);
                    publishProgress(count++);
                    resultData.add(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultData;
        }

    }
    public String formatDateTime(String input){
        String output="";
        if(input==null || input.equals("null")){
            return output;
        }
        else{
            PrettyTime p = new PrettyTime();
            try{
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date d= format.parse(input);
                output=p.format(d);
                return output;
            }catch (Exception e){
                return null;
            }
        }
    }

}

