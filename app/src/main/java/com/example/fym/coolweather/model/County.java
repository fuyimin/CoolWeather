package com.example.fym.coolweather.model;

/**
 * Created by Administrator on 2016/5/12 0012.
 */
public class County {
    int id;
    String  countyName;
    String  countyCode;
    int cityId;

    public County(){

    }

    public County(int id, int cityId, String countyCode, String countyName) {
        this.id = id;
        this.cityId = cityId;
        this.countyCode = countyCode;
        this.countyName = countyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
