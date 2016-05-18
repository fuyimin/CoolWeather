package com.example.fym.coolweather.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.fym.coolweather.db.CoolWeatherDB;
import com.example.fym.coolweather.model.City;
import com.example.fym.coolweather.model.County;
import com.example.fym.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/5/16 0016.
 */
public class Utility {
  //解析服务器返回的省级数据
   public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
       if (!TextUtils.isEmpty(response)){
           String[] codeAndProvince=response.split(",");
           if (codeAndProvince.length>0){
               for (String str:codeAndProvince){
                   String[] arrays=str.split("\\|");
                   Province province=new Province();
                   province.setProvinceCode(arrays[0]);
                   province.setProvinceName(arrays[1]);
                   coolWeatherDB.saveProvince(province);
               }
               return true;
           }
       }
       return false;
   }

    //解析服务器返回的市级数据
    public  static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] codeAndCity=response.split(",");
            if (codeAndCity.length>0){
                for (String str:codeAndCity){
                    String[] arrays=str.split("\\|");
                    City city=new City();
                    city.setCityCode(arrays[0]);
                    city.setCityName(arrays[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return  true;
            }
        }
        return  false;
    }

    //解析服务器返回的县级数据
    public  static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){

        if (!TextUtils.isEmpty(response)){
            String[] codeAndCounty=response.split(",");
            if (codeAndCounty.length>0){
                for (String str:codeAndCounty){
                    String[] arrays=str.split("\\|");
                    County county=new County();
                    county.setCountyCode(arrays[0]);
                    county.setCountyName(arrays[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }

        return  false;
    }

    //解析服务器返回的json格式的天气信息,然后用SharedPreference存储
    public static boolean handleWeatherResponse(Context context,String response){
        try {
            if (!TextUtils.isEmpty(response)){
                JSONObject jsonObject=new JSONObject(response);
                JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
                String cityName=weatherInfo.getString("city");
                String  cityId=weatherInfo.getString("cityid");
                String temp1=weatherInfo.getString("temp1");
                String temp2=weatherInfo.getString("temp2");
                String weather=weatherInfo.getString("weather");
                String publishTime=weatherInfo.getString("ptime");

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy年mm月dd日", Locale.CHINA);
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("cityName",cityName);
                editor.putString("cityCode",cityId);
                editor.putString("temp1",temp1);
                editor.putString("temp2",temp2);
                editor.putString("weatherDesc",weather);
                editor.putString("pTime",publishTime);
                editor.putString("data",sdf.format(new Date()));
                editor.commit();

                return  true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
