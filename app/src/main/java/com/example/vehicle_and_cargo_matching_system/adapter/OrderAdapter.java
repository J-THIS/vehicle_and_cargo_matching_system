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
import com.example.vehicle_and_cargo_matching_system.activity.EvaluationActivity;
import com.example.vehicle_and_cargo_matching_system.activity.MainActivity;
import com.example.vehicle_and_cargo_matching_system.activity.OrderActivity;
import com.example.vehicle_and_cargo_matching_system.activity.OrderDetailActivity;
import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Order;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientDao;
import com.example.vehicle_and_cargo_matching_system.dao.OrderDao;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ResourceAttentionDaoImpl;

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

public class OrderAdapter extends BaseAdapter {
    private OrderActivity mContext;
    private String id;
    private List<Order> order_list;
    public String mPosition,mRegion;
    private final static String AK = "IDPp1BRd0WEmkon5y38jIskMmMRyk9MR";//百度地图api密钥
    private String str_distance,str_load_time,str_transfer_num,str_use_type,str_car_length,
            str_car_type,str_cargo,str_client, str_credit_star,str_finish_num,
            str_applause_percentage;
    private ClientDao clientDao;
    private Client client;
    private ResourceAttentionDao resourceAttentionDao;
    private Resource resource;
    private OrderDao orderDao;
    private Dialog dialog;

