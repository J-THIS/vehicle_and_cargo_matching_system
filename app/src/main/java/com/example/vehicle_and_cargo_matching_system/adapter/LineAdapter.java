package com.example.vehicle_and_cargo_matching_system.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.vehicle_and_cargo_matching_system.activity.ResourceActivity;
import com.example.vehicle_and_cargo_matching_system.activity.ResourceDetailActivity;
import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;
import com.example.vehicle_and_cargo_matching_system.dao.LineDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientAttentionDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.LineDaoImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LineAdapter extends BaseAdapter {

    private List<LineAttention> line_list; //线路列表数据
    private Context mContext;
    private String id,mPosition;
    private LineDao lineDao;

    public LineAdapter(Context mContext, String id,String mPosition){
        this.mContext = mContext;
        this.id = id;
        this.mPosition = mPosition;
    }

    //设置数据更新界面
    public void setData(List<LineAttention> line_list){
        this.line_list=line_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return line_list==null?0:line_list.size();
    }

    @Override
    public LineAttention getItem(int position) {
        return line_list==null?null:line_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        //复用convertView
        if (convertView==null){
            vh = new ViewHolder();
            View view = LayoutInflater.from(mContext).inflate(R.layout.line_item,null);
            vh.tv_start = view.findViewById(R.id.tv_start);
            vh.tv_end = view.findViewById(R.id.tv_end);
            vh.tv_use_type = view.findViewById(R.id.tv_use_type);
            vh.tv_car_length = view.findViewById(R.id.tv_car_length);
            vh.tv_car_type = view.findViewById(R.id.tv_car_type);
            vh.btn_delete_line = view.findViewById(R.id.btn_delete_line);
            final LineAttention line = getItem(position);
            if (line != null){
                vh.tv_start.setText(get_region(line.getLoadPlace()));
                vh.tv_end.setText(get_region(line.getUnloadPlace()));
                if(line.getUseType()!=null){
                    vh.tv_use_type.setText(line.getUseType());
                }else{
                    vh.tv_use_type.setText("不限");
                }
                if(line.getCarLength()!=null){
                    BigDecimal bd = line.getCarLength();
                    bd=bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    vh.tv_car_length.setText(bd.toString());
                }else{
                    vh.tv_car_length.setText("不限");
                }
                if (line.getCarType()!=null){
                    vh.tv_car_type.setText(line.getCarType());
                }else{
                    vh.tv_car_type.setText("不限");
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到按线路搜索货源页面
                        Intent intent = new Intent(mContext, ResourceActivity.class);
                        //把装卸地址传递到按线路搜索货源页面
                        intent.putExtra("id",id);
                        intent.putExtra("mPosition",mPosition);
                        intent.putExtra("load_place",line.getLoadPlace());
                        intent.putExtra("unload_place",line.getUnloadPlace());
                        intent.putExtra("use_type",line.getUseType());
                        if (line.getCarLength() == null){
                            intent.putExtra("car_length",line.getCarLength());
                        }else{
                            BigDecimal bd = line.getCarLength();
                            bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                            intent.putExtra("car_length",bd.toString());
                        }
                        intent.putExtra("car_type",line.getCarType());
                        mContext.startActivity(intent);
                    }
                });

                vh.btn_delete_line.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(@NonNull Message msg) {
                                if (msg.what == 12){
                                    show_delete_line_dialog(v,"删除成功");
                                }
                                return true;
                            }
                        });
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                lineDao = new LineDaoImpl();
                                try {
                                    lineDao.deleteLineAttention(line.getLineKey());
                                    Message msg = new Message();
                                    msg.what = 12;
                                    handler.sendMessage(msg);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                });
            }
            return view;
        }else {
            return convertView;
        }
    }

    /*由于ui列表项显示空间有限，只能将原有mRegion信息裁剪成“市+区”地名才能装得下，
    也就是要去掉省级地名。百度地图api返回的mRegion形式中，特别行政区示例为“香港香港荃湾区”，
    直辖市示例为“北京市北京市东城区”*/
    private String get_region(String load_region){
        String str_load_region = "";
        if (Objects.equals(load_region, "全国")){
            return load_region;
        }
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

    private void show_delete_line_dialog(View v,String str_dialog){
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
        }, 2000);
    }

    class ViewHolder{
        public TextView tv_start, tv_end,tv_use_type,tv_car_length,tv_car_type;
        public Button btn_delete_line;
    }
}
