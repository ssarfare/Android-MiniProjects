package com.example.ilovezappos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    Button setPriceAlertBtn,deleteAlertBtn;
    TextView tv_alert;
    String notificationChannelId="101";
    CardView transactionCardview,orderBookCardView;
    ConstraintLayout alertConstraint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        transactionCardview=findViewById(R.id.transactionCard);
        orderBookCardView=findViewById(R.id.orderBookCard);
        setPriceAlertBtn=findViewById(R.id.button_setPriceAlert);
        deleteAlertBtn=findViewById(R.id.button_deleteAlert);
        alertConstraint=findViewById(R.id.alertConstraint);
        tv_alert=findViewById(R.id.tv_alert);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferenceKey),Context.MODE_PRIVATE);
        double storedValue=sharedPref.getFloat(getString(R.string.priceValue),0.0f);
        if(storedValue==0.0f)
            alertConstraint.setVisibility(View.INVISIBLE);
        else{
            alertConstraint.setVisibility(View.VISIBLE);
            tv_alert.setText("Alert set for price "+storedValue+"$");
        }
        transactionCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,TransactionHistory.class);
                startActivity(i);
            }
        });

        orderBookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,OrderBook.class);
                startActivity(i);
            }
        });

        setPriceAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });

        deleteAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferenceKey),Context.MODE_PRIVATE);
                String workId=sharedPref.getString(getString(R.string.workId),null);
                if(workId!=null){
                    alertConstraint.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor editor=sharedPref.edit();
                    editor.clear();
                    editor.commit();
                    WorkManager.getInstance(MainActivity.this).cancelWorkById(UUID.fromString(workId));
                    Toast.makeText(MainActivity.this,"Price Alert Cancelled",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void showAlertDialogButtonClicked(View view) {
        AlertDialog dialog = null;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Price Alert");
        final View customLayout = getLayoutInflater().inflate(R.layout.input_dialog_box, null);
        builder.setView(customLayout);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = customLayout.findViewById(R.id.et_alert_price_value);
                if(editText.getText()!=null && !editText.getText().toString().equals("")){

                    try{
                        double val=Double.parseDouble(editText.getText().toString());
                        sendDialogDataToActivity(editText.getText().toString());
                        alertConstraint.setVisibility(View.VISIBLE);
                        tv_alert.setText("Alert set for price "+val+"$");
                    }catch (Exception ex){
                        Log.e("Main Activity", "onClick: error parsing input",ex );
                        Toast.makeText(MainActivity.this,"Oops something went wrong!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog = builder.create();
        dialog.show();

    }

    private void sendDialogDataToActivity(String data) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferenceKey),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getString(R.string.priceValue), (float) Double.parseDouble(data));

        Constraints constraints=new Constraints.Builder().setRequiresCharging(false).setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest getRequest=new PeriodicWorkRequest.Builder(PriceAlertScheduler.class,1, TimeUnit.HOURS).setConstraints(constraints).build();
        editor.putString(getString(R.string.workId),getRequest.getId().toString());
        editor.commit();
        WorkManager.getInstance(getApplicationContext()).enqueue(getRequest);
        Toast.makeText(this, "Alert created for price:"+data+"$", Toast.LENGTH_SHORT).show();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            String description = "Price Alert Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notificationChannelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