    public OrderAdapter(OrderActivity mContext, String mPosition, String mRegion, String id) {
        this.mContext = mContext;
        this.mPosition = mPosition;
        this.mRegion = mRegion;
        this.id = id;
        clientDao = new ClientDaoImpl();
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
    public int getItemViewType(int position) {//得到当前item处于什么订单状态
        return order_list.get(position).getOrderState();
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//得到对应的position的视图
        final ViewHolder vh;
        vh=new ViewHolder();
        View view = null;
        if(convertView==null){
            if (getItemViewType(position) == 0){//未装货
                view = LayoutInflater.from(mContext).inflate(R.layout.order_unfilled_item,null);
                vh.btn_confirm_load = view.findViewById(R.id.btn_confirm_load);
                vh.btn_cancel_order = view.findViewById(R.id.btn_cancel_order);
            }else if(getItemViewType(position) == 1){//运输中
                view = LayoutInflater.from(mContext).inflate(R.layout.order_transit_item,null);
                vh.btn_confirm_unload = view.findViewById(R.id.btn_confirm_unload);
                vh.btn_cancel_order_application = view.findViewById(R.id.btn_cancel_order_application);
            }else if(getItemViewType(position) == 2){//待评价
                view = LayoutInflater.from(mContext).inflate(R.layout.order_evaluation_item,null);
                vh.btn_evaluation = view.findViewById(R.id.btn_evaluation);
            }else if(getItemViewType(position) == 3){//已取消
                view = LayoutInflater.from(mContext).inflate(R.layout.order_cancel_item,null);
                vh.btn_delete = view.findViewById(R.id.btn_delete);
            }else if(getItemViewType(position) == 4){//已完成
                view = LayoutInflater.from(mContext).inflate(R.layout.order_cancel_item,null);
                vh.btn_delete = view.findViewById(R.id.btn_delete);
            }
            vh.tv_start = view.findViewById(R.id.tv_start);
            vh.tv_end = view.findViewById(R.id.tv_end);
            vh.tv_detail = view.findViewById(R.id.tv_detail);
            vh.tv_take_order_time = view.findViewById(R.id.tv_take_order_time);
            vh.tv_distance = view.findViewById(R.id.tv_distance);
            vh.tv_load_time = view.findViewById(R.id.tv_load_time);
            vh.tv_use_type = view.findViewById(R.id.tv_use_type);
            vh.tv_car_length = view.findViewById(R.id.tv_car_length);
            vh.tv_car_type = view.findViewById(R.id.tv_car_type);
            vh.tv_transfer_num = view.findViewById(R.id.tv_transfer_num);
            vh.tv_cargo = view.findViewById(R.id.tv_cargo);
            vh.tv_pure_freight = view.findViewById(R.id.tv_pure_freight);
            vh.iv_user = view.findViewById(R.id.iv_user);
            vh.tv_client = view.findViewById(R.id.tv_client);
            vh.tv_credit_star = view.findViewById(R.id.tv_credit_star);
            vh.tv_finish_num = view.findViewById(R.id.tv_finish_num);
            vh.tv_applause_percentage = view.findViewById(R.id.tv_applause_percentage);


            final Order order = getItem(position);
            @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 15){
                        //获取订单对应货源信息
                        resource = (Resource) msg.obj;
                        if (resource != null){
                            if (resource.getLoadNum() > 1) {
                                //当有多个装货地时，在订单列表中优先显示与用户当前所处地区相同的装货地
                                if (Objects.equals(mRegion, resource.getLoadRegion1())){
                                    vh.tv_start.setText(get_region(resource.getLoadRegion1()));
                                }else if (Objects.equals(mRegion, resource.getLoadRegion2())){
                                    vh.tv_start.setText(get_region(resource.getLoadRegion2()));
                                }else if (resource.getLoadNum() > 2 && Objects.equals(mRegion, resource.getLoadRegion3())){
                                    vh.tv_start.setText(get_region(resource.getLoadRegion3()));
                                }
                            }else {
                                vh.tv_start.setText(get_region(resource.getLoadRegion1()));
                            }

                            vh.tv_end.setText(get_region(resource.getUnloadRegion1()));
                            vh.tv_take_order_time.setText(get_take_order_time(order.getCreateTime()));
                            @SuppressLint("HandlerLeak") final Handler handler1 = new Handler() {
                                @Override
                                public void handleMessage(@NonNull Message msg) {
                                    super.handleMessage(msg);
                                    if (msg.what == 16) {
                                        //设置货源列表项中的距离信息
                                        str_distance = msg.obj.toString();
                                        vh.tv_distance.setText(str_distance);
                                    }else if (msg.what == 17){
                                        client = (Client) msg.obj;
                                        if (client!=null){
                                            //设置货源列表项中的货主信息
                                            str_client = get_client(client.getSurname());
                                            vh.tv_client.setText(str_client);

                                            str_credit_star = get_credit_star(client.getCredit());
                                            vh.tv_credit_star.setText(str_credit_star);

                                            str_finish_num = get_finish_num(client.getFinishNum());
                                            vh.tv_finish_num.setText(str_finish_num);

                                            str_applause_percentage = get_applause_percentage(client);
                                            vh.tv_applause_percentage.setText(str_applause_percentage);
                                        }
                                    }
                                }
                            };
                            Thread thread1 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String distance = get_distance(get_lng_lat(resource.getLoadPlace1()),
                                            get_lng_lat(mPosition));
                                    String str_distance = "";
                                    if (distance != null){
                                        //将返回数据格式改成“约XX公里装货”
                                        str_distance = "约" + distance + "装货";
                                    }
                                    Message msg = new Message();
                                    msg.what = 16;
                                    msg.obj = str_distance;
                                    handler1.sendMessage(msg);
                                }
                            });
                            thread1.start();

                            str_load_time = get_load_time(resource.getLoadTime1());
                            vh.tv_load_time.setText(str_load_time);

                            str_use_type = get_use_type(resource.getUseType());
                            vh.tv_use_type.setText(str_use_type);

                            str_car_length = get_car_length(resource.getCarLength());
                            vh.tv_car_length.setText(str_car_length);

                            str_car_type = get_car_type(resource.getCarType());
                            vh.tv_car_type.setText(str_car_type);

                            str_transfer_num = get_transfer_num(resource.getLoadNum(),resource.getUnloadNum());
                            vh.tv_transfer_num.setText(str_transfer_num);

                            str_cargo = resource.getCargo();
                            vh.tv_cargo.setText(str_cargo);


                            vh.tv_pure_freight.setText(get_pure_freight(resource.getFreight(),
                                    resource.getDeposit(),resource.getIfReturn()));

                            //开启异步线程获取货主信息
                            Thread thread2 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        client = clientDao.getClient(resource.getClientKey());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    Message msg = new Message();
                                    msg.what = 17;
                                    msg.obj = client;
                                    handler1.sendMessage(msg);
                                }
                            });
                            thread2.start();
                        }
                    }else if (msg.what == 18){
                        //已完成操作
                        dialog.dismiss();
                        show_result_dialog("操作成功！");
                        mContext.setData();//刷新订单列表数据
                    }
                }
            };
            //开启异步线程获取订单对应货源信息
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    resourceAttentionDao = new ResourceAttentionDaoImpl();
                    Resource resource = null;
                    try {
                        resource = resourceAttentionDao.getResource(order.getResourceKey());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 15;
                    msg.obj = resource;
                    handler.sendMessage(msg);
                }
            });
            thread.start();

            if (getItemViewType(position) == 0){//未装货
                vh.btn_confirm_load.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        set_order_state(1,"您确定已完成装货吗？",handler,order,resource);
                    }
                });
                vh.btn_cancel_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        set_order_state(3,"您确定取消订单吗？",handler,order,resource);
                    }
                });

            }else if(getItemViewType(position) == 1){//运输中
                vh.btn_confirm_unload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        set_order_state(2,"您确定已完成装货吗？",handler,order,resource);
                    }
                });
                vh.btn_cancel_order_application.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //真正流程是要等待货主同意，但由于暂时没开发货主端所以直接调整订单状态
                        set_order_state(3,"您确定发出取消订单申请吗？\n" +
                                "（需等待货主同意）",handler,order,resource);
                    }
                });

            }else if(getItemViewType(position) == 2){//待评价
                vh.btn_evaluation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EvaluationActivity.class);
                        intent.putExtra("order_key",order.getOrderKey());
                        intent.putExtra("client_key",client.getClientKey());
                        mContext.startActivity(intent);
                    }
                });
            }else if(getItemViewType(position) == 3||getItemViewType(position) == 4){//已取消或已完成
                vh.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                orderDao = new OrderDaoImpl();
                                try {
                                    orderDao.deleteOrder(order.getOrderKey());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                msg.what = 18;
                                handler.sendMessage(msg);
                            }
                        });
                        show_confirm_dialog("您确定要删除该记录吗？",thread);
                    }
                });
            }

            vh.tv_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("OrderAdapter",order.getOrderKey());//每个列表项不同
                    Log.i("OrderAdapter",resource.getResourceKey());//每个列表项相同
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("resource",resource);
                    bundle.putSerializable("client",client);
                    bundle.putSerializable("order",order);
                    intent.putExtras(bundle);
                    intent.putExtra("id",id);
                    intent.putExtra("distance",vh.tv_distance.getText().toString());
                    intent.putExtra("load_time",vh.tv_load_time.getText().toString());
                    intent.putExtra("transfer_num",vh.tv_transfer_num.getText().toString());
                    intent.putExtra("use_type",vh.tv_use_type.getText().toString());
                    intent.putExtra("car_length",vh.tv_car_length.getText().toString());
                    intent.putExtra("car_type",vh.tv_car_type.getText().toString());
                    intent.putExtra("cargo",vh.tv_cargo.getText().toString());
                    intent.putExtra("client_name",vh.tv_client.getText().toString());
                    intent.putExtra("credit_star",vh.tv_credit_star.getText().toString());
                    intent.putExtra("finish_num",vh.tv_finish_num.getText().toString());
                    intent.putExtra("applause_percentage",vh.tv_applause_percentage.getText().toString());
                    mContext.startActivity(intent);
                }
            });

            return view;
        }else{
            return convertView;
        }
    }

    class ViewHolder{
        public TextView tv_start,tv_end,tv_detail,tv_take_order_time,tv_distance,tv_load_time,tv_use_type,
                tv_car_length,tv_car_type,tv_transfer_num,tv_cargo,tv_pure_freight,tv_client,
                tv_credit_star, tv_finish_num,tv_applause_percentage;
        public ImageView iv_user;
        public Button btn_confirm_load,btn_cancel_order,btn_confirm_unload,
                btn_cancel_order_application,btn_evaluation,btn_delete;
    }

    private void set_order_state(int order_state,String str,Handler handler,Order order,Resource resource){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                orderDao = new OrderDaoImpl();
                try {
                    orderDao.setOrderState(order.getOrderKey(),order_state,resource.getIfReturn());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 18;
                handler.sendMessage(msg);
            }
        });
        show_confirm_dialog(str,thread);
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

    /* 得到接单时间字符串，若一小时内发布的展示为“X分钟前”,24小时内发布的展示为“X小时前”，
    其余展示实际时间 */
    private String get_take_order_time( Date date ){
        String take_order_time = null;
        if(date != null){
            long differenceValue = new Date().getTime() - date.getTime();
            if(differenceValue < 3600000){
                take_order_time = (differenceValue / 1000 / 60 ) + "分钟前接单";
            }else if(differenceValue > 3600000){
                if(differenceValue < 86400000){
                    take_order_time = (differenceValue / 1000 / 60 / 60 ) + "小时前接单";
                }else{
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    take_order_time = format.format(date) + "接单";
                }
            }
            return take_order_time;
        }else{
            return "";
        }
    }

    //返回输入地址的经纬度坐标 lng(经度),lat(纬度)
    public static Map<String, String> get_lng_lat(String address) {
        try {
            // 将地址转换成utf-8的16进制
            address = URLEncoder.encode(address, "UTF-8");
            URL resjson = new URL("http://api.map.baidu.com/geocoder?address="
                    + address + "&output=json&key=" + AK);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    resjson.openStream()));
            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            in.close();
            String str = sb.toString();
            Log.i("lng_lat",str);
            if(str!=null&&!str.equals("")){
                Map<String, String> map = null;
                int lngStart = str.indexOf("lng\":");
                int lngEnd = str.indexOf(",\"lat");
                int latEnd = str.indexOf("},\"precise");
                if (lngStart > 0 && lngEnd > 0 && latEnd > 0) {
                    String lng = str.substring(lngStart + 5, lngEnd);
                    String lat = str.substring(lngEnd + 7, latEnd);
                    map = new HashMap<String, String>();
                    map.put("lng", lng);
                    map.put("lat", lat);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //求用户目前位置到装货地一的距离
    public static String get_distance(Map<String, String> A,Map<String, String> B) {
        try {
            URL resjson = new URL
                    ("http://api.map.baidu.com/routematrix/v2/driving?output=json&origins="
                            +A.get("lat")+","+A.get("lng")+"&destinations="+B.get("lat")+","
                            +B.get("lng")+"&ak="+AK);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    resjson.openStream()));
            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            in.close();
            String str = sb.toString();
            System.out.println("return json:" + str);
            JSONArray jsonArray = JSON.parseObject(str).getJSONArray("result");
            String distance = jsonArray.getJSONObject(0).getJSONObject("distance").getString("text");
            return distance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    //得到装货时间字符串
    private String get_load_time( Date load_date ){
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        //得到今天的日期字符串
        Date date = new Date();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String str_today = df1.format(date);
        //得到装货时间的日期字符串
        String str_target = df1.format(load_date);
        //计算时间差
        try {
            today.setTime(df1.parse(str_today));
            today.set(Calendar.HOUR, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            target.setTime(df1.parse(str_target));
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
        int difference = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        //根据时间差选择字符串
        switch (difference) {
            case 0:
                return "今天装货";
            case 1:
                return "明天装货";
            case 2:
                return "后天装货";
            case 3:
                return "大后天装货";
            default:
                DateFormat df2 = new SimpleDateFormat("MM-dd");
                return df2.format(load_date)+"装货";
        }
    }

    //得到用车类型字符串
    private String get_use_type(String use_type){
        String str_use_type = "用车类型不限";
        if (use_type != null){
            str_use_type = use_type;
        }
        return str_use_type;
    }

    //得到车长字符串
    private String get_car_length(BigDecimal car_length){
        String str_car_length = "车长不限";
        if (car_length != null){
            car_length = car_length.setScale(1, RoundingMode.HALF_UP);
            str_car_length = car_length.toString() + "米 ";
        }
        return str_car_length;
    }

    //得到车型字符串
    private String get_car_type(String car_type){
        String str_car_type = "";
        if (car_type != null){
            str_car_type = car_type;
        }else{
            str_car_type = "车型不限";
        }
        return str_car_type;
    }

    //得到装卸次数字符串
    private String get_transfer_num(int load_num , int unload_num){
        String str_transfer_num = "";
        String str_load_num = "";
        String str_unload_num = "";
        switch (load_num){
            case 1: str_load_num = "一装";break;
            case 2: str_load_num = "两装";break;
            case 3: str_load_num = "三装";break;
        }
        switch (unload_num){
            case 1: str_unload_num = "一卸";break;
            case 2: str_unload_num = "两卸";break;
            case 3: str_unload_num = "三卸";break;
        }
        str_transfer_num = str_load_num + str_unload_num;
        return str_transfer_num;
    }

    //得到净得运费字符串
    private String get_pure_freight(BigDecimal freight, BigDecimal deposit, int if_return){
        String str_pure_freight = "";
        if (if_return == 0){//订金不退还：成功装货后司机支付的订金作为“信息费”支付给货主
            str_pure_freight = freight.subtract(deposit).toString() + "元/趟";
        }else if (if_return == 1){//订金退还：货主确认收货后，司机支付的订金作为“履约保证金”退还
            str_pure_freight = freight.toString() + "元/趟";
        }
        return str_pure_freight;
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

    private void show_confirm_dialog(String str,Thread thread){
        dialog = new Dialog(mContext,R.style.Dialog_Style);
        dialog.setContentView(R.layout.dialog_confirm_exit);
        TextView tv_dialog_text = dialog.findViewById(R.id.tv_dialog_text);
        tv_dialog_text.setText(str);
        dialog.show();
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.start();
                dialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void show_result_dialog(String str_dialog){
        Dialog dialog = new Dialog(mContext,R.style.Result_Dialog_Style);
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
