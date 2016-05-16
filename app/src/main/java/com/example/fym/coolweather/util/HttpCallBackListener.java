package com.example.fym.coolweather.util;

/**
 * Created by Administrator on 2016/5/13 0013.
 */
public interface HttpCallBackListener {
     void onFinish(String response);

     void  onError(Exception e);
}
