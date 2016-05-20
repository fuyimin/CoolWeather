package com.example.fym.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fym.coolweather.R;
import com.example.fym.coolweather.service.UpdateWeatheService;
import com.example.fym.coolweather.util.HttpCallBackListener;
import com.example.fym.coolweather.util.HttpUtil;
import com.example.fym.coolweather.util.Utility;



/**
 * Created by Administrator on 2016/5/17 0017.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private TextView cityName;
    private TextView  pubTime;
    private  TextView currentDate;
    private TextView weatherDesc;
    private  TextView temp1;
    private  TextView temp2;
    private LinearLayout weatherInfoLayout;
    private Button home;
    private  Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        cityName=(TextView)findViewById(R.id.city_name);
        pubTime=(TextView)findViewById(R.id.pubTime);
        currentDate=(TextView)findViewById(R.id.current_date);
        weatherDesc=(TextView)findViewById(R.id.desc);
        temp1=(TextView)findViewById(R.id.lowTemp);
        temp2=(TextView)findViewById(R.id.highTemp);
        home=(Button)findViewById(R.id.home_button);
        refresh=(Button)findViewById(R.id.rfresh_button);
        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);

        home.setOnClickListener(this);
        refresh.setOnClickListener(this);

        String countyCode=getIntent().getStringExtra("countyCode");
        if (!TextUtils.isEmpty(countyCode)){
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeatherInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_button:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                startActivity(intent);
                finish();
                break;
            case  R.id.rfresh_button:
                SharedPreferences sdf=PreferenceManager.getDefaultSharedPreferences(this);
                String weather_code=sdf.getString("cityCode",".");
                cityName.setText("同步中。。。");

                if (!TextUtils.isEmpty(weather_code)){
                    queryWeatherInfo(weather_code);

                }
                break;
        }
    }

    public void queryWeatherCode(String countyCode){
          String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";

          queryFromServer(address,"countyCode");
    }

    public  void queryWeatherInfo(String weatherCode){
          String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

          queryFromServer(address, "weatherCode");
    }

    public void queryFromServer(final String address,final String type){
        HttpUtil.setHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (type.equals("countyCode")) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] arrays = response.split("\\|");
                        if (arrays != null && arrays.length == 2) {
                            String weatherCode = arrays[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if (type.equals("weatherCode")) {
                    result = Utility.handleWeatherResponse(WeatherActivity.this, response);
                }

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo();
                        }
                    });
                }


            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "加载失败", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    public void showWeatherInfo(){
        SharedPreferences spf= PreferenceManager.getDefaultSharedPreferences(this);
        String city_Name=spf.getString("cityName", ".");
        String highTemp=spf.getString("temp1",".");
        String lowTemp=spf.getString("temp2",".");
        String weather_Desc=spf.getString("weatherDesc",".");
        String pTime=spf.getString("pTime",".");
        String data=spf.getString("data",".");


        cityName.setText(city_Name);

        pubTime.setText("今天" + pTime + "发布");
        temp1.setText(lowTemp);
        temp2.setText(highTemp);
        weatherDesc.setText(weather_Desc);
        currentDate.setText(data);
        weatherInfoLayout.setVisibility(View.VISIBLE);

        Intent intent=new Intent(this, UpdateWeatheService.class);
        startActivity(intent);
    }


}
