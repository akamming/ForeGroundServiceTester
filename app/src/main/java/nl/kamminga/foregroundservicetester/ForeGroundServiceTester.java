package nl.kamminga.foregroundservicetester;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ForeGroundServiceTester extends AppCompatActivity {

    private static final String TAG = "ForeGroundServiceTester";
    public static final String URL = "nl.kamminga.foregroundservicetester.URL";
    public static final String DELAY = "nl.kamminga.foregroundservicetester.delay";
    // private boolean ServiceIsRunning = false;
    public boolean CountDownIsRunning = false;
    private int countdowncounter;
    private boolean countdownshouldbestopped = false;
    private CountDownTimer countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fore_ground_service_tester);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        if (isMyServiceRunning(ExampleService.class)){
            Toast.makeText(this,"Service is running", Toast.LENGTH_SHORT).show();

            Button button = (Button) findViewById(R.id.StartForeGroundService);
            button.setText("Stop");
        } else {
            Toast.makeText(this,"Service is not running", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fore_ground_service_tester, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Start(View view) {
        Log.d(TAG, "Pressed Start Button");
        if (isMyServiceRunning(ExampleService.class)) {
            Stopmaar();
        } else {
            if (CountDownIsRunning) {
                StopCountdown();
            }
            Startmaar();
        }
    }

    public void DelayedStart(View view) {
        if (isMyServiceRunning(ExampleService.class)) {
            Log.d(TAG, "Service already running, doing nothing");
        } else {
            if (CountDownIsRunning) {
                Log.d(TAG, "Countdown already running, stopping timer");
                StopCountdown();
            } else {
                Log.d(TAG, "Pressed Delayed Start Button");
                StartCountdown();
            }
        }
    }

    private void StopCountdown(){
        // countdownshouldbestopped = true;
        Toast.makeText(this,"Stopping Countdown", Toast.LENGTH_SHORT).show();
        countdown.cancel();
        CountDownIsRunning=false;
        Button bt = (Button) findViewById(R.id.DelayedStartButton);
        bt.setText(R.string.delayedbuttonname);
    }

    private void StartCountdown() {
        Toast.makeText(this, "Starting Countdown", Toast.LENGTH_SHORT).show();

        CountDownIsRunning = true;
        EditText et = (EditText) findViewById(R.id.DelayedStartInSeconds);
        countdowncounter = Integer.valueOf(et.getText().toString()).intValue();

        Log.d(TAG, "timer set to " + countdowncounter + " seconds");
        // Countdown();
        countdown = new CountDownTimer(countdowncounter*1000,1000) {
            @Override
            public void onTick(long l) {
                Button bt = (Button) findViewById(R.id.DelayedStartButton);
                bt.setText("Starting in " + l/1000 + " seconds");
            }

            @Override
            public void onFinish() {
                Button bt = (Button) findViewById(R.id.DelayedStartButton);
                bt.setText(R.string.delayedbuttonname);
                Startmaar();
            }
        }.start();
    }

    public void Startmaar(){
        Log.d(TAG, "startmaar()");


        // Make sure we know the service is running
        // ServiceIsRunning=true;


        // Change button text
        Button button=(Button)findViewById(R.id.StartForeGroundService);
        button.setText("Stop");

        // Get values from form
        EditText et = (EditText) findViewById(R.id.URL);
        EditText delay = (EditText) findViewById(R.id.DelayInMilliSeconds);

        // Start Service
        Intent startIntent = new Intent(this, ExampleService.class);
        startIntent.setAction("start");
        startIntent.putExtra(URL,et.getText().toString());
        startIntent.putExtra(DELAY,Integer.valueOf(delay.getText().toString()).intValue());
        startService(startIntent);
    }

    public void Stopmaar(){
        Log.d(TAG,"Stopmaar()");

        // ServiceIsRunning=false;

        // Change button text
        Button button=(Button)findViewById(R.id.StartForeGroundService);
        button.setText("Start");


        Intent stopIntent = new Intent(this, ExampleService.class);
        stopIntent.setAction("stop");
        startService(stopIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
