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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    EditText fname, lname, email, password, cpwd;
    Button button_signup, button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fname=findViewById(R.id.et_signup_fname);
        lname=findViewById(R.id.et_signup_lname);
        email=findViewById(R.id.et_signup_email);
        password=findViewById(R.id.et_signup_pwd);
        cpwd=findViewById(R.id.et_signup_cpwd);

        button_signup=findViewById(R.id.button_signup);
        button_cancel=findViewById(R.id.signup_cancel);


        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    UserSignUpRequest request= validaInputs();
                    if(request.isValid){
                        new SignUpAsyncTask().execute(request);
                    }
                }else{
                    Toast.makeText(SignUp.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(SignUp.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public UserSignUpRequest validaInputs(){
        fname.setError(null);
        lname.setError(null);
        email.setError(null);
        password.setError(null);
        cpwd.setError(null);
        UserSignUpRequest data= new UserSignUpRequest("","","","");
        String fname1=fname.getText().toString();
        if(fname1.equals("")){
            data.isValid=false;
            fname.setError("First name is required");
        }else{
            data.fname=fname1;
        }
        String lname1=lname.getText().toString();
        if(lname1.equals("")){
            data.isValid=false;
            lname.setError("Last name is required");
        }else{
            data.lname=lname1;
        }

        String email1=email.getText().toString();
        if(email1.equals("")){
            data.isValid=false;
            email.setError("Email is required");
        }else{
            data.email=email1;
        }

        String password1=password.getText().toString();
        if(password1.equals("")){
            data.isValid=false;
            password.setError("Password is required");
        }
        String password2=cpwd.getText().toString();
        if(password2.equals("")){
            data.isValid=false;
            cpwd.setError("Repeat password is required");
        }

        if(!password1.equals("")&&!password2.equals("") && password1.equals(password2)){
            data.password=password1;
        }else{
            data.isValid=false;
            cpwd.setError("Passwords do not match");
        }
        return data;
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



    private class SignUpAsyncTask extends AsyncTask<UserSignUpRequest, Integer, String> {
        ProgressDialog pb;

        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(SignUp.this);
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
        protected void onPostExecute(String result) {
            pb.dismiss();
            Gson s= new Gson();
            UserSignUpResponse data=s.fromJson(result,UserSignUpResponse.class);
            if (data.status.equals("error")) {
                Toast.makeText(SignUp.this, data.message, Toast.LENGTH_SHORT).show();
            } else {
                //show inbox
                Toast.makeText(SignUp.this, "sign up successful", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(MainActivity.mySharedPref,MODE_PRIVATE).edit();
                editor.putString(MainActivity.sharedPreferencesKey, result);
                editor.commit();

                Intent i = new Intent(SignUp.this, Inbox.class);
                i.putExtra(MainActivity.user_data,data);
                startActivity(i);
                finish();
            }
        }

        @Override
        protected String doInBackground(UserSignUpRequest... params) {

            UserSignUpRequest userToSignUp = params[0];
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("email",userToSignUp.email )
                    .add("password", userToSignUp.password)
                    .add("fname", userToSignUp.fname)
                    .add("lname", userToSignUp.lname)
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.sign_up_url)
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return  response.body().string();
                }
                else{
                    return  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
           // return null;
        }
    }
}
