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
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import android.os.Handler;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Http2Reader;

public class MainActivity extends AppCompatActivity {

    String token = "";
    //public static SharedPreferences sharedpreferences;
    public static String mySharedPref="LoggedInUserData";
    public static String sharedPreferencesKey="userData";
    public static String sign_up_url="http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup";
    public static String inbox_url="http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox";
    public static String user_data="userData";
    public static String login_url="http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login";
    public static String get_users_url="http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users";
    public static String send_email_url="http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add";
    public static String delete_message_url="http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/";
    public EditText email,password;
    public Button signUp,login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email = (EditText)findViewById(R.id.et_mailer_email);
        password = (EditText)findViewById(R.id.et_mailer_pwd);
        signUp = (Button) findViewById(R.id.et_mailer_signup);
        login = (Button) findViewById(R.id.et_mailer_login);
        //sharedpreferences = getSharedPreferences(MyFAVORITES, Context.MODE_PRIVATE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
                password.setText("");
                Intent i = new Intent(MainActivity.this, SignUp.class);
                startActivity(i);
                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    UserSignUpRequest loginRequest=validateInputs();

                    if(loginRequest.isValid){
                        new LoginAsyncTask().execute(loginRequest);
                    }

                }else{
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mySharedPref,MODE_PRIVATE);
        String jsonUserData= sharedpreferences.getString(MainActivity.sharedPreferencesKey,"");
        if(!jsonUserData.equals("")){
            Gson s= new Gson();
            UserSignUpResponse data=s.fromJson(jsonUserData,UserSignUpResponse.class);
            Intent i = new Intent(MainActivity.this, Inbox.class);
            i.putExtra(MainActivity.user_data,data);
            startActivity(i);
            finish();
        }

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


    public UserSignUpRequest validateInputs(){
        email.setError(null);
        password.setError(null);
        UserSignUpRequest returnData=new UserSignUpRequest("","","","");
        String emailString=email.getText().toString();
        if(emailString.equals("")){
            email.setError("Invalid email");
            returnData.isValid=false;
        }else
        {
            returnData.email=emailString;
        }

        String pass=password.getText().toString();
        if(pass.equals("")||pass.length()<6){
            returnData.isValid=false;
            password.setError("Password should be minimum 6 characters");
        }else{
            returnData.password=pass;
        }
        return returnData;
    }
    private class LoginAsyncTask extends AsyncTask<UserSignUpRequest, Integer, String> {
        ProgressDialog pb;

        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(MainActivity.this);
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
                    Toast.makeText(MainActivity.this, data.message, Toast.LENGTH_SHORT).show();
            } else {
                //show inbox
                Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                Context context = MainActivity.this;
                //MainActivity.sharedpreferences = MainActivity.this.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = getSharedPreferences(MainActivity.mySharedPref,MODE_PRIVATE).edit();
                editor.putString(MainActivity.sharedPreferencesKey, result);
                editor.commit();
                Intent i = new Intent(MainActivity.this, Inbox.class);
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
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.login_url)
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return response.body().string();
                }
                else{
                    return  response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
             return null;
        }
    }




}
