package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.ClientAttention;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class ResourceDetailActivity extends AppCompatActivity {

    private TextView tv_distance,tv_load_time,tv_transfer_num,tv_load_place1,tv_load_place2,
            tv_load_place3,tv_unload_place1,tv_unload_place2,tv_unload_place3,tv_use_type,
            tv_car_length,tv_car_type,tv_cargo,tv_pure_freight,tv_freight_explanation,
            tv_total_freight,tv_deposit,tv_if_return,tv_deposit_detail,tv_client,tv_credit_star,
            tv_close_num,tv_finish_num,tv_applause_percentage,tv_phone,tv_back,tv_title,tv_detail;
    private Button btn_client_attention,btn_resource_attention,btn_order_taking;
    private LinearLayout ll_transfer_info,ll_freight_explanation,ll_attention_list;
    private String id,mPosition,mRegion,str_distance,str_load_time,str_transfer_num,str_load_place1,
            str_load_place2,str_load_place3,str_unload_place1,str_unload_place2, str_unload_place3,
            str_use_type,str_car_length,str_car_type,str_cargo,str_client,str_credit_star,
            str_finish_num,str_applause_percentage;
    private Integer client_attentioned,resource_attentioned;
    private Bundle bundle;
    private Resource resource;
    private Client client;
    private ClientAttentionDao clientAttentionDao;
    private ResourceAttentionDao resourceAttentionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_detail);
        ActivityStackUtil.getAppManager().addActivity(this);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        btn_client_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == 6){//点完关注按钮
                            if ((boolean)msg.obj){//数据库修改成功后改变ui
                                client_attention_operation_result(v,"取消关注","关注成功！");
                            }else{//数据库修改失败
                                show_attention_result_dialog(v,"关注失败！");
                            }
                        }else if (msg.what == 7){//点完取消关注按钮
                            if ((boolean)msg.obj){//数据库修改成功后改变ui
                                client_attention_operation_result(v,"关注","取消成功！");
                            }else{//数据库修改失败
                                show_attention_result_dialog(v,"取消失败！");
                            }
                        }
                        return true;
                    }
                });
                if (client_attentioned == 0){
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientAttentionDao = new ClientAttentionDaoImpl();
                            try {
                                clientAttentionDao.setClientAttention(resource.getClientKey(),id);
                                Message msg = new Message();
                                msg.what = 6;
                                msg.obj = true;
                                handler.sendMessage(msg);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Message msg = new Message();
                                msg.what = 6;
                                msg.obj = false;
                                handler.sendMessage(msg);
                            }
                        }
                    });
                    thread.start();
                }else{
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientAttentionDao = new ClientAttentionDaoImpl();
                            try {
                                clientAttentionDao.cancelClientAttention(resource.getClientKey(),id);
                                Message msg = new Message();
                                msg.what = 7;
                                msg.obj = true;
                                handler.sendMessage(msg);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Message msg = new Message();
                                msg.what = 7;
                                msg.obj = false;
                                handler.sendMessage(msg);
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
        btn_resource_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == 6){//点完加入关注按钮
                            if ((boolean)msg.obj){//数据库修改成功后改变ui
                                resource_attention_operation_result(v,"取消关注","关注成功！");
                            }else{//数据库修改失败
                                show_attention_result_dialog(v,"关注失败！");
                            }
                        }else if (msg.what == 7){//点完取消关注按钮
                            if ((boolean)msg.obj){//数据库修改成功后改变ui
                                resource_attention_operation_result(v,"加入关注","取消成功！");
                            }else{//数据库修改失败
                                show_attention_result_dialog(v,"取消失败！");
                            }
                        }
                        return true;
                    }
                });
                if (resource_attentioned == 0){
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            resourceAttentionDao = new ResourceAttentionDaoImpl();
                            try {
                                resourceAttentionDao.setResourceAttention(resource.getResourceKey(),id);
                                Message msg = new Message();
                                msg.what = 6;
                                msg.obj = true;
                                handler.sendMessage(msg);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Message msg = new Message();
                                msg.what = 6;
                                msg.obj = false;
                                handler.sendMessage(msg);
                            }
                        }
                    });
                    thread.start();
                }else{
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            resourceAttentionDao = new ResourceAttentionDaoImpl();
                            try {
                                resourceAttentionDao.cancelResourceAttention(resource.getResourceKey(),id);
                                Message msg = new Message();
                                msg.what = 7;
                                msg.obj = true;
                                handler.sendMessage(msg);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Message msg = new Message();
                                msg.what = 7;
                                msg.obj = false;
                                handler.sendMessage(msg);
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
        btn_order_taking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourceDetailActivity.this,PayActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("deposit",tv_deposit.getText());
                intent.putExtra("resource_key",resource.getResourceKey());
                startActivity(intent);
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourceDetailActivity.this, ClientDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("client",client);
                intent.putExtras(bundle);
                intent.putExtra("id",id);
                intent.putExtra("mPosition",mPosition);
                intent.putExtra("mRegion",mRegion);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        tv_title.setText("货源详情");
        Intent intent = getIntent();
        bundle = intent.getExtras();
        resource = (Resource) bundle.getSerializable("resource");
        client = (Client) bundle.getSerializable("client");
        id = intent.getStringExtra("id");
        mPosition = intent.getStringExtra("mPosition");
        mRegion = intent.getStringExtra("mRegion");
        client_attentioned = intent.getIntExtra("client_attentioned",0);
        resource_attentioned = intent.getIntExtra("resource_attentioned",0);
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
        if (client_attentioned == 0){
            btn_client_attention.setText("关注");
            btn_client_attention.setBackgroundResource(R.drawable.button_shape_selector);
            btn_client_attention.setTextSize(15);
        }else{
            btn_client_attention.setText("取消关注");
            btn_client_attention.setBackgroundResource(R.drawable.button_shape_selector1);
            btn_client_attention.setTextSize(15);
        }
        if (resource_attentioned == 0){
            btn_resource_attention.setText("加入关注");
            btn_resource_attention.setBackgroundResource(R.drawable.button_shape_selector2);
            btn_resource_attention.setTextColor(getResources().getColor(R.color.blue_color));
            btn_resource_attention.setTextSize(15);
        }else{
            btn_resource_attention.setText("取消关注");
            btn_resource_attention.setBackgroundResource(R.drawable.button_shape_selector3);
            btn_resource_attention.setTextColor(getResources().getColor(R.color.dark_gray));
            btn_resource_attention.setTextSize(15);
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
        tv_detail = findViewById(R.id.tv_detail);
        btn_client_attention = findViewById(R.id.btn_client_attention);
        btn_resource_attention = findViewById(R.id.btn_resource_attention);
        btn_order_taking = findViewById(R.id.btn_order_taking);
        ll_transfer_info = findViewById(R.id.ll_transfer_info);
        ll_attention_list = findViewById(R.id.ll_attention_list);
        ll_freight_explanation = findViewById(R.id.ll_freight_explanation);
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

    private void client_attention_operation_result(View v,String str_btn,String str_dialog){
        btn_client_attention.setText(str_btn);
        if (client_attentioned==0){
            btn_client_attention.setBackgroundResource(R.drawable.button_shape_selector1);
            client_attentioned = 1;
        }else{
            btn_client_attention.setBackgroundResource(R.drawable.button_shape_selector);
            client_attentioned = 0;
        }
        show_attention_result_dialog(v,str_dialog);
    }

    private void resource_attention_operation_result(View v,String str_btn,String str_dialog){
        btn_resource_attention.setText(str_btn);
        if (resource_attentioned==0){
            btn_resource_attention.setBackgroundResource(R.drawable.button_shape_selector3);
            btn_resource_attention.setTextColor(getResources().getColor(R.color.dark_gray));
            resource_attentioned = 1;
        }else{
            btn_resource_attention.setBackgroundResource(R.drawable.button_shape_selector2);
            btn_resource_attention.setTextColor(getResources().getColor(R.color.blue_color));
            resource_attentioned = 0;
        }
        show_attention_result_dialog(v,str_dialog);
    }

    private void show_attention_result_dialog(View v,String str_dialog){
        Dialog dialog = new Dialog(v.getContext(),R.style.Result_Dialog_Style);
        dialog.setContentView(R.layout.dialog_attention_result);
        TextView tv_attention_result = dialog.findViewById(R.id.tv_attention_result);
        tv_attention_result.setText(str_dialog);
        dialog.show();
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();
                t.cancel();
            }
        }, 3000);
    }
}