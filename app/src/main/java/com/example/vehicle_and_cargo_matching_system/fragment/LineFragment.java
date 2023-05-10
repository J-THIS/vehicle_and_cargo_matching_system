package com.example.vehicle_and_cargo_matching_system.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.activity.AddLineActivity;
import com.example.vehicle_and_cargo_matching_system.adapter.LineAdapter;
import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;
import com.example.vehicle_and_cargo_matching_system.dao.LineDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.LineDaoImpl;
import com.example.vehicle_and_cargo_matching_system.view.LineListView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LineFragment extends Fragment {
    private LineListView llv_line;
    private Button add_line_btn,btn_refresh;
    private LineAdapter lineAdapter;
    private List<LineAttention> lineAttentionList;
    private LineDao lineDao;
    private String id;//用户名
    private String mPosition;//用户当前所在位置
    private String mRegion;//用户当前所在地区
    private int attention_num;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            id = bundle.getString("id");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_line,container,false);
        initView(view);
        initAdapter();
        try {
            initData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initEvent();
        return view;
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        add_line_btn.setOnClickListener(onClick);
        btn_refresh.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_line_btn:
                    Intent intent = new Intent(getActivity(),AddLineActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    intent.putExtra("attention_num",attention_num);
                    startActivity(intent);
                    break;
                case R.id.btn_refresh:
                    try {
                        initAdapter();
                        initData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void initData() throws SQLException {
        lineDao = new LineDaoImpl();
        //用Handler处理异步线程中传来的数据
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==1){
                    lineAttentionList = (List<LineAttention>) msg.obj;
                    attention_num = lineAttentionList.size();
                    lineAdapter.setData(lineAttentionList);//将关注线路集数据传递到adapter中
                }
            }
        };
        //在异步线程中访问数据库获取关注线路数据集
        Thread thread = new Thread(new Runnable() {
            List<LineAttention> list = new ArrayList<>();
            @Override
            public void run() {
                try {
                    list = lineDao.getLineAttention(id);
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = list;
                    handler.sendMessage(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initAdapter() {
        lineAdapter = new LineAdapter(getActivity(),id,mPosition);
        llv_line.setAdapter(lineAdapter);
    }

    private void initView(View view) {
        llv_line = view.findViewById(R.id.llv_line);
        add_line_btn = view.findViewById(R.id.add_line_btn);
        btn_refresh = view.findViewById(R.id.btn_refresh);
    }

    public void initLocation(String mPosition,String mRegion){
        this.mPosition = mPosition;
        this.mRegion = mRegion;
    }

    //用于Activity向Fragment中传入数据
    public static LineFragment newInstance(String id){
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        LineFragment lineFragment = new LineFragment();
        lineFragment.setArguments(bundle);
        return lineFragment;
    }
}