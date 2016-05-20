package com.example.fym.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fym.coolweather.R;
import com.example.fym.coolweather.db.CoolWeatherDB;
import com.example.fym.coolweather.model.City;
import com.example.fym.coolweather.model.County;
import com.example.fym.coolweather.model.Province;
import com.example.fym.coolweather.util.HttpCallBackListener;
import com.example.fym.coolweather.util.HttpUtil;
import com.example.fym.coolweather.util.Utility;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16 0016.
 */
public class ChooseAreaActivity extends Activity{
       public static  final int PROVINCE_LEVEL=0;
       public static final int CITY_LEVEL=1;
       public static final int COUNTY_LEVEL=2;

       private TextView textView;
       private ListView listView;
       private ArrayAdapter<String> adapter;
       private CoolWeatherDB coolWeatherDB;
       private  ProgressDialog progressDialog;
       //存储临时数据
       private List<String> dataList=new ArrayList<>();
       private List<Province> provinceList;
       private List<City> cityList;
       private  List<County> countyList;
       private  Province selectedProvince;
       private  City selectedCity;
       private int Current_level;
       private  boolean isfromWeatherActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        textView=(TextView)findViewById(R.id.title_text);
        listView=(ListView)findViewById(R.id.list_view);
        isfromWeatherActivity=getIntent().getBooleanExtra("is_from_weatherActivity",false);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Current_level == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (Current_level == CITY_LEVEL) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (Current_level == COUNTY_LEVEL) {
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("countyCode", countyCode);
                    startActivity(intent);

                }
            }
        });

        if (!isfromWeatherActivity){
            queryProvinces();
        }else{
            Log.d("selectedCity",selectedCity.toString());
        }

    }

    //优先从数据库中查询，如果没有再到服务器上查询
    public void queryProvinces(){
        provinceList=coolWeatherDB.getProvince();

        if (provinceList.size()>0){
             dataList.clear();
             for (Province province:provinceList){
                 dataList.add(province.getProvinceName());
             }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            Current_level=PROVINCE_LEVEL;

        }else{
            queryFromServer(null,"Province");
        }
    }

    //优先从数据库中查询选中省份中的城市，如果没有再到服务器上查询
    public void queryCities(){
        cityList=coolWeatherDB.getCity(selectedProvince.getId());

        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            Current_level=CITY_LEVEL;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"City");
        }
    }

    //优先从数据库中查询选中城市下的城镇，如果没有再到服务器上查询
    public void queryCounties(){
        countyList=coolWeatherDB.getCounty(selectedCity.getId());

        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            Current_level=COUNTY_LEVEL;
        }else{
            queryFromServer(selectedCity.getCityCode(),"County");
        }
    }

    //从服务器查询数据并写进数据库，传入code和type
    public void queryFromServer(final String code, final String type){
        String address;

        if (!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.setHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if (type.equals("Province")){
                    result= Utility.handleProvinceResponse(coolWeatherDB,response);
                    Log.d("ChooseAreaActivity","true");
                }else if (type.equals("City")){
                    result=Utility.handleCityResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if (type.equals("County")){
                    result=Utility.handleCountyResponse(coolWeatherDB,response,selectedCity.getId());
                }

                if (result){
                    //因为要进行ui操作所以要回到主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type.equals("Province")){
                               queryProvinces();
                            }else if (type.equals("City")){
                                queryCities();
                            }else if (type.equals("County")){
                                queryCounties();
                            }
                            closeProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT);
                      }
                  });
            }
        });

    }

    //显示进度对话框
    public void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("数据正在加载.....");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    //消除进度对话框
    public  void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    //捕获back键，根据当前级别来判断，此刻应该返回什么列表
    @Override
    public void onBackPressed() {
        if (Current_level==PROVINCE_LEVEL){
             System.exit(0);
        }else if (Current_level==CITY_LEVEL){
            queryProvinces();
        }else if (Current_level==COUNTY_LEVEL){
            queryCities();
        }
    }
}
