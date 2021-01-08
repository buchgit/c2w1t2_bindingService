package com.example.c2w1t2_bindingservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CURRENT_PROGRESS_VALUE = "current progress value";
    public static final int PERIOD_OF_PROGRESS_BAR = 1000;
    public static final int MAX_PROGRESS_VALUE = 1000;
    public static int current_progress_bar_scale = MAX_PROGRESS_VALUE;
    private ProgressBar progressBar;
    public static int progressValue = 0;
    private MyBoundService myBoundService;
    private TextView textViewMin;
    private TextView textViewMax;
    private TextView textViewCurrentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnReset = findViewById(R.id.btn_reset);
        progressBar = findViewById(R.id.pr_bar_main);
        textViewMin = findViewById(R.id.tv_main_min);
        textViewMax = findViewById(R.id.tv_main_max);
        textViewCurrentValue = findViewById(R.id.tv_main_current_value);

        progressBar.setMin(progressValue);
        progressBar.setMax(MAX_PROGRESS_VALUE);

        textViewMin.setText("min is: " + 0);
        textViewMax.setText("max is:" + MAX_PROGRESS_VALUE);
        textViewCurrentValue.setText("current value is: " + progressValue);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressValue > MAX_PROGRESS_VALUE / 2) {
                    progressBar.setMin(MAX_PROGRESS_VALUE / 2);
                    textViewMin.setText("min is:" + MAX_PROGRESS_VALUE / 2);
                } else {
                    progressBar.setMin(progressValue);
                    textViewMin.setText("min is:" + progressValue);
                }
            }
        });

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "###== onServiceConnected: ");
                myBoundService = ((MyBoundService.MyBinder) service).getService();
                final ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
                es.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        progressValue = myBoundService.getProgressValue();
                        Log.d(TAG, "###== progressValue in mainActivity: " + progressValue);
                        if (progressValue <= current_progress_bar_scale) {
                            progressBar.setProgress(progressValue);
                            textViewCurrentValue.setText("current value is: " + progressValue);
                        }
                    }
                }, 0, PERIOD_OF_PROGRESS_BAR, TimeUnit.MILLISECONDS);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "###== onServiceDisconnected: ");
            }
        };

        Intent intent = new Intent(MainActivity.this, MyBoundService.class);
        intent.putExtra(CURRENT_PROGRESS_VALUE, progressValue);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

    }
}