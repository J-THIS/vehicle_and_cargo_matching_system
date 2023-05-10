package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.dao.OrderDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class PayActivity extends AppCompatActivity {

    private TextView tv_title,tv_back,tv_deposit;
    private RelativeLayout rl_wechat,rl_alipay;
    private String id,resource_key,deposit;
    private ResourceDao resourceDao;
    private OrderDao orderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ActivityStackUtil.getAppManager().addActivity(this);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        resource_key = getIntent().getStringExtra("resource_key");
        deposit = getIntent().getStringExtra("deposit");
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        rl_wechat.setOnClickListener(onClick);
        rl_alipay.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {
        @SuppressLint("Range")
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.tv_back:
                    //返回按钮
                    Toast.makeText(ActivityStackUtil.getAppManager().
                                    getActivity(ResourceDetailActivity.class), "支付失败！",
                            Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case R.id.rl_alipay:
                case R.id.rl_wechat:
                    //由于微信支付需要接入api，所以支付功能暂时不能使用
                    @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 10) {
                                Toast.makeText(ActivityStackUtil.getAppManager().
                                    getActivity(ResourceDetailActivity.class), "支付成功！",
                                        Toast.LENGTH_LONG).show();
                                //跳过中间的货源详情页面，直接回到主页面
                                ActivityStackUtil.getAppManager().finishActivity(ResourceDetailActivity.class);
                                finish();
                            }
                        }
                    };
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            resourceDao = new ResourceDaoImpl();
                            orderDao = new OrderDaoImpl();
                            try {
                                resourceDao.setResourceState(resource_key,0);
                                orderDao.createOrder(resource_key,id);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.what = 10;
                            handler.sendMessage(msg);
                        }
                    });
                    thread.start();
                    finish();
                    break;
            }
        }
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("支付");
        tv_deposit = findViewById(R.id.tv_deposit);
        tv_deposit.setText(deposit);
        tv_back = findViewById(R.id.tv_back);
        rl_wechat = findViewById(R.id.rl_wechat);
        rl_alipay = findViewById(R.id.rl_alipay);
    }
}