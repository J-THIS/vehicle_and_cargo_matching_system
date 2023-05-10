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
import com.example.vehicle_and_cargo_matching_system.adapter.EvaluationAdapter;
import com.example.vehicle_and_cargo_matching_system.adapter.ResourceAdapter;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Order;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ClientDao;
import com.example.vehicle_and_cargo_matching_system.dao.OrderDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;
import com.example.vehicle_and_cargo_matching_system.view.ClientListView;
import com.example.vehicle_and_cargo_matching_system.view.EvaluationListView;
import com.example.vehicle_and_cargo_matching_system.view.ResourceListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDetailActivity extends AppCompatActivity {

    private TextView tv_title,tv_back;
    private Button btn_resource,btn_evaluation,btn_refresh;
    private ScrollView sv_resource,sv_evaluation;
    private ResourceListView rlv_client;
    private ResourceAdapter resourceAdapter;
    private ResourceDao resourceDao;
    private List<Resource> resource_list;
    private ClientAttentionDao clientAttentionDao;
    private ClientDao clientDao;
    private Client client;
    private OrderDao orderDao;
    private EvaluationAdapter evaluationAdapter;
    private EvaluationListView elv_client;
    private List<Order> order_list;
    private String id,mPosition,mRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);
        initData();
        initView();
        initEvent();
        initAdapter();
    }

    private void initAdapter(){
        resourceAdapter = new ResourceAdapter(this,mPosition,mRegion,id);
        rlv_client.setAdapter(resourceAdapter);//绑定适配器
        evaluationAdapter = new EvaluationAdapter(this,mPosition,mRegion,id);
        elv_client.setAdapter(evaluationAdapter);//绑定适配器
        setData();
    }

    private void setData(){
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==22){
                    Object[] objects = (Object[]) msg.obj;
                    resource_list = (List<Resource>)objects[0];
                    resourceAdapter.setData(resource_list);//将货源集数据传递到adapter中
                    order_list = (List<Order>) objects[1];
                    evaluationAdapter.setData(order_list);//将货源集数据传递到adapter中
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                resourceDao = new ResourceDaoImpl();
                List<Resource> resourceList = new ArrayList<>();
                orderDao = new OrderDaoImpl();
                List<Order> orderList = new ArrayList<>();
                try {
                    resourceList = resourceDao.getResourceByClient(client.getClientKey());
                    orderList = orderDao.getOrderListByClient(client.getClientKey(),4);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                Object[] objects = new Object[2];
                objects[0] = resourceList;
                objects[1] = orderList;
                msg.what = 22;
                msg.obj = objects;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private void initData() {
        client = (Client) getIntent().getSerializableExtra("client");
        id = getIntent().getStringExtra("id");
        mPosition = getIntent().getStringExtra("mPosition");
        mRegion = getIntent().getStringExtra("mRegion");
        resource_list = new ArrayList<>();
        order_list = new ArrayList<>();
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        btn_resource.setOnClickListener(onClick);
        btn_evaluation.setOnClickListener(onClick);
        btn_refresh.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.btn_resource:
                    btn_resource.setSelected(true);
                    btn_resource.setTextColor(getResources().getColor(R.color.white));
                    btn_evaluation.setSelected(false);
                    btn_evaluation.setTextColor(getResources().getColor(R.color.dark_orange));
                    sv_evaluation.setVisibility(View.GONE);
                    sv_resource.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_evaluation:
                    btn_evaluation.setSelected(true);
                    btn_evaluation.setTextColor(getResources().getColor(R.color.white));
                    btn_resource.setSelected(false);
                    btn_resource.setTextColor(getResources().getColor(R.color.dark_orange));
                    sv_resource.setVisibility(View.GONE);
                    sv_evaluation.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_refresh:
                    initAdapter();
                    break;
            }
        }
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("货主详情");
        tv_back = findViewById(R.id.tv_back);
        btn_resource = findViewById(R.id.btn_resource);
        btn_resource.setSelected(true);
        btn_resource.setTextColor(getResources().getColor(R.color.white));
        btn_evaluation = findViewById(R.id.btn_evaluation);
        btn_refresh = findViewById(R.id.btn_refresh);
        sv_resource = findViewById(R.id.sv_resource);
        sv_resource.setVisibility(View.VISIBLE);
        sv_evaluation = findViewById(R.id.sv_evaluation);
        rlv_client = findViewById(R.id.rlv_client);
        elv_client = findViewById(R.id.elv_client);
    }
}