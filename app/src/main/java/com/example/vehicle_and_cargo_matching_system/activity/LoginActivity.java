package com.example.vehicle_and_cargo_matching_system.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.bean.Driver;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.DriverDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.sql.SQLException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button quick_register,forget_password,sing_in;
    public String password="",username="",id="";
    private DriverDao driverDao;
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityStackUtil.getAppManager().addActivity(this);
        initView();
        initEvent();
    }

    //button点击事件
    private class OnClick implements View.OnClickListener{

        @SuppressLint("Range")
        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()){
                case R.id.quick_register:
                    //点击注册按钮
                    intent=new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.forget_password:
                    //点击忘记密码按钮
                    intent=new Intent(LoginActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sing_in:
                    //点击登录按钮
                    EditText editText1 = (EditText) findViewById(R.id.username);
                    EditText editText2 = (EditText) findViewById(R.id.password);
                    //获取账号和密码
                    username = editText1.getText().toString();
                    password = editText2.getText().toString();
                    //异步线程进入数据库进行查询
                    driverDao = new DriverDaoImpl();
                    driver = new Driver();
                    Handler handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(@NonNull Message msg) {
                            if (msg.what == 9) {
                                driver = (Driver) msg.obj;
                                if (driver.getPhone() == null){
                                    Toast.makeText(LoginActivity.this, "该账号不存在", Toast.LENGTH_SHORT).show();
                                }else if(!Objects.equals(driver.getPassword(), password)){
                                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("driver",driver);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                            return false;
                        }
                    });
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 9;
                            Driver driver = new Driver();
                            try {
                                driver = driverDao.getDriver(username);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            msg.obj = driver;
                            handler.sendMessage(msg);
                        }
                    });
                    thread.start();
            }
        }
    }

    private void initEvent() {
        //对布局界面按钮添加监听事件
        OnClick onClick = new OnClick();
        quick_register.setOnClickListener(onClick);
        forget_password.setOnClickListener(onClick);
        sing_in.setOnClickListener(onClick);
    }

    private void initView() {
        quick_register = findViewById(R.id.quick_register);
        forget_password = findViewById(R.id.forget_password);
        sing_in = findViewById(R.id.sing_in);
    }
}
