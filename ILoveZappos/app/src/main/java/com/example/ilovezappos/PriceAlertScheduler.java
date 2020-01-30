package com.example.ilovezappos;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;


public class PriceAlertScheduler extends Worker{
    String api="https://www.bitstamp.net/api/v2/ticker_hour/btcusd";
    public PriceAlertScheduler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(api);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                JSONObject data = new JSONObject(json);
                checkPrice(data.getDouble("last"));
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return Result.success();
    }

    private void showNotification(double value){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "101")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Price Alert")
                .setContentText("Current bit coin price less than"+value+"$. Click to open the app")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification nf = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1,nf);
    }

    private void checkPrice(Double value){
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferenceKey),Context.MODE_PRIVATE);
        double storedValue=sharedPref.getFloat(context.getString(R.string.priceValue),0.0f);
        if(storedValue>=value){
            showNotification(storedValue);
        }
    }
}