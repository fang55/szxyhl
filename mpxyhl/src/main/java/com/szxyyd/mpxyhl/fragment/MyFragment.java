package com.szxyyd.mpxyhl.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.activity.Constant;
import com.szxyyd.mpxyhl.activity.IncomeActivity;
import com.szxyyd.mpxyhl.activity.MyCollectActivity;
import com.szxyyd.mpxyhl.activity.ServiceAddressActivity;
import com.szxyyd.mpxyhl.activity.SetActivity;
import com.szxyyd.mpxyhl.utils.PicassoUtils;
import com.szxyyd.mpxyhl.view.RoundImageView;

/**
 * Created by fq on 2016/7/6.
 */
public class MyFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my,container,false);
        initView();
        lodeNationData();
        return rootView;
    }
    private void initView(){
        RelativeLayout rl_location = (RelativeLayout) rootView.findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        RelativeLayout rl_income = (RelativeLayout) rootView.findViewById(R.id.rl_income);
        rl_income.setOnClickListener(this);
        RelativeLayout rl_collet = (RelativeLayout) rootView.findViewById(R.id.rl_collet);
        rl_collet.setOnClickListener(this);
        RelativeLayout rl_set = (RelativeLayout) rootView.findViewById(R.id.rl_set);
        rl_set.setOnClickListener(this);
    }
    private void lodeNationData(){
        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String nickname = preferences.getString("nickname", "");
        String icon =  preferences.getString("icon", "");
        RoundImageView iv_people = (RoundImageView) rootView.findViewById(R.id.iv_people);
        TextView tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_name.setText(nickname);
        PicassoUtils.loadImageViewHolder(getActivity(),Constant.nurseImage + icon,150,150,R.mipmap.teach,iv_people);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_location: //服务地址
                Intent intentLocation = new Intent(getActivity(),ServiceAddressActivity.class);
                startActivity(intentLocation);
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.rl_collet: //我的收藏
                Intent intentCollet = new Intent(getActivity(),MyCollectActivity.class);
                startActivity(intentCollet);
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.rl_set: //设置
                Intent intentSet = new Intent(getActivity(),SetActivity.class);
                startActivity(intentSet);
                //设置切换动画，从右边进入，左边退出
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.rl_income: //我的收益
                Intent intentCome = new Intent(getActivity(),IncomeActivity.class);
                startActivity(intentCome);
                //设置切换动画，从右边进入，左边退出
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
        }
    }
}
