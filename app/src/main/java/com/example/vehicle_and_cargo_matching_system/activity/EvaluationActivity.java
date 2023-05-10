package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.dao.ClientDao;
import com.example.vehicle_and_cargo_matching_system.dao.OrderDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.ClientDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class EvaluationActivity extends AppCompatActivity {

    private TextView tv_title,tv_back;
    private Button btn_submit,btn_good_evaluation,btn_poor_evaluation;
    private EditText et_content;
    private CheckBoxViewGroup cvg_good_evaluation,cvg_poor_evaluation;
    private String client_key,order_key,evaluation,good_evaluation_label,poor_evaluation_label;
    private ClientDao clientDao;
    private OrderDao orderDao;
    private Dialog dialog;
    private int evaluation_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ActivityStackUtil.getAppManager().addActivity(this);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        order_key = getIntent().getStringExtra("order_key");
        client_key = getIntent().getStringExtra("client_key");
        good_evaluation_label = "";
        poor_evaluation_label = "";
        evaluation_type = 1;//默认好评
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        btn_submit.setOnClickListener(onClick);
        btn_good_evaluation.setOnClickListener(onClick);
        btn_poor_evaluation.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.btn_good_evaluation:
                    btn_good_evaluation.setSelected(true);
                    btn_good_evaluation.setTextColor(getResources().getColor(R.color.white));
                    btn_poor_evaluation.setSelected(false);
                    btn_poor_evaluation.setTextColor(getResources().getColor(R.color.dark_orange));
                    cvg_poor_evaluation.setVisibility(View.GONE);
                    cvg_good_evaluation.setVisibility(View.VISIBLE);
                    et_content.setText("");
                    evaluation_type = 1;
                    break;
                case R.id.btn_poor_evaluation:
                    btn_poor_evaluation.setSelected(true);
                    btn_poor_evaluation.setTextColor(getResources().getColor(R.color.white));
                    btn_good_evaluation.setSelected(false);
                    btn_good_evaluation.setTextColor(getResources().getColor(R.color.dark_orange));
                    cvg_good_evaluation.setVisibility(View.GONE);
                    cvg_poor_evaluation.setVisibility(View.VISIBLE);
                    et_content.setText("");
                    evaluation_type = 0;
                    break;
                case R.id.btn_submit:
                    //生成评论
                    evaluation = String.valueOf(et_content.getText());
                    if (evaluation_type == 1) {//在好评面板
                        good_evaluation_label = good_evaluation_label.trim();//去掉首尾空格
                        evaluation = evaluation + " " + good_evaluation_label;
                    }else{//在差评面板
                        poor_evaluation_label = poor_evaluation_label.trim();//去掉首尾空格
                        evaluation = evaluation + " " + poor_evaluation_label;
                    }
                    //提交评论
                    @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 19) {
                                //已完成操作
                                dialog.dismiss();
                                Toast.makeText(ActivityStackUtil.getAppManager().
                                    getActivity(OrderActivity.class), "评论成功！",
                                        Toast.LENGTH_LONG).show();
                                OrderActivity orderActivity = (OrderActivity) ActivityStackUtil.getAppManager().
                                        getActivity(OrderActivity.class);
                                orderActivity.setData();
                                finish();
                            }
                        }
                    };
                    if (evaluation_type == 1){//在好评面板
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                clientDao = new ClientDaoImpl();
                                orderDao = new OrderDaoImpl();
                                try {
                                    clientDao.setApplauseNum(client_key);
                                    orderDao.setOrderEvaluation(order_key,evaluation_type,evaluation);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                msg.what = 19;
                                handler.sendMessage(msg);
                            }
                        });
                        show_confirm_dialog("您确定要提交评论吗？",thread);
                    }else{//在差评面板
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                clientDao = new ClientDaoImpl();
                                orderDao = new OrderDaoImpl();
                                try {
                                    clientDao.setComplaintNum(client_key);
                                    orderDao.setOrderEvaluation(order_key,evaluation_type,evaluation);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                msg.what = 19;
                                handler.sendMessage(msg);
                            }
                        });
                        show_confirm_dialog("您确定要提交评论吗？",thread);
                    }
                    break;
            }
        }
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("货主评价");
        tv_back = findViewById(R.id.tv_back);
        btn_submit = findViewById(R.id.btn_submit);
        btn_good_evaluation = findViewById(R.id.btn_good_evaluation);
        btn_poor_evaluation = findViewById(R.id.btn_poor_evaluation);
        et_content = findViewById(R.id.et_content);
        cvg_good_evaluation = findViewById(R.id.cvg_good_evaluation);
        cvg_poor_evaluation = findViewById(R.id.cvg_poor_evaluation);

        //初始显示好评面板
        btn_good_evaluation.setSelected(true);
        btn_good_evaluation.setTextColor(getResources().getColor(R.color.white));
        cvg_good_evaluation.setVisibility(View.VISIBLE);

        final ArrayList<String> goodEvaluationText = new ArrayList<>();
        goodEvaluationText.add("装卸货快"); goodEvaluationText.add("货源描述准确"); goodEvaluationText.add("运费结算及时");
        goodEvaluationText.add("尊重司机"); goodEvaluationText.add("装卸货不压车"); goodEvaluationText.add("主动解决问题");
        goodEvaluationText.add("联系方式畅通"); goodEvaluationText.add("运费可商议");
        cvg_good_evaluation = findViewById(R.id.cvg_good_evaluation);
        cvg_good_evaluation.addItemViews(goodEvaluationText, ConditionViewGroup.TEV_MODE);
        cvg_good_evaluation.setGroupClickListener(new CheckBoxViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                HashSet<Integer> hashSet = cvg_good_evaluation.getChosen_id();
                good_evaluation_label = "";
                for (int i:hashSet){
                    good_evaluation_label = good_evaluation_label + goodEvaluationText.get(i) + " ";
                }
            }
        });

        final ArrayList<String> poorEvaluationText = new ArrayList<>();
        poorEvaluationText.add("装卸货慢"); poorEvaluationText.add("货源描述不准确"); poorEvaluationText.add("克扣运费");
        poorEvaluationText.add("不尊重司机"); poorEvaluationText.add("装卸货压车"); poorEvaluationText.add("态度差");
        poorEvaluationText.add("多次联系不上"); poorEvaluationText.add("订单信息和实际不符");
        poorEvaluationText.add("临时变更装卸信息"); poorEvaluationText.add("随意取消订单");
        cvg_poor_evaluation = findViewById(R.id.cvg_poor_evaluation);
        cvg_poor_evaluation.addItemViews(poorEvaluationText, ConditionViewGroup.TEV_MODE);
        cvg_poor_evaluation.setGroupClickListener(new CheckBoxViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                HashSet<Integer> hashSet = cvg_poor_evaluation.getChosen_id();
                poor_evaluation_label = "";
                for (int i:hashSet){
                    poor_evaluation_label = poor_evaluation_label + poorEvaluationText.get(i) + " ";
                }
            }
        });
    }

    private void show_confirm_dialog(String str,Thread thread){
        dialog = new Dialog(this,R.style.Dialog_Style);
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
}