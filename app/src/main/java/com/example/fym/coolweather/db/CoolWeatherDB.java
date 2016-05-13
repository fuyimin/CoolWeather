package com.example.fym.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fym.coolweather.model.City;
import com.example.fym.coolweather.model.County;
import com.example.fym.coolweather.model.Province;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/5/12 0012.
 */
public class CoolWeatherDB {
         /*
          *数据库名
          */
         public static  final  String dbName="cool_weather";

        //数据库版本号
         public  static  final  int version=1;

         private static  CoolWeatherDB coolWeatherDB;

         private SQLiteDatabase db;

         //构造函数私有化
         private CoolWeatherDB(Context context) {
                CoolWeatherOpenHelper coolWeatherOpenHelper=new CoolWeatherOpenHelper(context,dbName,null,version);
                db=coolWeatherOpenHelper.getWritableDatabase();
         }

         //单例模式提供类的实例
        public synchronized static CoolWeatherDB getInstance(Context context){
            if (coolWeatherDB==null){
                coolWeatherDB=new CoolWeatherDB(context);
            }
            return  coolWeatherDB;
        }

        //向Province表中插入数据
        public void saveProvince(Province province){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }

         //提取Province表中的数据
         public List<Province> getProvince(){
             Cursor cursor=db.query("Province",null,null,null,null,null,null);
             List<Province> provinceList=new ArrayList<>();
             if (cursor.moveToFirst()){
                 while (cursor.moveToNext()){
                     String  provinceName=cursor.getString(cursor.getColumnIndex("province_name"));
                     String  provinceCode=cursor.getString(cursor.getColumnIndex("province_code"));
                     int  id=cursor.getInt(cursor.getColumnIndex("id"));
                     Province province=new Province();
                     province.setProvinceName(provinceName);
                     province.setProvinceCode(provinceCode);
                     province.setId(id);
                     provinceList.add(province);
                 }
             }

             if (cursor!=null){
                 cursor.close();
             }
             return  provinceList;
         }

        //向city表里存储数据
       public  void  saveCity(City city){
           ContentValues values=new ContentValues();
           values.put("city_name",city.getCityName());
           values.put("city_code",city.getCityCode());
           values.put("province_id",city.getProvinceId());
           db.insert("City",null,values);
       }

       //获取某个province下city表的数据
       public  List<City> getCity(int provinceId){
           List<City> cityList=new ArrayList<>();
           Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
           if (cursor.moveToFirst()){
               while (cursor.moveToNext()){
                   String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
                   String cityCode=cursor.getString(cursor.getColumnIndex("city_code"));
                   int  id=cursor.getInt(cursor.getColumnIndex("id"));
                   City city=new City();
                   city.setId(id);
                   city.setCityName(cityName);
                   city.setCityCode(cityCode);
                   city.setProvinceId(provinceId);
                   cityList.add(city);
               }
           }
           if (cursor!=null){
               cursor.close();
           }
           return  cityList;
       }

      //向County表添加数据
      public void  saveCounty(County county){
          ContentValues values=new ContentValues();
          values.put("county_name",county.getCountyName());
          values.put("county_code",county.getCountyCode());
          values.put("city_id",county.getCityId());
          db.insert("County",null,values);
      }

     //获取County表里的数据
    public  List<County> getCounty(int cityId){
        List<County> countyList=new ArrayList<>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null,null);
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                String countyName=cursor.getString(cursor.getColumnIndex("county_name"));
                String countyCode=cursor.getString(cursor.getColumnIndex("county_code"));
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                County county=new County();
                county.setId(id);
                county.setCityId(cityId);
                county.setCountyCode(countyCode);
                county.setCountyName(countyName);
                countyList.add(county);
            }
        }
        if (cursor!=null){
            cursor.close();
        }
        return  countyList;
    }
}
