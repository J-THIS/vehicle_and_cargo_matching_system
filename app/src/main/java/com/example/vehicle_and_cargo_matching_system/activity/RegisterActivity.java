package com.example.vehicle_and_cargo_matching_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.bean.Driver;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.DriverDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.sql.SQLException;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_phone,et_password,et_password_confirm,et_surname,et_given_name,et_id_number,
            et_licence_id,et_registration_id;
    private Button btn_finish;
    private CheckBox checkBox;
    private AlertDialog.Builder builder;
    private TextView tv_back,tv_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityStackUtil.getAppManager().addActivity(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_phone.getText().length() != 11) {
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
                } else if (et_surname.getText().length() == 0) {
                    builder.setTitle("通知")
                            .setMessage("姓不能为空")
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                } else if (et_given_name.getText().length() == 0) {
                    builder.setTitle("通知")
                            .setMessage("名不能为空")
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                } else if (et_id_number.getText().length() != 18) {
                    builder.setTitle("通知")
                            .setMessage("身份证格式不正确")
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                }else if (et_licence_id.getText().length() != 12) {
                    builder.setTitle("通知")
                            .setMessage("驾驶证格式不正确")
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                }else if (et_registration_id.getText().length() != 17) {
                    builder.setTitle("通知")
                            .setMessage("行驶证格式不正确")
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                }else if (!checkBox.isChecked()) {
                    builder.setTitle("通知")
                            .setMessage("请勾选协议")
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                }else{
                    register();
                }
            }
        });
    }

    private void register(){
        DriverDao driverDao = new DriverDaoImpl();
        Driver driver = new Driver();
        driver.setPhone(et_phone.getText().toString());
        driver.setPassword(et_password.getText().toString());
        driver.setSurName(et_surname.getText().toString());
        driver.setGivenName(et_given_name.getText().toString());
        driver.setIdNumber(et_id_number.getText().toString());
        driver.setLicenseId(et_licence_id.getText().toString());
        driver.setRegistrationId(et_registration_id.getText().toString());
        //检查数据库中是否已有该账号
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 8) {
                    if ((Integer)msg.obj == 0){
                        builder.setTitle("通知")
                                .setMessage("该手机号已被注册")
                                .setPositiveButton("确定", null)
                                .create()
                                .show();
                    }else if ((Integer)msg.obj == 1){
                        builder.setTitle("通知")
                                .setMessage("操作失败，请稍后重试")
                                .setPositiveButton("确定", null)
                                .create()
                                .show();
                    }else{
                        Toast.makeText(ActivityStackUtil.getAppManager().
                                        getActivity(LoginActivity.class), "评论成功！",
                                Toast.LENGTH_LONG).show();
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
                    if(driverDao.getDriver(et_phone.getText().toString())!=null){
                        msg.obj = 0;//代表此手机号已被注册过
                    }else if (driverDao.addDriver(driver)==0){
                        msg.obj = 1;//代表数据库插入操作失败
                    }
                    msg.obj = 2;//代表数据库插入操作成功
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private void initView(){
        tv_back = findViewById(R.id.tv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("注册账号");
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        et_password_confirm = findViewById(R.id.et_password_confirm);
        et_surname = findViewById(R.id.et_surname);
        et_given_name = findViewById(R.id.et_given_name);
        et_id_number = findViewById(R.id.et_id_number);
        et_licence_id = findViewById(R.id.et_licence_id);
        et_registration_id = findViewById(R.id.et_registration_id);
        btn_finish = findViewById(R.id.btn_finish);
        checkBox = findViewById(R.id.congirm);
        builder = new AlertDialog.Builder(RegisterActivity.this);
    }
}