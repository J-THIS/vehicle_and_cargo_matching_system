package com.example.vehicle_and_cargo_matching_system.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.activity.ClientDetailActivity;
import com.example.vehicle_and_cargo_matching_system.activity.ResourceDetailActivity;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ClientAdapter extends BaseAdapter {
    private Context mContext;
    private String id;
    private List<Client> client_list;
    public String mPosition,mRegion;
    private String str_client,str_credit_star,str_finish_num,str_applause_percentage;
    private ClientAttentionDao clientAttentionDao;

    public ClientAdapter(Context mContext, String mPosition, String mRegion, String id) {
        this.mContext = mContext;
        this.mPosition = mPosition;
        this.mRegion = mRegion;
        this.id = id;
    }
    
    //设置数据更新
    public void setData(List<Client> client_list){
        this.client_list=client_list;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return client_list == null?0: client_list.size();
    }

    @Override
    public Client getItem(int position) {//找到对象
        return client_list==null? null: client_list.get(position);
    }

    @Override
    public long getItemId(int position) {//找到item的id
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//得到对应的position的视图
        final ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            View view = LayoutInflater.from(mContext).inflate(R.layout.client_item,null);
            vh.iv_user = view.findViewById(R.id.iv_user);
            vh.tv_client = view.findViewById(R.id.tv_client);
            vh.tv_credit_star = view.findViewById(R.id.tv_credit_star);
            vh.tv_close_num = view.findViewById(R.id.tv_close_num);
            vh.tv_finish_num = view.findViewById(R.id.tv_finish_num);
            vh.tv_detail = view.findViewById(R.id.tv_detail);
            vh.tv_applause_percentage = view.findViewById(R.id.tv_applause_percentage);
            vh.btn_cancel_attention = view.findViewById(R.id.btn_cancel_attention);
            final Client client = getItem(position);
            if (client!=null){
                //设置货源列表项中的货主信息
                str_client = get_client(client.getSurname());
                vh.tv_client.setText(str_client);

                str_credit_star = get_credit_star(client.getCredit());
                vh.tv_credit_star.setText(str_credit_star);

                String str_close_num = "下单数" + client.getCloseNum();
                vh.tv_close_num.setText(str_close_num);

                str_finish_num = get_finish_num(client.getFinishNum());
                vh.tv_finish_num.setText(str_finish_num);

                str_applause_percentage = get_applause_percentage(client);
                vh.tv_applause_percentage.setText(str_applause_percentage);

                vh.btn_cancel_attention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(@NonNull Message msg) {
                                if (msg.what == 13){//点完取消关注按钮
                                    if ((boolean)msg.obj){//数据库修改成功后改变ui
                                        show_attention_result_dialog(v,"取消成功！");
                                    }else{//数据库修改失败
                                        show_attention_result_dialog(v,"取消失败！");
                                    }
                                }
                                return true;
                            }
                        });
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                clientAttentionDao = new ClientAttentionDaoImpl();
                                Message msg = new Message();
                                msg.what = 13;
                                try {
                                    clientAttentionDao.cancelClientAttention(client.getClientKey(),id);
                                    msg.obj = true;
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    msg.obj = false;
                                }
                                handler.sendMessage(msg);
                            }
                        });
                        thread.start();
                    }
                });

                vh.tv_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ClientDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("client",client);
                        intent.putExtras(bundle);
                        intent.putExtra("id",id);
                        intent.putExtra("mPosition",mPosition);
                        intent.putExtra("mRegion",mRegion);
                        mContext.startActivity(intent);
                    }
                });
            }
            return view;
        }else{
            return convertView;
        }
    }

    class ViewHolder{
        public TextView tv_client,tv_credit_star,tv_close_num,tv_finish_num,tv_applause_percentage,
                tv_detail;
        public ImageView iv_user;
        public Button btn_cancel_attention;
    }

    //得到货主称呼字符串
    private String get_client(String client_surname){
        String str_client = client_surname + "老板";
        return str_client;
    }

    //得到信用星级（默认三星）
    private String get_credit_star(int credit){
        String str_credit_star = "⭐⭐⭐";
        switch (credit){
            case 1: str_credit_star = "⭐";break;
            case 2: str_credit_star = "⭐⭐";break;
            case 3: str_credit_star = "⭐⭐⭐";break;
            case 4: str_credit_star = "⭐⭐⭐⭐";break;
            case 5: str_credit_star = "⭐⭐⭐⭐⭐";break;
        }
        return str_credit_star;
    }


    //得到交易量字符串
    private String get_finish_num(Integer finish_num){
        String str_client = "完单量0";
        if (finish_num != null){
            str_client = "完单量" + finish_num;
        }
        return str_client;
    }

    //得到好评率字符串
    private String get_applause_percentage(Client client){
        String str_applause_percentage = "";
        if (client.getApplauseNum() != null){
            Double applause_percentage = client.getApplauseNum().doubleValue()/
                    client.getFinishNum().doubleValue() * 100;
            if (applause_percentage>100){
                applause_percentage = 100.0;
            }
            str_applause_percentage = "好评率" + applause_percentage + "%";
        }
        return str_applause_percentage;
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
