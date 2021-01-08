package com.example.c2w1t2_bindingservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
Курс Coursera по андроиду: Многопоточность и сетевое взаимодействие. Неделя 1, Задание на Services, Receivers.
 */

public class MyBoundService extends Service {

    public static final String TAG = MyBoundService.class.getSimpleName();
    private static int progressValue = 0;


    public MyBoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        progressValue = intent.getIntExtra(MainActivity.CURRENT_PROGRESS_VALUE,0);
        Log.d(TAG, "###== onBind: ");
        return new MyBinder();
    }

    public class MyBinder extends Binder{
        public MyBoundService getService(){
            return MyBoundService.this;
        }
    }

    public int getProgressValue(){
        return progressValue;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "###== onCreate: ");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                progressValue+=(int)(MainActivity.current_progress_bar_scale*0.05);
                Log.d(TAG, "###== start activity in service run: "+ progressValue);
            }
        },0,MainActivity.PERIOD_OF_PROGRESS_BAR, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "###== onDestroy: ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "###== onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "###== onRebind: ");
    }

}
