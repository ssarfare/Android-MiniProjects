package com.example.inclass09;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class EmailAdapter extends ArrayAdapter<Email> {
    public Context inboxContext;
    public IUpdateInbox updateListner;
    public EmailAdapter(@NonNull Context context, int resource, @NonNull List<Email> objects) {
        super(context, resource, objects);
        inboxContext=context;
        updateListner= (IUpdateInbox) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Email n=getItem(position);
        ViewHolder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.emailrow,parent,false);
            holder= new ViewHolder();
            holder.iv_deleteEmail=(ImageView) convertView.findViewById(R.id.imageView_deleteEmail);
            holder.subject=(TextView) convertView.findViewById(R.id.textView_emailSubject);
            holder.date=(TextView) convertView.findViewById(R.id.textView_emailDate);
            convertView.setTag(holder);

            holder.iv_deleteEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isConnected()){
                        new DeleteEmailAsyncTask().execute(n.id);
                        updateListner.updateInbox();
                    }
                    else
                        Toast.makeText(inboxContext,"No internet connection",Toast.LENGTH_SHORT).show();
                }
            });
            //Picasso.get().load(url).into(view);
        }
        else{
            holder=(ViewHolder) convertView.getTag();
        }

        holder.subject.setText(n.subject);
        holder.date.setText(n.created_at);
        return convertView;


    }
    private  static class ViewHolder{
        TextView subject,date;
        ImageView iv_deleteEmail;
    }

    private class DeleteEmailAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog pb;
        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(inboxContext);
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
            DeleteEmailResponse data=s.fromJson(result,DeleteEmailResponse.class);
            if (data!=null && !data.status.equals("ok")) {
                Toast.makeText(inboxContext, "Could not delete email", Toast.LENGTH_SHORT).show();
            } else if(data!=null){
                //show inbox
                Toast.makeText(inboxContext, "deleted successfully", Toast.LENGTH_SHORT).show();


            }
        }

        @Override
        protected String doInBackground(String... params) {
            boolean result=false;
            String messageId = params[0];
            SharedPreferences sharedpreferences = inboxContext.getSharedPreferences(MainActivity.mySharedPref,MODE_PRIVATE);
            String jsonUserData= sharedpreferences.getString(MainActivity.sharedPreferencesKey,"");
            Gson s= new Gson();
            UserSignUpResponse data=s.fromJson(jsonUserData,UserSignUpResponse.class);
            String token="Bearer "+data.token;
            String url=MainActivity.delete_message_url+messageId;
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization",token)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);{
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) inboxContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }













}
