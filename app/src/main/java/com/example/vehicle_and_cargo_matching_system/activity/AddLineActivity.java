package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.LineDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.LineDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;
import com.example.vehicle_and_cargo_matching_system.util.AddressUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddLineActivity extends AppCompatActivity {

    private TextView tv_title,tv_back,tv_load_place,tv_unload_place,tv_selection_title,
            tv_use_now_position,tv_next_level,tv_province1,tv_province2,tv_city,tv_back1,tv_back2;
    private Button btn_add_load_place,btn_add_unload_place,btn_add_line_confirm,btn_submit,
            btn_delete_load_place,btn_delete_unload_place;
    private LinearLayout ll_place_selection,ll_province_tab,ll_city_tab,ll_district_tab;
    private ConditionViewGroup cvg_use_type,cvg_car_length,cvg_car_type,cvg_province,cvg_city,cvg_district;
    private String id,mRegion,load_place,unload_place,use_type,car_length,car_type,start_province,
            start_city,start_district,end_province,end_city,end_district;
    private int attention_num;
    private LineDao lineDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_line);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        mRegion = getIntent().getStringExtra("mRegion");
        attention_num = getIntent().getIntExtra("attention_num",-1);
        load_place = null;
        unload_place = null;
        use_type = "不限";
        car_length = "不限";
        car_type = "不限";
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        btn_add_load_place.setOnClickListener(onClick);
        btn_add_unload_place.setOnClickListener(onClick);
        btn_delete_load_place.setOnClickListener(onClick);
        btn_delete_unload_place.setOnClickListener(onClick);
        btn_add_line_confirm.setOnClickListener(onClick);
        tv_use_now_position.setOnClickListener(onClick);
        tv_next_level.setOnClickListener(onClick);
        tv_back1.setOnClickListener(onClick);
        tv_back2.setOnClickListener(onClick);
        btn_submit.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.btn_add_load_place:
                    ll_place_selection.setVisibility(View.VISIBLE);
                    tv_selection_title.setText("选择装货地");
                    //创建一个从底部滑出的动画
                    Animation animation1 = AnimationUtils.loadAnimation(
                            AddLineActivity.this, R.anim.slide_bottom_to_top);
                    ll_place_selection.startAnimation(animation1);//将动画加载到地点选择界面
                    refresh();
                    ll_province_tab.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_add_unload_place:
                    ll_place_selection.setVisibility(View.VISIBLE);
                    tv_selection_title.setText("选择卸货地");
                    //创建一个从底部滑出的动画
                    Animation animation2 = AnimationUtils.loadAnimation(
                            AddLineActivity.this, R.anim.slide_bottom_to_top);
                    ll_place_selection.startAnimation(animation2);//将动画加载到地点选择界面
                    refresh();
                    ll_province_tab.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_use_now_position:
                    if (tv_selection_title.getText()=="选择装货地"){
                        load_place = mRegion;
                        String str_start = get_region(mRegion);
                        set_textView(tv_load_place,str_start);
                        btn_add_load_place.setVisibility(View.GONE);
                        btn_delete_load_place.setVisibility(View.VISIBLE);
                    }else if (tv_selection_title.getText()=="选择卸货地"){
                        unload_place = mRegion;
                        String str_end = get_region(mRegion);
                        set_textView(tv_unload_place, str_end);
                        btn_add_unload_place.setVisibility(View.GONE);
                        btn_delete_unload_place.setVisibility(View.VISIBLE);
                    }
                    ll_place_selection.setVisibility(View.GONE);
                    break;
                case R.id.tv_next_level:
                    if (ll_province_tab.getVisibility()==View.VISIBLE){
                        if (cvg_province.getChosen_id() == 0){//当前选中的省级行政区是“全国”
                            break;
                        }
                        ll_province_tab.setVisibility(View.GONE);
                        ll_city_tab.setVisibility(View.VISIBLE);
                        //初始化该省级行政区下的市级行政区面板
                        String str_province1;
                        if (tv_selection_title.getText()=="选择装货地"){
                            str_province1 = start_province + " >";
                        }else{
                            str_province1 = end_province + " >";
                        }
                        tv_province1.setText(str_province1);
                        final ArrayList<String> cityText = new ArrayList<>();
                        //StringBuilder和StringBuffer功能类似,存储字符串
                        StringBuilder builder = new StringBuilder();
                        readJson(builder);
                        //builder.toString() 返回表示此序列中数据的字符串 (就是json串，后面自行解析就行)
                        List<AddressUtil> addressUtils = JSON.parseArray(builder.toString(), AddressUtil.class);
                        if (tv_selection_title.getText()=="选择装货地"){
                            for (int i = 0; i < addressUtils.size(); i++) {
                                AddressUtil addressUtil = addressUtils.get(i);
                                if (Objects.equals(addressUtil.getName(), start_province)){
                                    List<AddressUtil.Child> child = addressUtil.getCityList();
                                    for (int j = 0; j < child.size(); j++) {
                                        cityText.add(child.get(j).getName());
                                    }
                                    break;
                                }
                            }
                        }else{
                            for (int i = 0; i < addressUtils.size(); i++) {
                                AddressUtil addressUtil = addressUtils.get(i);
                                if (Objects.equals(addressUtil.getName(), end_province)){
                                    List<AddressUtil.Child> child = addressUtil.getCityList();
                                    for (int j = 0; j < child.size(); j++) {
                                        cityText.add(child.get(j).getName());
                                    }
                                    break;
                                }
                            }
                        }
                        cvg_city = ll_place_selection.findViewById(R.id.cvg_city);
                        cvg_city.addItemViews(cityText, ConditionViewGroup.TEV_MODE);
                        cvg_city.chooseItemStyle(0);
                        //设置默认市级行政区
                        if (tv_selection_title.getText()=="选择装货地"){
                            start_city = cityText.get(0);
                        }else {
                            end_city = cityText.get(0);
                        }
                        cvg_city.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
                            @Override
                            public void onGroupItemClick(int item) {
                                if (tv_selection_title.getText()=="选择装货地"){
                                    start_city = cityText.get(item);
                                }else {
                                    end_city = cityText.get(item);
                                }
                            }
                        });
                    }else if (ll_city_tab.getVisibility()==View.VISIBLE){
                        ll_city_tab.setVisibility(View.GONE);
                        ll_district_tab.setVisibility(View.VISIBLE);
                        tv_next_level.setVisibility(View.GONE);
                        //初始化该省、市级行政区下的区级行政区面板
                        String str_province2,str_city;
                        if (tv_selection_title.getText()=="选择装货地"){
                            str_province2 = start_province + " >";
                            str_city = start_city + " >";
                        }else{
                            str_province2 = end_province + " >";
                            str_city = end_city + " >";
                        }
                        tv_province2.setText(str_province2);
                        tv_city.setText(str_city);
                        final ArrayList<String> districtText = new ArrayList<>();
                        //StringBuilder和StringBuffer功能类似,存储字符串
                        StringBuilder builder = new StringBuilder();
                        readJson(builder);
                        //builder.toString() 返回表示此序列中数据的字符串 (就是json串，后面自行解析就行)
                        List<AddressUtil> addressUtils = JSON.parseArray(builder.toString(), AddressUtil.class);
                        if (tv_selection_title.getText()=="选择装货地"){
                            for (int i = 0; i < addressUtils.size(); i++) {
                                AddressUtil addressUtil = addressUtils.get(i);
                                if (Objects.equals(addressUtil.getName(), start_province)){
                                    List<AddressUtil.Child> child = addressUtil.getCityList();
                                    for (int j = 0; j < child.size(); j++) {
                                        if (Objects.equals(child.get(j).getName(), start_city)){
                                            List<AddressUtil.Child.Grandchild> grandchild = child.get(j).getAreaList();
                                            for (int k = 0; k < grandchild.size(); k++) {
                                                districtText.add(grandchild.get(k).getName());
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }else{
                            for (int i = 0; i < addressUtils.size(); i++) {
                                AddressUtil addressUtil = addressUtils.get(i);
                                if (Objects.equals(addressUtil.getName(), end_province)){
                                    List<AddressUtil.Child> child = addressUtil.getCityList();
                                    for (int j = 0; j < child.size(); j++) {
                                        if (Objects.equals(child.get(j).getName(), end_city)){
                                            List<AddressUtil.Child.Grandchild> grandchild = child.get(j).getAreaList();
                                            for (int k = 0; k < grandchild.size(); k++) {
                                                districtText.add(grandchild.get(k).getName());
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        cvg_district = ll_place_selection.findViewById(R.id.cvg_district);
                        cvg_district.addItemViews(districtText, ConditionViewGroup.TEV_MODE);
                        cvg_district.chooseItemStyle(0);
                        if (tv_selection_title.getText()=="选择装货地"){
                            start_district = districtText.get(0);
                        }else {
                            end_district = districtText.get(0);
                        }
                        cvg_district.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
                            @Override
                            public void onGroupItemClick(int item) {
                                if (tv_selection_title.getText()=="选择装货地"){
                                    start_district = districtText.get(item);
                                }else {
                                    end_district = districtText.get(item);
                                }
                            }
                        });
                    }
                    break;
                case R.id.tv_back1:
                    ll_city_tab.setVisibility(View.GONE);
                    ll_province_tab.setVisibility(View.VISIBLE);
                    if (tv_selection_title.getText()=="选择装货地"){
                        start_city = null;
                        start_district = null;
                    }else {
                        end_city = null;
                        end_district = null;
                    }
                    break;
                case R.id.tv_back2:
                    ll_district_tab.setVisibility(View.GONE);
                    ll_city_tab.setVisibility(View.VISIBLE);
                    tv_next_level.setVisibility(View.VISIBLE);
                    if (tv_selection_title.getText()=="选择装货地"){
                        start_district = null;
                    }else {
                        end_district = null;
                    }
                    break;
                case R.id.btn_submit:
                    if (cvg_province.getChosen_id() == 0){//当前选中的省级行政区是“全国”
                        if (tv_selection_title.getText()=="选择装货地"){
                            load_place = "全国";
                            set_textView(tv_load_place,load_place);
                            btn_add_load_place.setVisibility(View.GONE);
                            btn_delete_load_place.setVisibility(View.VISIBLE);
                        }else {
                            unload_place = "全国";
                            set_textView(tv_unload_place,unload_place);
                            btn_add_unload_place.setVisibility(View.GONE);
                            btn_delete_unload_place.setVisibility(View.VISIBLE);
                        }
                        ll_place_selection.setVisibility(View.GONE);
                        break;
                    }else if (ll_district_tab.getVisibility()!=View.VISIBLE){//若当前不在选择区级行政区
                        Toast.makeText(AddLineActivity.this, "请选择完整地址", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (tv_selection_title.getText()=="选择装货地"){
                        load_place = start_province + start_city + start_district;
                        String str_start = start_city+start_district;
                        set_textView(tv_load_place,str_start);
                        btn_add_load_place.setVisibility(View.GONE);
                        btn_delete_load_place.setVisibility(View.VISIBLE);
                    }else {
                        unload_place = end_province + end_city + end_district;
                        String str_end = end_city + end_district;
                        set_textView(tv_unload_place,str_end);
                        btn_add_unload_place.setVisibility(View.GONE);
                        btn_delete_unload_place.setVisibility(View.VISIBLE);
                    }
                    ll_place_selection.setVisibility(View.GONE);
                    break;
                case R.id.btn_delete_load_place:
                    load_place = null;
                    tv_load_place.setVisibility(View.GONE);
                    btn_delete_load_place.setVisibility(View.GONE);
                    btn_add_load_place.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_delete_unload_place:
                    unload_place = null;
                    tv_unload_place.setVisibility(View.GONE);
                    btn_delete_unload_place.setVisibility(View.GONE);
                    btn_add_unload_place.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_add_line_confirm:
                    try {
                        if (load_place == null||unload_place == null){
                            Toast.makeText(AddLineActivity.this, "请选择装卸地点",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                        setData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void setData() throws SQLException {
        lineDao = new LineDaoImpl();
        //用Handler处理异步线程中传来的数据
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==11){
                    Toast.makeText(ActivityStackUtil.getAppManager().
                                    getActivity(MainActivity.class), "添加成功！",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };
        //在异步线程中访问数据库获取关注线路数据集
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lineDao.addLineAttention(id,attention_num,load_place,unload_place,use_type,
                            car_length,car_type);
                    Message message = Message.obtain();
                    message.what = 11;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void refresh(){
        ll_province_tab.setVisibility(View.GONE);
        cvg_province.chooseItemStyle(0);
        ll_city_tab.setVisibility(View.GONE);
        ll_district_tab.setVisibility(View.GONE);
    }

    /*由于ui列表项显示空间有限，只能将原有mRegion信息裁剪成“市+区”地名才能装得下，
    也就是要去掉省级地名。百度地图api返回的mRegion形式中，特别行政区示例为“香港香港荃湾区”，
    直辖市示例为“北京市北京市东城区”*/
    private String get_region(String load_region){
        String str_load_region = "";
        if (Objects.equals(load_region, "全国")){
            return load_region;
        }
        if (load_region.contains("省")){//处理一般省级行政区
            str_load_region = load_region.split("省")[1];
        }else if (load_region.contains("自治区")){//处理自治区
            str_load_region = load_region.split("自治区")[1];
        }else if (load_region.contains("香港")||load_region.contains("澳门")){//处理特别行政区
            str_load_region = load_region.substring(2);
        }else{//处理直辖市
            str_load_region = load_region.substring(3);
        }
        return str_load_region;
    }

    //动态创建TextView
    private void set_textView(TextView textView,String str){
        if (textView.getVisibility() == View.GONE){
            textView.setVisibility(View.VISIBLE);
            textView.setText(get_region(str));
        }else{
            textView.setVisibility(View.GONE);
        }
    }

    private void readJson(StringBuilder builder){
        try {
            //InputStreamReader 将字节输入流转换为字符流
            InputStreamReader isr = new InputStreamReader(getAssets().open("address.json"), "UTF-8");
            //包装字符流,将字符流放入缓存里
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                //append 被选元素的结尾(仍然在内部)插入指定内容,缓存的内容依次存放到builder中
                builder.append(line);
            }
            br.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("添加常用线路");
        tv_back = findViewById(R.id.tv_back);
        tv_load_place = findViewById(R.id.tv_load_place);
        tv_unload_place = findViewById(R.id.tv_unload_place);
        btn_add_load_place = findViewById(R.id.btn_add_load_place);
        btn_add_unload_place = findViewById(R.id.btn_add_unload_place);
        btn_delete_load_place = findViewById(R.id.btn_delete_load_place);
        btn_delete_unload_place = findViewById(R.id.btn_delete_unload_place);
        btn_add_line_confirm = findViewById(R.id.btn_add_line_confirm);
        ll_place_selection = findViewById(R.id.ll_place_selection);
        ll_province_tab = findViewById(R.id.ll_province_tab);
        ll_city_tab = findViewById(R.id.ll_city_tab);
        ll_district_tab = findViewById(R.id.ll_district_tab);
        tv_selection_title = findViewById(R.id.tv_selection_title);
        tv_use_now_position = findViewById(R.id.tv_use_now_position);
        tv_next_level = findViewById(R.id.tv_next_level);
        tv_province1 = findViewById(R.id.tv_province1);
        tv_province2 = findViewById(R.id.tv_province2);
        tv_city = findViewById(R.id.tv_city);
        tv_back1 = findViewById(R.id.tv_back1);
        tv_back2 = findViewById(R.id.tv_back2);
        btn_submit = findViewById(R.id.btn_submit);
        final ArrayList<String> provinceText = new ArrayList<>();
        provinceText.add("全国");
        StringBuilder builder = new StringBuilder();
        readJson(builder);
        //builder.toString() 返回表示此序列中数据的json串
        Log.i("builder.toString()",builder.toString());
        List<AddressUtil> addressUtils = JSON.parseArray(builder.toString(), AddressUtil.class);
        for (int i = 0; i < addressUtils.size(); i++) {
            AddressUtil addressUtil = addressUtils.get(i);
            provinceText.add(addressUtil.getName());
        }
        cvg_province = findViewById(R.id.cvg_province);
        cvg_province.addItemViews(provinceText, ConditionViewGroup.TEV_MODE);
        cvg_province.chooseItemStyle(0);
        cvg_province.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                if (item!=0){
                    tv_next_level.setVisibility(View.VISIBLE);
                }
                if (tv_selection_title.getText()=="选择装货地"){
                    start_province = provinceText.get(item);
                }else if (tv_selection_title.getText()=="选择卸货地"){
                    end_province = provinceText.get(item);
                }
            }
        });

        final ArrayList<String> useTypeText = new ArrayList<>();
        useTypeText.add("不限"); useTypeText.add("整车"); useTypeText.add("零担");
        cvg_use_type = findViewById(R.id.cvg_use_type);
        cvg_use_type.addItemViews(useTypeText, ConditionViewGroup.TEV_MODE);
        cvg_use_type.chooseItemStyle(0);
        cvg_use_type.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                use_type = useTypeText.get(item);
            }
        });

        final ArrayList<String> carLengthText = new ArrayList<>();
        carLengthText.add("不限");
        carLengthText.add("1.8"); carLengthText.add("2.7"); carLengthText.add("3.8");
        carLengthText.add("4.2"); carLengthText.add("5"); carLengthText.add("6.2");
        carLengthText.add("6.8"); carLengthText.add("7.7"); carLengthText.add("8.2");
        carLengthText.add("8.7"); carLengthText.add("9.6"); carLengthText.add("11.7");
        carLengthText.add("12.5"); carLengthText.add("13"); carLengthText.add("13.7");
        carLengthText.add("15"); carLengthText.add("16"); carLengthText.add("17.5");
        cvg_car_length = findViewById(R.id.cvg_car_length);
        cvg_car_length.addItemViews(carLengthText, ConditionViewGroup.TEV_MODE);
        cvg_car_length.chooseItemStyle(0);
        cvg_car_length.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                car_length = carLengthText.get(item);
            }
        });

        final ArrayList<String> carTypeText = new ArrayList<>();
        carTypeText.add("不限"); carTypeText.add("平板"); carTypeText.add("高栏");
        carTypeText.add("厢式"); carTypeText.add("集装箱"); carTypeText.add("自卸");
        carTypeText.add("冷藏"); carTypeText.add("保温"); carTypeText.add("高低板");
        carTypeText.add("面包车"); carTypeText.add("棉被车"); carTypeText.add("爬梯车");
        carTypeText.add("飞翼车"); carTypeText.add("依维柯");
        cvg_car_type = findViewById(R.id.cvg_car_type);
        cvg_car_type.addItemViews(carTypeText, ConditionViewGroup.TEV_MODE);
        cvg_car_type.chooseItemStyle(0);
        cvg_car_type.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                car_type = carTypeText.get(item);
            }
        });
    }
}