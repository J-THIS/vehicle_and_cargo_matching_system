package com.example.vehicle_and_cargo_matching_system.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.activity.ClientDetailActivity;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Driver;
import com.example.vehicle_and_cargo_matching_system.bean.Order;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.DriverDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceDaoImpl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EvaluationAdapter extends BaseAdapter {
    private Context mContext;
    private String id;
    private List<Order> order_list;
    public String mPosition,mRegion;
    private String str_driver,str_evaluation,str_time;
    private DriverDao driverDao;
    private Driver driver;

    public EvaluationAdapter(Context mContext, String mPosition, String mRegion, String id) {
        this.mContext = mContext;
        this.mPosition = mPosition;
        this.mRegion = mRegion;
        this.id = id;
    }

    //设置数据更新
    public void setData(List<Order> order_list){
        this.order_list=order_list;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return order_list == null?0: order_list.size();
    }

    @Override
    public Order getItem(int position) {//找到对象
        return order_list==null? null: order_list.get(position);
    }

    @Override
    public long getItemId(int position) {//找到item的id
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//得到对应的position的视图
        final ViewHolder vh;
        if(convertView==null){
            vh = new ViewHolder();
            View view = LayoutInflater.from(mContext).inflate(R.layout.evaluation_item,null);
            vh.iv_user = view.findViewById(R.id.iv_user);
            vh.tv_driver = view.findViewById(R.id.tv_driver);
            vh.tv_evaluation = view.findViewById(R.id.tv_evaluation);
            vh.tv_time = view.findViewById(R.id.tv_time);
            vh.tv_content = view.findViewById(R.id.tv_content);
            final Order order = getItem(position);
            if (order!=null){
                @SuppressLint("HandlerLeak")
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        if (msg.what==23){
                            driver = (Driver)msg.obj;
                            if (driver != null){
                                str_driver = get_driver(driver.getSurName());
                                vh.tv_driver.setText(str_driver);
                            }

                        }
                    }
                };
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        driverDao = new DriverDaoImpl();
                        Driver driver = new Driver();
                        try {
                             driver = driverDao.getDriver(order.getDriverKey());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Message msg = new Message();
                        msg.what = 23;
                        msg.obj = driver;
                        handler.sendMessage(msg);
                    }
                });
                thread.start();

                str_evaluation = get_evaluation(order.getEvaluation());
                vh.tv_evaluation.setText(str_evaluation);

                str_time = get_release_time(order.getRealUnloadTime());
                vh.tv_time.setText(str_time);

                vh.tv_content.setText(order.getContent());
            }
            return view;
        }else{
            return convertView;
        }
    }

    class ViewHolder{
        public TextView tv_driver,tv_evaluation,tv_time,tv_content;
        public ImageView iv_user;
    }

    //得到货主称呼字符串
    private String get_driver(String driver_surname){
        String str_driver = driver_surname + "师傅";
        return str_driver;
    }

    //得到评价
    private String get_evaluation(int evaluation){
        String str_evaluation;
        if(evaluation == 1){
            str_evaluation = "好评";
        }else{
            str_evaluation = "差评";
        }
        return str_evaluation;
    }


    /* 得到发布时间字符串，若一小时内发布的展示为“X分钟前”,24小时内发布的展示为“X小时前”，
    其余展示实际时间 */
    private String get_release_time( Date date ){
        String release_time = null;
        if(date != null){
            long differenceValue = new Date().getTime() - date.getTime();
            if(differenceValue < 3600000){
                release_time = (differenceValue / 1000 / 60 ) + "分钟前发布";
            }else if(differenceValue > 3600000){
                if(differenceValue < 86400000){
                    release_time = (differenceValue / 1000 / 60 / 60 ) + "小时前发布";
                }else{
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    release_time = format.format(date) + "发布";
                }
            }
            return release_time;
        }else{
            return "";
        }
    }

}
