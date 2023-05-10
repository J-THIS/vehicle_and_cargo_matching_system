package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.adapter.ClientAdapter;
import com.example.vehicle_and_cargo_matching_system.adapter.ResourceAdapter;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ClientDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;
import com.example.vehicle_and_cargo_matching_system.view.ClientListView;
import com.example.vehicle_and_cargo_matching_system.view.ResourceListView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttentionActivity extends AppCompatActivity {

    private TextView tv_title,tv_back;
    private Button btn_resource,btn_client,btn_refresh;
    private ScrollView sv_resource,sv_client;
    private ResourceListView rlv_attention;
    private ResourceAdapter resourceAdapter;
    private ResourceAttentionDao resourceAttentionDao;
    private List<Resource> resource_list;
    private ClientListView clv_attention;
    private ClientAdapter clientAdapter;
    private ClientAttentionDao clientAttentionDao;
    private ClientDao clientDao;
    private List<Client> client_list;
    private String id,mPosition,mRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        initData();
        initView();
        initEvent();
        initAdapter();
    }

    private void initAdapter(){
        resourceAdapter = new ResourceAdapter(this,mPosition,mRegion,id);
        rlv_attention.setAdapter(resourceAdapter);//绑定适配器
        clientAdapter = new ClientAdapter(this,mPosition,mRegion,id);
        clv_attention.setAdapter(clientAdapter);//绑定适配器
        setData();
    }

    private void setData(){
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==14){
                    Object[] objects = (Object[]) msg.obj;
                    resource_list = (List<Resource>)objects[0];
                    resourceAdapter.setData(resource_list);//将货源集数据传递到adapter中
                    client_list = (List<Client>) objects[1];
                    clientAdapter.setData(client_list);//将货源集数据传递到adapter中
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                resourceAttentionDao = new ResourceAttentionDaoImpl();
                List<String> resource_attention_list;
                List<Resource> resourceList = new ArrayList<>();
                clientAttentionDao = new ClientAttentionDaoImpl();
                List<String> client_attention_list;
                List<Client> clientList = new ArrayList<>();
                clientDao = new ClientDaoImpl();
                try {
                    resource_attention_list = resourceAttentionDao.getResourceAttention(id);
                    if(resource_attention_list!=null){
                        for (String resourceKey:resource_attention_list){
                            Resource resource = resourceAttentionDao.getResource(resourceKey);
                            if (resource != null){
                                if (resource.getResourceState() == 1){//只显示未被抢单的货源
                                    resourceList.add(resource);
                                }
                            }
                        }
                    }
                    client_attention_list = clientAttentionDao.getClientAttention(id);
                    if (client_attention_list!=null){
                        for (String clientKey:client_attention_list){
                            Client client = clientDao.getClient(clientKey);
                            clientList.add(client);
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                Object[] objects = new Object[2];
                objects[0] = resourceList;
                objects[1] = clientList;
                msg.what = 14;
                msg.obj = objects;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        mPosition = getIntent().getStringExtra("mPosition");
        mRegion = getIntent().getStringExtra("mRegion");
        resource_list = new ArrayList<>();
        client_list = new ArrayList<>();
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        btn_resource.setOnClickListener(onClick);
        btn_client.setOnClickListener(onClick);
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
                    btn_client.setSelected(false);
                    btn_client.setTextColor(getResources().getColor(R.color.dark_orange));
                    sv_client.setVisibility(View.GONE);
                    sv_resource.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_client:
                    btn_client.setSelected(true);
                    btn_client.setTextColor(getResources().getColor(R.color.white));
                    btn_resource.setSelected(false);
                    btn_resource.setTextColor(getResources().getColor(R.color.dark_orange));
                    sv_resource.setVisibility(View.GONE);
                    sv_client.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_refresh:
                    initAdapter();
                    break;
            }
        }
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("关注列表");
        tv_back = findViewById(R.id.tv_back);
        btn_resource = findViewById(R.id.btn_resource);
        btn_resource.setSelected(true);
        btn_resource.setTextColor(getResources().getColor(R.color.white));
        btn_client = findViewById(R.id.btn_client);
        btn_refresh = findViewById(R.id.btn_refresh);
        sv_resource = findViewById(R.id.sv_resource);
        sv_resource.setVisibility(View.VISIBLE);
        sv_client = findViewById(R.id.sv_client);
        rlv_attention = findViewById(R.id.rlv_attention);
        clv_attention = findViewById(R.id.clv_attention);
    }
}