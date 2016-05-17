package com.example.fym.coolweather.util;

import android.text.TextUtils;

import com.example.fym.coolweather.db.CoolWeatherDB;
import com.example.fym.coolweather.model.City;
import com.example.fym.coolweather.model.County;
import com.example.fym.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

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
}
