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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Order;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tv_distance,tv_load_time,tv_transfer_num,tv_load_place1,tv_load_place2,
            tv_load_place3,tv_unload_place1,tv_unload_place2,tv_unload_place3,tv_use_type,
            tv_car_length,tv_car_type,tv_cargo,tv_pure_freight,tv_freight_explanation,
            tv_total_freight,tv_deposit,tv_if_return,tv_deposit_detail,tv_client,tv_credit_star,
            tv_close_num,tv_finish_num,tv_applause_percentage,tv_phone,tv_back,tv_title,
            tv_create_time,tv_deposit_state,tv_pay_state,tv_real_load_time,tv_real_unload_time;
    private LinearLayout ll_transfer_info,ll_freight_explanation;
    private RelativeLayout rl_real_load_time,rl_real_unload_time;
    private String id,str_distance,str_load_time,str_transfer_num,str_load_place1, str_load_place2,
            str_load_place3,str_unload_place1,str_unload_place2, str_unload_place3,str_use_type,
            str_car_length,str_car_type,str_cargo,str_client,str_credit_star,str_finish_num,
            str_applause_percentage;
    private Bundle bundle;
    private Resource resource;
    private Client client;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ActivityStackUtil.getAppManager().addActivity(this);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        tv_title.setText("订单详情");
        Intent intent = getIntent();
        bundle = intent.getExtras();
        resource = (Resource) bundle.getSerializable("resource");
        client = (Client) bundle.getSerializable("client");
        order = (Order) bundle.getSerializable("order");
        id = intent.getStringExtra("id");
        str_distance = intent.getStringExtra("distance");
        str_load_time = intent.getStringExtra("load_time");
        str_transfer_num = intent.getStringExtra("transfer_num");
        str_use_type = intent.getStringExtra("use_type");
        str_car_length = intent.getStringExtra("car_length");
        str_car_type = intent.getStringExtra("car_type");
        str_cargo = intent.getStringExtra("cargo");
        str_client = intent.getStringExtra("client_name");
        str_credit_star = intent.getStringExtra("credit_star");
        str_finish_num = intent.getStringExtra("finish_num");
        str_applause_percentage = intent.getStringExtra("applause_percentage");

        tv_distance.setText(str_distance);
        tv_load_time.setText(str_load_time);
        tv_transfer_num.setText(str_transfer_num);
        str_load_place1 = "装货地一：" + resource.getLoadPlace1();
        tv_load_place1.setText(str_load_place1);
        if (resource.getLoadNum()>1){
            str_load_place2 = "装货地二：" + resource.getLoadPlace2();
            create_transfer_textView(tv_load_place2,str_load_place2);
        }else if (resource.getLoadNum()>2){
            str_load_place3 = "装货地三：" + resource.getLoadPlace3();
            create_transfer_textView(tv_load_place3,str_load_place3);
        }
        str_unload_place1 = "卸货地一：" + resource.getUnloadPlace1();
        tv_unload_place1.setText(str_unload_place1);
        if (resource.getUnloadNum()>1){
            str_unload_place2 = "卸货地二：" + resource.getUnloadPlace2();
            create_transfer_textView(tv_unload_place2,str_unload_place2);
        }else if (resource.getUnloadNum()>2){
            str_unload_place3 = "卸货地三：" + resource.getUnloadPlace3();
            create_transfer_textView(tv_unload_place3,str_unload_place3);
        }
        tv_use_type.setText(str_use_type);
        tv_car_length.setText(str_car_length);
        tv_car_type.setText(str_car_type);
        tv_cargo.setText(str_cargo);
        if (resource.getIfReturn()==0){//若订金退还
            BigDecimal pure_freight = resource.getFreight().subtract(resource.getDeposit());
            String str_pure_freight = pure_freight.toString() + "元";
            tv_pure_freight.setText(str_pure_freight);
            String str_freight_explanation = "￥"+pure_freight+"(净得运费)=￥"+
                    resource.getFreight()+"(运费)-"+resource.getDeposit()+"(订金)";
            create_freight_textView(tv_freight_explanation,str_freight_explanation);
            tv_if_return.setText("(退还)");
            tv_deposit_detail.setText("订金支付到平台用于订货订金,在司机点击“确认装货”后支付给货主，作为“信息费”");
        }else{//若订金不退还
            String str_pure_freight = resource.getFreight() + "元";
            tv_pure_freight.setText(str_pure_freight);
            tv_if_return.setText("(不退还)");
            tv_deposit_detail.setText("订金支付到平台用于订货订金,在货主点击“确认收货”后退还给司机，作为“履约保证金”");
        }
        String str_total_freight = resource.getFreight() + "元/趟";
        tv_total_freight.setText(str_total_freight);
        String str_deposit = resource.getDeposit() + "元";
        tv_deposit.setText(str_deposit);
        tv_client.setText(str_client);
        tv_credit_star.setText(str_credit_star);
        String str_close_num = "下单数" + client.getCloseNum();
        tv_close_num.setText(str_close_num);
        tv_finish_num.setText(str_finish_num);
        tv_applause_percentage.setText(str_applause_percentage);
        tv_phone.setText(client.getPhone());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_create_time.setText(sdf.format(order.getCreateTime()));
        if (order.getDepositState() == 0){
            tv_deposit_state.setText("未退还");
        }else if (order.getDepositState() == 1){
            tv_deposit_state.setText("已支付给货主");
        }else if (order.getDepositState() == 2){
            tv_deposit_state.setText("已退还");
        }
        //由于暂时没设计货主端，这里把实际支付运费时间等同于默认支付运费时间
        if (order.getOrderState()!=2 && order.getOrderState()!=4){
            tv_pay_state.setText("未支付");
        }else if(order.getDefaultPayTime()!=null){
            long differenceValue = new Date().getTime() - order.getDefaultPayTime().getTime();
            if (differenceValue > 0){
                tv_pay_state.setText("未支付");
            }else{
                tv_pay_state.setText("已支付");
            }
        }else{
            tv_pay_state.setText("未支付");
        }
        rl_real_load_time.setVisibility(View.GONE);
        rl_real_unload_time.setVisibility(View.GONE);
        int order_state = order.getOrderState();
        if (order_state!=0 && order_state!=3){
            rl_real_load_time.setVisibility(View.VISIBLE);
            tv_real_load_time.setText(sdf.format(order.getRealLoadTime()));
        }
        if (order_state==2 || order_state==4){
            rl_real_unload_time.setVisibility(View.VISIBLE);
            tv_real_unload_time.setText(sdf.format(order.getRealUnloadTime()));
        }
    }

    private void initView() {
        tv_distance = findViewById(R.id.tv_distance);
        tv_load_time = findViewById(R.id.tv_load_time);
        tv_transfer_num = findViewById(R.id.tv_transfer_num);
        tv_load_place1 = findViewById(R.id.tv_load_place1);
        tv_unload_place1 = findViewById(R.id.tv_unload_place1);
        tv_use_type = findViewById(R.id.tv_use_type);
        tv_car_length = findViewById(R.id.tv_car_length);
        tv_car_type = findViewById(R.id.tv_car_type);
        tv_cargo = findViewById(R.id.tv_cargo);
        tv_pure_freight = findViewById(R.id.tv_pure_freight);
        tv_total_freight = findViewById(R.id.tv_total_freight);
        tv_deposit = findViewById(R.id.tv_deposit);
        tv_if_return = findViewById(R.id.tv_if_return);
        tv_deposit_detail = findViewById(R.id.tv_deposit_detail);
        tv_client = findViewById(R.id.tv_client);
        tv_credit_star = findViewById(R.id.tv_credit_star);
        tv_close_num = findViewById(R.id.tv_close_num);
        tv_finish_num = findViewById(R.id.tv_finish_num);
        tv_applause_percentage = findViewById(R.id.tv_applause_percentage);
        tv_phone = findViewById(R.id.tv_phone);
        tv_title = findViewById(R.id.tv_title);
        tv_back = findViewById(R.id.tv_back);
        ll_transfer_info = findViewById(R.id.ll_transfer_info);
        ll_freight_explanation = findViewById(R.id.ll_freight_explanation);
        rl_real_load_time = findViewById(R.id.rl_real_load_time);
        rl_real_unload_time = findViewById(R.id.rl_real_unload_time);
        tv_create_time = findViewById(R.id.tv_create_time);
        tv_deposit_state = findViewById(R.id.tv_deposit_state);
        tv_pay_state = findViewById(R.id.tv_pay_state);
        tv_real_load_time = findViewById(R.id.tv_real_load_time);
        tv_real_unload_time = findViewById(R.id.tv_real_unload_time);
    }

    //动态创建TextView
    private void create_transfer_textView(TextView textView,String str){
        textView = new TextView(this);
        textView.setText(str);
        textView.setTextSize(17);
        textView.setTextColor(this.getResources().getColor(R.color.black));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        ll_transfer_info.addView(textView);
    }

    //动态创建TextView
    private void create_freight_textView(TextView textView,String str){
        textView = new TextView(this);
        textView.setText(str);
        textView.setTextSize(12);
        textView.setTextColor(this.getResources().getColor(R.color.dark_gray));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        ll_freight_explanation.addView(textView);
    }
}