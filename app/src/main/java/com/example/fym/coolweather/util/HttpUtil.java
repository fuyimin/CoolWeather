package com.example.fym.coolweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while (reader.readLine() != null) {
                        line = reader.readLine();
                        sb.append(line);
                    }
                    if (httpCallBackListener != null) {
                        httpCallBackListener.onFinish(sb.toString());
                    }

                } catch (Exception e) {
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
