package com.example.vehicle_and_cargo_matching_system.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.adapter.ResourceAdapter;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;
import com.example.vehicle_and_cargo_matching_system.fragment.ResourceFragment;
import com.example.vehicle_and_cargo_matching_system.view.ResourceListView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResourceActivity extends AppCompatActivity {
    private ResourceListView rlv_resource; //列表控件
    private ResourceAdapter resourceAdapter; //列表的适配器
    private List<Resource> resourceList;
    private ResourceDao resourceDao;
    private String id = "";
    private String mPosition = "";
    private String mRegion = "";
    private TextView tv_yes,tv_title,tv_back;
    private Button btn_condition;
    private View filter;
    private BottomSheetDialog bottomSheetDialog;
    private ConditionViewGroup cvg_sort,cvg_resource_quality,cvg_cargo,cvg_car_length,cvg_car_type;
    private int sort,resource_quality;
    private String load_region,unload_region,cargo,use_type,car_length,car_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        initView();
        initEvent();
        initData();
        initCondition();
        initAdapter();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        mPosition = getIntent().getStringExtra("mPosition");
        load_region = getIntent().getStringExtra("load_place");
        unload_region = getIntent().getStringExtra("unload_place");
        use_type = getIntent().getStringExtra("use_type");
        car_length = getIntent().getStringExtra("car_length");
        car_type = getIntent().getStringExtra("car_type");
    }

    private void initCondition() {
        sort = 0;
        resource_quality = 0;
        cargo = "不限";
        car_length = "不限";
        car_type = "不限";
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        btn_condition.setOnClickListener(onClick);
        tv_yes.setOnClickListener(onClick);
        tv_back.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.btn_condition:
                    bottomSheetDialog.show();
                    break;
                case R.id.yes:
                    try {
                        setData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    bottomSheetDialog.hide();
                    break;
            }
        }
    }

    private void setData() throws SQLException {
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
                    resourceAdapter.setData(resourceList);//将货源集数据传递到adapter中
                    rlv_resource.setAdapter(resourceAdapter);
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
                            use_type,car_length,car_type);
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
        resourceAdapter = new ResourceAdapter(this,mPosition,mRegion,id);
        try {
            setData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        rlv_resource = findViewById(R.id.rlv_resource);
        btn_condition = findViewById(R.id.btn_condition);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("货源列表");
        tv_back = findViewById(R.id.tv_back);
        //为选择筛选条件弹窗绑定布局
        filter = LayoutInflater.from(this).inflate(R.layout.filter,null);
        bottomSheetDialog = new BottomSheetDialog(this);
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
    }

    // 自定义比较器：按净得运费价格排序
    static class PureFreightComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Resource p1 = (Resource) object1; // 强制转换
            Resource p2 = (Resource) object2;
            return (p1.freight.subtract(p1.deposit)).compareTo(p2.freight.subtract(p2.deposit));
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