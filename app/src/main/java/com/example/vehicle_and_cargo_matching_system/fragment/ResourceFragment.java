package com.example.vehicle_and_cargo_matching_system.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.activity.ConditionViewGroup;
import com.example.vehicle_and_cargo_matching_system.activity.LoginActivity;
import com.example.vehicle_and_cargo_matching_system.activity.MainActivity;
import com.example.vehicle_and_cargo_matching_system.adapter.ResourceAdapter;
import com.example.vehicle_and_cargo_matching_system.bean.Driver;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.DriverDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.AddressUtil;
import com.example.vehicle_and_cargo_matching_system.view.ResourceListView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class ResourceFragment extends Fragment{
    private ResourceListView rlv_resource; //列表控件
    private ResourceAdapter resourceAdapter; //列表的适配器
    private List<Resource> resourceList;
    private ResourceDao resourceDao;
    private DriverDao driverDao;
    private Driver driver;
    private String id = "";
    private String mPosition = "";
    private String mRegion = "";
    private TextView tv_start,tv_end,tv_condition,tv_yes,tv_selection_title,tv_use_now_position,
            tv_next_level,tv_province1,tv_province2,tv_city,tv_back1,tv_back2;
    private View filter;
    private BottomSheetDialog bottomSheetDialog;
    private ConditionViewGroup cvg_sort,cvg_resource_quality,cvg_cargo,cvg_car_length,cvg_car_type,
            cvg_province,cvg_city,cvg_district;
    private int sort,resource_quality;
    private String load_region,unload_region,cargo,car_length,car_type,start_province,start_city,
            start_district,end_province,end_city,end_district;
    private LinearLayout ll_place_selection,ll_province_tab,ll_city_tab,ll_district_tab;
    private Button btn_submit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            id = bundle.getString("id");
            mPosition = bundle.getString("mPosition");
            mRegion = bundle.getString("mRegion");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_resource,container,false);
        initView(view);
        initEvent(view);
        initCondition();
        return view;
    }

    private void initCondition() {
        unload_region = "全国";
        resource_quality = 0;
        cargo = "不限";
        driverDao = new DriverDaoImpl();
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==24){
                    driver = (Driver) msg.obj;
                    if (driver.getCarType()!=null && driver.getUseLength()!=null){//若用户已完善车辆信息
                        car_length = driver.getUseLength().toString();
                        car_type = driver.getCarType();
                    }else{//若用户未完善车辆信息
                        car_length = "不限";
                        car_type = "不限";
                    }
                    //刷新数据集
                    try {
                        initData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //在异步线程中访问数据库获取数据集
        Thread thread = new Thread(new Runnable() {
            Driver driver = new Driver();
            @Override
            public void run() {
                try {
                    driver = driverDao.getDriver(id);
                    Message message = Message.obtain();
                    message.what = 24;
                    message.obj = driver;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initEvent(View view) {
        OnClick onClick = new OnClick();
        tv_start.setOnClickListener(onClick);
        tv_end.setOnClickListener(onClick);
        tv_condition.setOnClickListener(onClick);
        tv_yes.setOnClickListener(onClick);
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
            switch (v.getId()){
                case R.id.tv_start:
                    ll_place_selection.setVisibility(View.VISIBLE);
                    tv_selection_title.setText("选择装货地");
                    //创建一个从底部滑出的动画
                    Animation animation1 = AnimationUtils.loadAnimation(
                            getActivity(), R.anim.slide_bottom_to_top);
                    ll_place_selection.startAnimation(animation1);//将动画加载到地点选择界面
                    refresh();
                    ll_province_tab.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_end:
                    ll_place_selection.setVisibility(View.VISIBLE);
                    tv_selection_title.setText("选择卸货地");
                    //创建一个从底部滑出的动画
                    Animation animation2 = AnimationUtils.loadAnimation(
                            getActivity(), R.anim.slide_bottom_to_top);
                    ll_place_selection.startAnimation(animation2);//将动画加载到地点选择界面
                    refresh();
                    ll_province_tab.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_condition:
                    bottomSheetDialog.show();
                    break;
                case R.id.yes:
                    try {
                        initData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    bottomSheetDialog.hide();
                    break;
                case R.id.tv_use_now_position:
                    if (tv_selection_title.getText()=="选择装货地"){
                        String str_start = get_region(mRegion);
                        tv_start.setText(str_start);
                        load_region = mRegion;
                    }else {
                        String str_end = get_region(mRegion);
                        tv_end.setText(str_end);
                        unload_region = mRegion;
                    }
                    ll_place_selection.setVisibility(View.GONE);
                    try {
                        initData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
                            load_region = "全国";
                            tv_start.setText("全国");
                        }else {
                            unload_region = "全国";
                            tv_end.setText("全国");
                        }
                        try {
                            initData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        ll_place_selection.setVisibility(View.GONE);
                        break;
                    }else if (ll_district_tab.getVisibility()!=View.VISIBLE){//若当前不是在选择区级行政区
                        Toast.makeText(getActivity(), "请选择完整地址", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (tv_selection_title.getText()=="选择装货地"){
                        load_region = start_province + start_city + start_district;
                        String str_start = start_city+start_district;
                        tv_start.setText(str_start);
                    }else {
                        unload_region = end_province + end_city + end_district;
                        String str_end = end_city + end_district;
                        tv_end.setText(str_end);
                    }
                    try {
                        initData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    ll_place_selection.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void readJson(StringBuilder builder){
        try {
            //InputStreamReader 将字节输入流转换为字符流
            InputStreamReader isr = new InputStreamReader(getActivity().getAssets().open("address.json"), "UTF-8");
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

    public void initLocation(String mPosition, String mRegion){
        this.mPosition = mPosition;
        this.mRegion = mRegion;
        load_region = mRegion;
        tv_start.setText(get_region(load_region));
        tv_end.setText("全国");
        initAdapter();
    }

    //用于Activity向Fragment中传入数据
    public static ResourceFragment newInstance(String id, String mPosition, String mRegion){
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("mPosition", mPosition);
        bundle.putString("mRegion", mRegion);
        ResourceFragment resourceFragment = new ResourceFragment();
        resourceFragment.setArguments(bundle);
        return resourceFragment;
    }

    public void initData() throws SQLException {
        resourceDao = new ResourceDaoImpl();
        //用Handler处理异步线程中传来的数据
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==1){
                    resourceList = (List<Resource>) msg.obj;
                    if (sort == 0){//按净得运费排序
                        resourceList.sort(new PureFreightComparator());
                        Collections.reverse(resourceList);
                    }else{//按发布时间排序
                        resourceList.sort(new ReleaseTimeComparator());
                    }
                    if (resourceList.size()!=0){
                        resourceAdapter.setData(resourceList);//将货源集数据传递到adapter中
                        rlv_resource.setAdapter(resourceAdapter);
                    }
                }
            }
        };
        //在异步线程中访问数据库获取关注线路数据集
        Thread thread = new Thread(new Runnable() {
            List<Resource> list = new ArrayList<>();
            @Override
            public void run() {
                try {
                    list = resourceDao.getResource(load_region,unload_region,resource_quality,cargo,
                            null,car_length,car_type);
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = list;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initAdapter(){
        resourceAdapter = new ResourceAdapter(getActivity(),mPosition,mRegion,id);
        try {
            initData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initView(View view) {
        rlv_resource = view.findViewById(R.id.rlv_resource);
        tv_start = view.findViewById(R.id.tv_start);
        tv_end = view.findViewById(R.id.tv_end);
        tv_condition = view.findViewById(R.id.tv_condition);
        ll_place_selection = view.findViewById(R.id.ll_place_selection);
        ll_province_tab = view.findViewById(R.id.ll_province_tab);
        ll_city_tab = view.findViewById(R.id.ll_city_tab);
        ll_district_tab = view.findViewById(R.id.ll_district_tab);
        tv_selection_title = view.findViewById(R.id.tv_selection_title);
        tv_use_now_position = view.findViewById(R.id.tv_use_now_position);
        tv_next_level = view.findViewById(R.id.tv_next_level);
        tv_province1 = view.findViewById(R.id.tv_province1);
        tv_province2 = view.findViewById(R.id.tv_province2);
        tv_city = view.findViewById(R.id.tv_city);
        tv_back1 = view.findViewById(R.id.tv_back1);
        tv_back2 = view.findViewById(R.id.tv_back2);
        btn_submit = view.findViewById(R.id.btn_submit);
        //为选择筛选条件弹窗绑定布局
        filter = LayoutInflater.from(getActivity()).inflate(R.layout.filter,null);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(filter);
        tv_yes = filter.findViewById(R.id.yes);

        final ArrayList<String> sortText = new ArrayList<>();
        sortText.add("按净得运费排序");
        sortText.add("按发布时间排序");
        cvg_sort = filter.findViewById(R.id.cvg_sort);
        cvg_sort.addItemViews(sortText, ConditionViewGroup.TEV_MODE);
        cvg_sort.chooseItemStyle(0);
        cvg_sort.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                if (sortText.get(item).equals("按净得运费排序")){
                    sort = 0;
                }else{
                    sort = 1;
                }
            }
        });

        final ArrayList<String> resourceText = new ArrayList<>();
        resourceText.add("不限");
        resourceText.add("订金可退");
        resourceText.add("货主信用四星以上");
        cvg_resource_quality = filter.findViewById(R.id.cvg_resource_quality);
        cvg_resource_quality.addItemViews(resourceText, ConditionViewGroup.TEV_MODE);
        cvg_resource_quality.chooseItemStyle(0);
        cvg_resource_quality.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                if (resourceText.get(item).equals("订金可退")){
                    resource_quality = 1;
                }else if (resourceText.get(item).equals("货主信用四星以上")){
                    resource_quality = 2;
                }else{
                    resource_quality = 0;
                }
            }
        });

        final ArrayList<String> cargoText = new ArrayList<>();
        cargoText.add("不限");
        cargoText.add("蔬果生鲜"); cargoText.add("煤炭矿产"); cargoText.add("金属钢材");
        cargoText.add("机械设备"); cargoText.add("家具家居"); cargoText.add("食品饮料");
        cargoText.add("服饰纺织"); cargoText.add("车辆");cargoText.add("其他");
        cvg_cargo = filter.findViewById(R.id.cvg_cargo);
        cvg_cargo.addItemViews(cargoText, ConditionViewGroup.TEV_MODE);
        cvg_cargo.chooseItemStyle(0);
        cvg_cargo.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                cargo = cargoText.get(item);
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
        cvg_car_length = filter.findViewById(R.id.cvg_car_length);
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
        cvg_car_type = filter.findViewById(R.id.cvg_car_type);
        cvg_car_type.addItemViews(carTypeText, ConditionViewGroup.TEV_MODE);
        cvg_car_type.chooseItemStyle(0);
        cvg_car_type.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                car_type = carTypeText.get(item);
            }
        });

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
        cvg_province = view.findViewById(R.id.cvg_province);
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
    }

    // 自定义比较器：按净得运费价格排序
    static class PureFreightComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Resource p1 = (Resource) object1; // 强制转换
            Resource p2 = (Resource) object2;
            BigDecimal t1,t2;
            if (p1.getIfReturn()==0){
                t1 = p1.freight.subtract(p1.deposit);
            }else{
                t1 = p1.getFreight();
            }
            if (p2.getIfReturn()==0){
                t2 = p2.freight.subtract(p2.deposit);
            }else{
                t2 = p2.getFreight();
            }
            return t1.compareTo(t2);
        }
    }

    // 自定义比较器：按发布时间排序
    static class ReleaseTimeComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Resource p1 = (Resource) object1; // 强制转换
            Resource p2 = (Resource) object2;
            return p2.releaseTime.compareTo(p1.releaseTime);
        }
    }

}