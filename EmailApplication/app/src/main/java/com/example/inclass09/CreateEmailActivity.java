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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateEmailActivity extends AppCompatActivity {
    TextInputLayout spinnerLayout,subjectLayout,mailBodyLayout;
    EditText subject,mailBody;
    Spinner sendRecipient;
    Button sendEmail,cancel;
    UserSignUpResponse userData;
    ArrayList<SpinnerUser> users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        spinnerLayout=findViewById(R.id.textInputLayout_spinner_sendTo);
        subjectLayout=findViewById(R.id.textInputLayout_subject_CreateEmail);
        mailBodyLayout=findViewById(R.id.textInputLayout_emailMessage);
        subject=findViewById(R.id.editText_subject_CreateEmail);
        mailBody=findViewById(R.id.editText_emailMessage_CreateEmail);
        sendRecipient=findViewById(R.id.spinner_sendTo);
        sendEmail=findViewById(R.id.button_sendEmail);
        cancel=findViewById(R.id.button_cancel_CreateEmail);

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mySharedPref,MODE_PRIVATE);
        String jsonUserData= sharedpreferences.getString(MainActivity.sharedPreferencesKey,"");
        if(!jsonUserData.equals("")){
            Gson s= new Gson();
            userData=s.fromJson(jsonUserData,UserSignUpResponse.class);
        }

        if(isConnected())
            new GetUsersAsyncTask().execute(userData.token);
        else Toast.makeText(CreateEmailActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isConnected()){
                    SendEmailRequest request =validateInputs();
                    if(request.isValidRequest){
                        if(request.receiver_id!=null)
                            new SendEmailAsyncTask().execute(request);
                        else Toast.makeText(CreateEmailActivity.this,"Select a receiver",Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(CreateEmailActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



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


    public SendEmailRequest validateInputs(){
        SendEmailRequest request= new SendEmailRequest();
        subjectLayout.setError(null);
        mailBodyLayout.setError(null);
        request.isValidRequest=true;
        SpinnerUser sendTo=(SpinnerUser)sendRecipient.getSelectedItem();
        if(sendTo!=null)
            request.receiver_id=sendTo.id;
        String emailSubject=subject.getText().toString();
        if(emailSubject.equals("")){
            request.isValidRequest=false;
            subjectLayout.setError("Subject is required");
        }
        else{
            request.subject=emailSubject;
        }
        String emailBody=mailBody.getText().toString();
        if(emailBody.equals("")){
            request.isValidRequest=false;
            mailBodyLayout.setError("Email body is required");
        }
        else{
            request.message=emailSubject;
        }
        return request;
    }




    private class GetUsersAsyncTask extends AsyncTask<String, Integer, ArrayList<SpinnerUser>> {
        ProgressDialog pb;

        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(CreateEmailActivity.this);
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
        protected void onPostExecute(ArrayList<SpinnerUser> result) {
            pb.dismiss();
            if (result.size()==0) {
                Toast.makeText(CreateEmailActivity.this, "No user to send email", Toast.LENGTH_SHORT).show();
            } else {
                //show inbox

                sendRecipient=findViewById(R.id.spinner_sendTo);
                //spi.setSelection(index); // set to a value from the list of  data, give index;
                // create an adapter first, GENRE_LIST is the data list
                ArrayAdapter<SpinnerUser> adapter= new ArrayAdapter<SpinnerUser>(CreateEmailActivity.this,android.R.layout.simple_spinner_dropdown_item,result);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sendRecipient.setAdapter(adapter);
                sendRecipient.setSelection(0);
            }
        }

        @Override
        protected ArrayList<SpinnerUser> doInBackground(String... params) {

            String token = params[0];
            token="Bearer "+token;
            ArrayList<SpinnerUser> resultData = new ArrayList<SpinnerUser>();
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MainActivity.get_users_url)
                    .header("Authorization", token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Gson gson = new Gson();
                SpinnerResponse spinnerResponse= gson.fromJson(response.body().string(), SpinnerResponse.class);
                int count=0;
                for (SpinnerUser user : spinnerResponse.users) {
                    publishProgress(count++);
                    resultData.add(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultData;
        }

    }

    private class SendEmailAsyncTask extends AsyncTask<SendEmailRequest, Integer, Boolean> {
        ProgressDialog pb;

        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(CreateEmailActivity.this);
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
        protected void onPostExecute(Boolean result) {
            pb.dismiss();
            if (result==false) {
                Toast.makeText(CreateEmailActivity.this, "Could not send email", Toast.LENGTH_SHORT).show();
            } else {
                //show inbox
                Toast.makeText(CreateEmailActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                sendRecipient=findViewById(R.id.spinner_sendTo);
                sendRecipient.setSelection(0);
                mailBody=findViewById(R.id.editText_emailMessage_CreateEmail);
                mailBody.setText("");
                subject=findViewById(R.id.editText_subject_CreateEmail);
                subject.setText("");
            }
        }

        @Override
        protected Boolean doInBackground(SendEmailRequest... params) {
            boolean result=false;
            SendEmailRequest requestData = params[0];
            String token="Bearer "+userData.token;
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("receiver_id",requestData.receiver_id )
                    .add("subject", requestData.subject)
                    .add("message", requestData.message)
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.send_email_url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization",token)
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);{
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

    }



}
