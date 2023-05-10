package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.DriverDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.sql.SQLException;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText et_phone,et_password,et_password_confirm;
    private Button btn_confirm;
    private TextView tv_back,tv_title;
    private LinearLayout ll_phone;
    private AlertDialog.Builder builder;
    private String phone,newPassword,reNewPassword;
    private DriverDao driverDao;
    private int ifForget;//0-忘记密码 1-修改密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        ifForget = 0;
        ll_phone.setVisibility(View.VISIBLE);
        if (ActivityStackUtil.getAppManager().getActivity(MainActivity.class)!=null){
            Log.i("password",ActivityStackUtil.getAppManager().getActivityStack().size()+"");
            phone = getIntent().getStringExtra("id");
            ll_phone.setVisibility(View.GONE);
            tv_title.setText("修改密码");
            ifForget = 1;
        }
        initEvent();
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        btn_confirm.setOnClickListener(onClick);
        tv_back.setOnClickListener(onClick);
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("忘记密码");
        tv_back = findViewById(R.id.tv_back);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        et_password_confirm = findViewById(R.id.et_password_confirm);
        btn_confirm = findViewById(R.id.btn_confirm);
        ll_phone = findViewById(R.id.ll_phone);
        builder = new AlertDialog.Builder(ChangePasswordActivity.this);
    }

    private class OnClick implements View.OnClickListener {
        @SuppressLint("Range")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    //返回按钮
                    finish();
                    break;
                case R.id.btn_confirm:
                    //修改密码(由于验证码需要接入api，所以修改密码功能暂时不能使用)
                    if (ifForget==0){
                        phone = et_phone.getText().toString();
                    }
                    newPassword = et_password.getText().toString();
                    reNewPassword = et_password_confirm.getText().toString();
                    if (ifForget==0 && et_phone.getText().length() != 11) {
                        builder.setTitle("通知")
                                .setMessage("手机号格式不正确")
                                .setPositiveButton("确定", null)
                                .create()
                                .show();
                    }else if (!(et_password.getText().toString()).equals(et_password_confirm.getText().toString())) {
                        builder.setTitle("通知")
                                .setMessage("两次密码输入不一致")
                                .setPositiveButton("确定", null)
                                .create()
                                .show();
                    }else if ((et_password.getText().length() == 0) || (et_password_confirm.getText().length() == 0)) {
                        builder.setTitle("通知")
                                .setMessage("密码不能为空")
                                .setPositiveButton("确定", null)
                                .create()
                                .show();
                    }else{
                        driverDao = new DriverDaoImpl();
                        Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(@NonNull Message msg) {
                                if (msg.what == 8) {
                                    if ((Integer)msg.obj == 0){
                                        builder.setTitle("通知")
                                                .setMessage("该账号不存在")
                                                .setPositiveButton("确定", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(ActivityStackUtil.getAppManager().
                                                        getActivity(LoginActivity.class), "操作成功！",
                                                Toast.LENGTH_LONG).show();
                                        if (ifForget==1){//如果是修改密码页面，要先终结MainActivity
                                            ActivityStackUtil.getAppManager().finishActivity(MainActivity.class);
                                        }
                                        finish();
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = 8;
                                try {
                                    if(driverDao.setPassword(phone,newPassword)==0){
                                        msg.obj = 0;//代表此账号不存在
                                    }
                                    msg.obj = 1;//代表数据库更新操作成功
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                handler.sendMessage(msg);
                            }
                        });
                        thread.start();
                    }
                    break;
            }
        }
    }
}