package com.example.fym.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.fym.coolweather.receiver.AutoUpdateReceiver;
import com.example.fym.coolweather.util.HttpCallBackListener;
import com.example.fym.coolweather.util.HttpUtil;
import com.example.fym.coolweather.util.Utility;

/**
 * Created by Administrator on 2016/5/19 0019.
 */
public class UpdateWeatheService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int hours=8*60*60*1000;
        long triggerTime= SystemClock.elapsedRealtime()+hours;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,triggerTime,pIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeather(){
        SharedPreferences spf= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=spf.getString("cityCode", ".");
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

        HttpUtil.setHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(UpdateWeatheService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
