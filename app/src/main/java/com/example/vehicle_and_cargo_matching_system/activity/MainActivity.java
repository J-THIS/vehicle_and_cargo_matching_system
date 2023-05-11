package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.bean.Driver;
import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;
import com.example.vehicle_and_cargo_matching_system.fragment.LineFragment;
import com.example.vehicle_and_cargo_matching_system.fragment.MineFragment;
import com.example.vehicle_and_cargo_matching_system.fragment.ResourceFragment;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Tab对应的按钮布局
    public LinearLayout resource_btn, mine_btn, line_btn;
    //Tab对应的ImageButton
    private ImageButton resource_ibt, mine_ibt, line_ibt;
    //Tab对应的ImageButton下的说明文字
    private TextView resource_text, mine_text, line_text;
    //Tab对应的Fragment
    private Fragment content;
    private ResourceFragment resource_tab;
    private LineFragment line_tab;
    private MineFragment mine_tab;

    private TextView tv_back,tv_title; //返回键和标题控件
    private long exitTime; //记录点击事件

    FragmentManager fm;

    private Driver driver;
    private String id,surname;

    private Double longitude, latitude;//经纬度信息
    private String mPosition = null;//用户当前所在位置地名
    private String mRegion = null;//用户当前所在地区地名
    private static String AK = "IDPp1BRd0WEmkon5y38jIskMmMRyk9MR";//百度地图api密钥
    FutureTask<String> futureTask;
    private ExecutorService executorService;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityStackUtil.getAppManager().addActivity(this);
        initData();
        try {
            initLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();//初始化控件
        initFragment();//初始化碎片
        initEvents();//初始化事件
        selectTab(resource_tab);
    }

    private void initData() {
        Intent intent = getIntent();
        driver = (Driver) intent.getExtras().getSerializable("driver");
        id = driver.getPhone();
        surname = driver.getSurName();
    }

    //初始化控件
    private void initViews() {
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.GONE);
        tv_title = findViewById(R.id.tv_title);

        resource_btn = findViewById(R.id.resource_btn);
        mine_btn = findViewById(R.id.mine_btn);
        line_btn = findViewById(R.id.line_btn);

        resource_ibt = findViewById(R.id.resource_tab_img);
        mine_ibt = findViewById(R.id.mine_tab_img);
        line_ibt = findViewById(R.id.line_tab_img);

        resource_text = findViewById(R.id.resource_text);
        mine_text = findViewById(R.id.mine_text);
        line_text = findViewById(R.id.line_text);
    }

    //为tab切换按钮绑定点击事件监听器
    private void initEvents() {
        resource_btn.setOnClickListener(this);
        mine_btn.setOnClickListener(this);
        line_btn.setOnClickListener(this);
    }

    private void initFragment() {
        content = new Fragment();
        resource_tab = ResourceFragment.newInstance(id, mPosition, mRegion);
        line_tab = LineFragment.newInstance(id);
        mine_tab = MineFragment.newInstance(id, surname);
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, content);
        transaction.commit();
    }

    public void onClick(View view) {
        //先将ImageButton置为灰色
        reset();
        //再将选中的ImageButton变为蓝色，并切换fragment
        switch (view.getId()) {
            case R.id.resource_btn:
                selectTab(resource_tab);
                //刷新数据
                try {
                    resource_tab.initData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mine_btn:
                selectTab(mine_tab);
                break;
            case R.id.line_btn:
                selectTab(line_tab);
                break;
        }
    }

    //将ImageButton全部设置为灰色,说明文字全部置为黑色，即未选中状态
    private void reset() {
        resource_ibt.setImageResource(R.drawable.ic_resource_black);
        resource_text.setTextColor(Color.parseColor("#000000"));
        mine_ibt.setImageResource(R.drawable.ic_mine_black);
        mine_text.setTextColor(Color.parseColor("#000000"));
        line_ibt.setImageResource(R.drawable.ic_line_black);
        line_text.setTextColor(Color.parseColor("#000000"));
    }

    private void selectTab(Fragment target) {
        //切换页面
        if (content != target){
            FragmentTransaction transaction = fm.beginTransaction();
            if (!target.isAdded()){
                transaction.hide(content).add(R.id.content,target).commit();
            }else{
                transaction.hide(content).show(target).commit();
            }
            content = target;
        }
        //根据点击的Tab设置对应的按钮为选中的颜色
        if (resource_tab.equals(target)) {
            tv_title.setText(R.string.resource_tab_name);
            resource_ibt.setImageResource(R.drawable.ic_resource_selected);
            resource_text.setTextColor(Color.parseColor("#3399FF"));
        } else if (mine_tab.equals(target)) {
            tv_title.setText(R.string.mine_tab_name);
            mine_ibt.setImageResource(R.drawable.ic_mine_selected);
            mine_text.setTextColor(Color.parseColor("#3399FF"));
        } else if (line_tab.equals(target)) {
            tv_title.setText(R.string.line_tab_name);
            line_ibt.setImageResource(R.drawable.ic_line_selected);
            line_text.setTextColor(Color.parseColor("#3399FF"));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private LocationListener locationListener = new LocationListener() {
//        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//
//        // Provider被enable时触发此函数，比如GPS被打开
//        @Override
//        public void onProviderEnabled(String provider) {
//        }
//
//        // Provider被disable时触发此函数，比如GPS被关闭
//        @Override
//        public void onProviderDisabled(String provider) {
//        }
//
//        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
//        @Override
//        public void onLocationChanged(Location location) {
//            if (location != null) {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
////                Toast.makeText(MainActivity.this, "longitude:"+longitude+"latitude:"+latitude, Toast.LENGTH_LONG).show();
//                Log.i("longitude:", longitude + "");
//                Log.i("latitude:", latitude + "");
//            }
//        }
//    };

    private void checkPermission(){
        //检查并申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    public void initLocation() throws Exception {
        checkPermission();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //获取所有可用的位置提供器
//        List providers = locationManager.getProviders(true);
//        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//            //如果是Network
//            locationProvider = LocationManager.NETWORK_PROVIDER;
//        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
//            //如果是GPS
//            locationProvider = LocationManager.GPS_PROVIDER;
//        } else {
//            //如果是Passive
//            locationProvider = LocationManager.PASSIVE_PROVIDER;
//        }
//        //获取最新的定位信息
//        Location location = locationManager.getLastKnownLocation(locationProvider);
//        //位置监听器实现每隔3s更新一次位置信息
//        locationManager.requestLocationUpdates(
//                locationProvider,//指定GPS定位的提供者
//                3000,//间隔时间
//                1,//位置更新之间的最小距离
//                locationListener//监听定位信息是否改变
//        );
//        //为avd上运行临时设定
////        longitude = 116.40441;
////        latitude = 39.907411;
//        longitude = 118.813091;
//        latitude = 31.88551;

        LocationClient.setAgreePrivacy(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        //LocationMode.Fuzzy_Locating, 模糊定位模式；v9.2.8版本开始支持，可以降低API的调用频率，但同时也会降低定位精度；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setFirstLocType(LocationClientOption.FirstLocType.SPEED_IN_FIRST_LOC);
        //可选，首次定位时可以选择定位的返回是准确性优先还是速度优先，默认为速度优先
        //可以搭配setOnceLocation(Boolean isOnceLocation)单次定位接口使用，当设置为单次定位时，setFirstLocType接口中设置的类型即为单次定位使用的类型
        //FirstLocType.SPEED_IN_FIRST_LOC:速度优先，首次定位时会降低定位准确性，提升定位速度；
        //FirstLocType.ACCUARACY_IN_FIRST_LOC:准确性优先，首次定位时会降低速度，提升定位准确性；

        option.setScanSpan(0);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public void getLocation(Double latitude, Double longitude){
        Toast.makeText(MainActivity.this, "longitude:"+longitude+"latitude:"+latitude, Toast.LENGTH_LONG).show();

        @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 2){
                    if (msg.obj.toString().equals("/")){
                        Toast.makeText(MainActivity.this,"获取用户位置失败！",Toast.LENGTH_LONG).show();
                        mPosition = "全国";
                        Log.i("mPosition", mPosition);
                        mRegion = "全国";
                        Log.i("mRegion", mRegion);
                    }else{
                        String[] str_message = msg.obj.toString().split("/");
                        mPosition = str_message[0];
                        Log.i("mPosition", mPosition);
                        mRegion = str_message[1];
                        Log.i("mRegion", mRegion);
                    }
                    resource_tab.initLocation(mPosition,mRegion);
                    line_tab.initLocation(mPosition,mRegion);
                    mine_tab.initLocation(mPosition,mRegion);
                }
            }
        };

        //在异步线程中访问百度api，通过逆地理编码，由经纬度获取定位地名
        futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String my_position = null;
                try {
                    URL resjson = new URL("https://api.map.baidu.com/reverse_geocoding/v3/?ak="
                            + AK + "&output=json&coordtype=wgs84ll&location=" + latitude + "," + longitude);
                    Log.i("longitude111:", longitude + "");
                    Log.i("latitude111:", latitude + "");
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            resjson.openStream()));
                    String res;
                    StringBuilder sb = new StringBuilder("");
                    while ((res = in.readLine()) != null) {
                        sb.append(res.trim());
                    }
                    in.close();
                    String location = sb.toString();
                    Log.i("location", location);
                    String address = JSON.parseObject(location).getJSONObject("result").getString("formatted_address");
                    JSONObject addressComponent = JSON.parseObject(location).getJSONObject("result").getJSONObject("addressComponent");
                    String province = addressComponent.getString("province");
                    String city = addressComponent.getString("city");
                    String district = addressComponent.getString("district");
                    String region = province + city + district;
                    my_position = address + "/" + region;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return my_position;
            }
        }) {
            //异步线程执行结束后会主动回调此方法
            @Override
            protected void done(){
                try {
                    //在done方法中调用get方法获取异步线程返回的执行结果，不会阻塞ui线程
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = futureTask.get();
                    handler.sendMessage(msg);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService = Executors.newFixedThreadPool(2);
        executorService.execute(futureTask);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            Toast.makeText(MainActivity.this, "error:"+errorCode, Toast.LENGTH_LONG).show();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            getLocation(latitude,longitude);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkPermission();
//        locationManager.requestLocationUpdates(
//                locationProvider,//指定GPS定位的提供者
//                3000,//间隔时间
//                1,//位置更新之间的最小距离
//                locationListener//监听定位信息是否改变
//        );
    }

    @Override
    protected void onPause() {
        super.onPause();
//        checkPermission();
//        locationManager.requestLocationUpdates(
//                locationProvider,//指定GPS定位的提供者
//                3000,//间隔时间
//                1,//位置更新之间的最小距离
//                locationListener//监听定位信息是否改变
//        );
    }


}
