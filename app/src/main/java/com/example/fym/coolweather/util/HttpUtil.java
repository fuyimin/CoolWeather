package com.example.fym.coolweather.util;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/5/13 0013.
 */
public class HttpUtil {
    public static void setHttpRequest(final String address, final HttpCallBackListener httpCallBackListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line=reader.readLine() )!= null) {
                        sb.append(line);
                    }
                    if (httpCallBackListener != null) {
                        httpCallBackListener.onFinish(sb.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    httpCallBackListener.onError(e);

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
