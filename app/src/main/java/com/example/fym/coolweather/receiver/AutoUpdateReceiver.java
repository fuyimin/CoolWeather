package com.example.fym.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fym.coolweather.service.UpdateWeatheService;

/**
 * Created by Administrator on 2016/5/19 0019.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, UpdateWeatheService.class);
        context.startActivity(i);
    }
}
