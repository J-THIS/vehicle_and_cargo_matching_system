package com.example.vehicle_and_cargo_matching_system.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.dao.impl.DriverDaoImpl;
import com.example.vehicle_and_cargo_matching_system.dao.impl.OrderDaoImpl;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VehicleInformationActivity extends AppCompatActivity {

    private TextView tv_back,tv_title;
    private EditText et_inner_length,et_inner_width,et_inner_high;
    private ConditionViewGroup cvg_car_type,cvg_use_length,cvg_equipment;
    private Button btn_submit;
    private DriverDao driverDao;
    private Dialog dialog;
    private String id,car_type,use_length,equipment;
    private BigDecimal inner_length,inner_width,inner_high;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_information);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        car_type = "平板";
        use_length = "1.8";
        equipment = "无";
    }

    private void initEvent() {
        OnClick onClick = new OnClick();
        tv_back.setOnClickListener(onClick);
        btn_submit.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener{

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.btn_submit:
                    if (!confirm()){//若信息填写不完整
                        break;
                    }
                    @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 21) {
                                if ((int)msg.obj != 0){
                                    //已完成操作
                                    dialog.dismiss();
                                    Toast.makeText(ActivityStackUtil.getAppManager().
                                                    getActivity(MainActivity.class), "操作成功！",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                }else{
                                    Toast.makeText(ActivityStackUtil.getAppManager().
                                                    getActivity(MainActivity.class), "操作失败！",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    };
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            driverDao = new DriverDaoImpl();
                            int result = 0;
                            try {
                                result = driverDao.setCarInformation(id,car_type,use_length,inner_length,
                                        inner_width,inner_high,equipment);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.what = 21;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    });
                    show_confirm_dialog("您确定要提交车辆信息吗？",thread);
                    break;
            }
        }
    }

    private boolean confirm() {
        if (!et_inner_length.getText().toString().equals("") &&
                !et_inner_width.getText().toString().equals("") &&
                !et_inner_high.getText().toString().equals("")){//若输入不为空
            inner_length = new BigDecimal(et_inner_length.getText().toString().trim());
            inner_width = new BigDecimal(et_inner_width.getText().toString().trim());
            inner_high = new BigDecimal(et_inner_high.getText().toString().trim());
            return true;
        }else{
            Toast.makeText(this, "尚未填写车内空间信息", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void initView() {
        tv_back = findViewById(R.id.tv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("完善车辆信息");
        et_inner_length = findViewById(R.id.et_inner_length);
        //限制输入长度
        et_inner_length.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        et_inner_width = findViewById(R.id.et_inner_width);
        et_inner_width.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        et_inner_high = findViewById(R.id.et_inner_high);
        et_inner_high.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        cvg_car_type = findViewById(R.id.cvg_car_type);
        cvg_use_length = findViewById(R.id.cvg_use_length);
        cvg_equipment = findViewById(R.id.cvg_equipment);
        btn_submit = findViewById(R.id.btn_submit);

        final ArrayList<String> carTypeText = new ArrayList<>();
        carTypeText.add("平板"); carTypeText.add("高栏");
        carTypeText.add("厢式"); carTypeText.add("集装箱"); carTypeText.add("自卸");
        carTypeText.add("冷藏"); carTypeText.add("保温"); carTypeText.add("高低板");
        carTypeText.add("面包车"); carTypeText.add("棉被车"); carTypeText.add("爬梯车");
        carTypeText.add("飞翼车"); carTypeText.add("依维柯");
        cvg_car_type = findViewById(R.id.cvg_car_type);
        cvg_car_type.addItemViews(carTypeText, ConditionViewGroup.TEV_MODE);
        cvg_car_type.chooseItemStyle(0);
        cvg_car_type.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                car_type = carTypeText.get(item);
            }
        });

        final ArrayList<String> carLengthText = new ArrayList<>();
        carLengthText.add("1.8"); carLengthText.add("2.7"); carLengthText.add("3.8");
        carLengthText.add("4.2"); carLengthText.add("5"); carLengthText.add("6.2");
        carLengthText.add("6.8"); carLengthText.add("7.7"); carLengthText.add("8.2");
        carLengthText.add("8.7"); carLengthText.add("9.6"); carLengthText.add("11.7");
        carLengthText.add("12.5"); carLengthText.add("13"); carLengthText.add("13.7");
        carLengthText.add("15"); carLengthText.add("16"); carLengthText.add("17.5");
        cvg_use_length = findViewById(R.id.cvg_use_length);
        cvg_use_length.addItemViews(carLengthText, ConditionViewGroup.TEV_MODE);
        cvg_use_length.chooseItemStyle(0);
        cvg_use_length.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                use_length = carLengthText.get(item);
            }
        });

        final ArrayList<String> equipmentText = new ArrayList<>();
        equipmentText.add("无");equipmentText.add("带尾板"); equipmentText.add("带A字架");
        cvg_equipment = findViewById(R.id.cvg_equipment);
        cvg_equipment.addItemViews(equipmentText, ConditionViewGroup.TEV_MODE);
        cvg_equipment.chooseItemStyle(0);
        cvg_equipment.setGroupClickListener(new ConditionViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                equipment = equipmentText.get(item);
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