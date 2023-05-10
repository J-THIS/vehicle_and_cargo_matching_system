package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.adapter.ClientAdapter;
import com.example.vehicle_and_cargo_matching_system.adapter.OrderAdapter;
import com.example.vehicle_and_cargo_matching_system.adapter.ResourceAdapter;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Order;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ClientDao;
import com.example.vehicle_and_cargo_matching_system.dao.OrderDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;
import com.example.vehicle_and_cargo_matching_system.view.ClientListView;
import com.example.vehicle_and_cargo_matching_system.view.OrderListView;
import com.example.vehicle_and_cargo_matching_system.view.ResourceListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private TextView tv_title,tv_back,tv_all,tv_unfilled,tv_transit,tv_evaluation,tv_cancel;
    private OrderListView olv;
    private OrderAdapter orderAdapter;
    private OrderDao orderDao;
    private List<Order> order_list;
    private String id,mPosition,mRegion;
    private int tab;//页面编号：0全部，1未装货，2运输中，3待评价，4已取消

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ActivityStackUtil.getAppManager().addActivity(this);
        initData();
        initView();
        initEvent();
        initAdapter();
    }

    private void initAdapter(){
        orderAdapter = new OrderAdapter(this,mPosition,mRegion,id);
        olv.setAdapter(orderAdapter);
        setData();
    }

    public void setData(){
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 20){
                    order_list = (List<Order>)msg.obj;
                    orderAdapter.setData(order_list);//将货源集数据传递到adapter中
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                orderDao = new OrderDaoImpl();
                List<Order> orderList = new ArrayList<>();
                try {
                    if (tab == 0){
                        orderList = orderDao.getOrderList(id,null);
                    }else{
                        orderList = orderDao.getOrderList(id,tab-1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 20;
                msg.obj = orderList;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        mPosition = getIntent().getStringExtra("mPosition");
        mRegion = getIntent().getStringExtra("mRegion");
        tab = getIntent().getIntExtra("tab",0);
        order_list = new ArrayList<>();
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        tv_all.setOnClickListener(onClick);
        tv_unfilled.setOnClickListener(onClick);
        tv_transit.setOnClickListener(onClick);
        tv_evaluation.setOnClickListener(onClick);
        tv_cancel.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.tv_all:
                    clear();
                    tv_all.setTextColor(getResources().getColor(R.color.blue_color));
                    tab = 0;
                    setData();
                    break;
                case R.id.tv_unfilled:
                    clear();
                    tv_unfilled.setTextColor(getResources().getColor(R.color.blue_color));
                    tab = 1;
                    setData();
                    break;
                case R.id.tv_transit:
                    clear();
                    tv_transit.setTextColor(getResources().getColor(R.color.blue_color));
                    tab = 2;
                    setData();
                    break;
                case R.id.tv_evaluation:
                    clear();
                    tv_evaluation.setTextColor(getResources().getColor(R.color.blue_color));
                    tab = 3;
                    setData();
                    break;
                case R.id.tv_cancel:
                    clear();
                    tv_cancel.setTextColor(getResources().getColor(R.color.blue_color));
                    tab = 4;
                    setData();
                    break;
                case R.id.btn_refresh:
                    initAdapter();
                    break;
            }
        }
    }

    private void clear(){
        tv_all.setTextColor(getResources().getColor(R.color.dark_orange));
        tv_unfilled.setTextColor(getResources().getColor(R.color.dark_orange));
        tv_transit.setTextColor(getResources().getColor(R.color.dark_orange));
        tv_evaluation.setTextColor(getResources().getColor(R.color.dark_orange));
        tv_cancel.setTextColor(getResources().getColor(R.color.dark_orange));
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("订单列表");
        tv_back = findViewById(R.id.tv_back);
        tv_all = findViewById(R.id.tv_all);
        tv_unfilled = findViewById(R.id.tv_unfilled);
        tv_transit = findViewById(R.id.tv_transit);
        tv_evaluation = findViewById(R.id.tv_evaluation);
        tv_cancel = findViewById(R.id.tv_cancel);
        olv = findViewById(R.id.olv);
        switch (tab){
            case 0:
                clear();
                tv_all.setTextColor(getResources().getColor(R.color.blue_color));
                break;
            case 1:
                clear();
                tv_unfilled.setTextColor(getResources().getColor(R.color.blue_color));
                break;
            case 2:
                clear();
                tv_transit.setTextColor(getResources().getColor(R.color.blue_color));
                break;
            case 3:
                clear();
                tv_evaluation.setTextColor(getResources().getColor(R.color.blue_color));
                break;
            case 4:
                clear();
                tv_cancel.setTextColor(getResources().getColor(R.color.blue_color));
                break;
        }
    }
}