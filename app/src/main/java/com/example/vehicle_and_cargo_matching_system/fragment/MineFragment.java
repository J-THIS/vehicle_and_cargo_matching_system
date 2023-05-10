package com.example.vehicle_and_cargo_matching_system.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vehicle_and_cargo_matching_system.R;
import com.example.vehicle_and_cargo_matching_system.activity.AttentionActivity;
import com.example.vehicle_and_cargo_matching_system.activity.ChangePasswordActivity;
import com.example.vehicle_and_cargo_matching_system.activity.LoginActivity;
import com.example.vehicle_and_cargo_matching_system.activity.OrderActivity;
import com.example.vehicle_and_cargo_matching_system.activity.VehicleInformationActivity;
import com.example.vehicle_and_cargo_matching_system.util.ActivityStackUtil;

public class MineFragment extends Fragment {
    private String id,surname,mPosition,mRegion;
    public Button exit;
    public ImageButton ib_unfilled,ib_transit,ib_evaluation,ib_cancel,change_password,car_information;
    private TextView tv_id,tv_username,tv_all_order;
    private RelativeLayout rl_my_attention;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            id = bundle.getString("id");
            surname = bundle.getString("surname");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        initView(view);
        initEvent();
        initData();
        return view;
    }

    public void initLocation(String mPosition, String mRegion){
        this.mPosition = mPosition;
        this.mRegion = mRegion;
    }

    private void initData() {
        //获取从登录界面传来的ID和手机号并显示
        String userName = surname + "师傅";
        tv_id.setText(id);
        tv_username.setText(userName);
    }

    private void initView(View view){
        tv_id = view.findViewById(R.id.tv_id);
        rl_my_attention = view.findViewById(R.id.rl_my_attention);
        tv_username= view.findViewById(R.id.tv_user_name);
        exit = view.findViewById(R.id.exit);
        tv_all_order = view.findViewById(R.id.tv_all_order);
        ib_unfilled = view.findViewById(R.id.ib_unfilled);
        ib_transit = view.findViewById(R.id.ib_transit);
        ib_evaluation = view.findViewById(R.id.ib_evaluation);
        ib_cancel = view.findViewById(R.id.ib_cancel);
        change_password = view.findViewById(R.id.change_password);
        car_information = view.findViewById(R.id.car_information);
    }

    private void initEvent(){
        //添加点击事件监听
        OnClick onClick = new OnClick();
        exit.setOnClickListener(onClick);
        tv_all_order.setOnClickListener(onClick);
        ib_unfilled.setOnClickListener(onClick);
        ib_transit.setOnClickListener(onClick);
        ib_evaluation.setOnClickListener(onClick);
        ib_cancel.setOnClickListener(onClick);
        change_password.setOnClickListener(onClick);
        car_information.setOnClickListener(onClick);
        rl_my_attention.setOnClickListener(onClick);
    }

    //点击事件
    private class OnClick implements View.OnClickListener{

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.tv_all_order:
                    intent = new Intent(getActivity(), OrderActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    intent.putExtra("tab",0);
                    startActivity(intent);
                    break;
                case R.id.ib_unfilled:
                    intent = new Intent(getActivity(), OrderActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    intent.putExtra("tab",1);
                    startActivity(intent);
                    break;
                case R.id.ib_transit:
                    intent = new Intent(getActivity(), OrderActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    intent.putExtra("tab",2);
                    startActivity(intent);
                    break;
                case R.id.ib_evaluation:
                    intent = new Intent(getActivity(), OrderActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    intent.putExtra("tab",3);
                    startActivity(intent);
                    break;
                case R.id.ib_cancel:
                    intent = new Intent(getActivity(), OrderActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    intent.putExtra("tab",4);
                    startActivity(intent);
                    break;
                case R.id.change_password:
                    intent = new Intent(v.getContext(), ChangePasswordActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                    break;
                case R.id.car_information:
                    intent = new Intent(v.getContext(), VehicleInformationActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                    break;
                case R.id.exit:
                    Dialog dialog = new Dialog(getActivity(),R.style.Dialog_Style);
                    dialog.setContentView(R.layout.dialog_confirm_exit);
                    dialog.show();
                    Button btn_yes = dialog.findViewById(R.id.btn_yes);
                    Button btn_no = dialog.findViewById(R.id.btn_no);
                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            ActivityStackUtil.getAppManager().finishActivity(getActivity());
                        }
                    });
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    break;
                case R.id.rl_my_attention:
                    intent = new Intent(getActivity(), AttentionActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mPosition",mPosition);
                    intent.putExtra("mRegion",mRegion);
                    startActivity(intent);
                    break;
            }
        }
    }

    //用于Activity向Fragment中传入数据
    public static MineFragment newInstance(String id, String surname){
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("surname", surname);
        MineFragment mineFragment = new MineFragment();
        mineFragment.setArguments(bundle);
        return mineFragment;
    }
}
