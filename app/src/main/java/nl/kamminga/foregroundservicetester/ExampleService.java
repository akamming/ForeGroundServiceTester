package nl.kamminga.foregroundservicetester;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * Created by arnold on 20-7-17.
 */
public class ExampleService extends Service {
    public static final String TAG = "ExampleService";
    private Handler h;
    private Runnable r;
    public String url;
    public int delay;
    private String responsetext = "";
    private RequestQueue queue;

    int counter = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification updateNotification() {
        counter++;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ForeGroundServiceTester.class), 0);

        String info = counter + "";

        Log.d(TAG,"trying to locate URL : "+url);



        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responsetext="Response succeeded : "+response.substring(0,80);
                        Log.d(TAG,responsetext);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse==null)
                {
                    responsetext = "That didn't work: Cannot reach url";
                } else {

                    responsetext = "That didn't work:" + error.networkResponse.statusCode;
                }
                Log.d(TAG,responsetext);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return new NotificationCompat.Builder(this)
                .setContentTitle(info)
                .setTicker(info)
                .setContentText(responsetext)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_stat_name), 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onstartcommand reached");
        url = intent.getStringExtra(ForeGroundServiceTester.URL);
        delay = intent.getIntExtra(ForeGroundServiceTester.DELAY, 1000);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);


        if (intent.getAction().contains("start")) {
            Log.d(TAG, "get action contains start");
            h = new Handler();
            r = new Runnable() {
                @Override
                public void run() {
                    startForeground(101, updateNotification());
                    Log.d(TAG, "Setting delay to " + delay);
                    h.postDelayed(this, delay);
                }
            };

            h.post(r);
        } else {
            Log.d(TAG, "does not contain start");
            h.removeCallbacks(r);
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Stopping Service");
    }

    @Override
    public void onCreate(){
        Toast.makeText(this,"service started", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Starting Service");
    }



}