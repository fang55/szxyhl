package com.szxyyd.mpxyhl.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.activity.Constant;
import com.szxyyd.mpxyhl.activity.MyCollectActivity;
import com.szxyyd.mpxyhl.activity.MyOrderActivity;
import com.szxyyd.mpxyhl.activity.ServiceAddressActivity;
import com.szxyyd.mpxyhl.activity.SetActivity;
import com.szxyyd.mpxyhl.utils.FileUtils;
import com.szxyyd.mpxyhl.utils.PicassoUtils;
import com.szxyyd.mpxyhl.view.RoundImageView;

/**
 * Created by fq on 2016/7/6.
 */
public class MyFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private RoundImageView iv_people;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my,container,false);
        initView();
        lodeNationData();
        return rootView;
    }
    private void initView(){
        RelativeLayout rl_order = (RelativeLayout) rootView.findViewById(R.id.rl_order);
        rl_order.setOnClickListener(this);
        RelativeLayout rl_location = (RelativeLayout) rootView.findViewById(R.id.rl_location);
        rl_location.setOnClickListener(this);
        RelativeLayout rl_collet = (RelativeLayout) rootView.findViewById(R.id.rl_collet);
        rl_collet.setOnClickListener(this);
        RelativeLayout rl_set = (RelativeLayout) rootView.findViewById(R.id.rl_set);
        rl_set.setOnClickListener(this);
    }
    private void lodeNationData(){
        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String nickname = preferences.getString("nickname", "");
        String icon =  preferences.getString("icon", "");
        iv_people = (RoundImageView) rootView.findViewById(R.id.iv_people);
        TextView tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_name.setText(nickname);
        showHeaderImage(icon);
    }
    private void showHeaderImage(String icon){
        String path = FileUtils.getSDPATH();
        if(TextUtils.isEmpty(path)){
            PicassoUtils.loadImageViewHolder(getActivity(),Constant.nurseImage + icon,150,150,R.mipmap.teach,iv_people);
        }else{
            String imagePath = path + "clipheader" + ".png";
            if(FileUtils.fileIsExists(imagePath)){
                Bitmap bitmap = FileUtils.getPhotoFromSDCard(imagePath);
                iv_people.setImageBitmap(bitmap);
            }else{
                PicassoUtils.loadImageViewHolder(getActivity(),Constant.nurseImage + icon,150,150,R.mipmap.teach,iv_people);
            }
        }
    }
    @Override
    public void onClick(View view) {
        Class mClass = null;
        switch (view.getId()){
            case R.id.rl_order:
                mClass = MyOrderActivity.class;
                break;
            case R.id.rl_location: //服务地址
                mClass = ServiceAddressActivity.class;
                break;
            case R.id.rl_collet: //我的收藏
                mClass = MyCollectActivity.class;
                break;
            case R.id.rl_set: //设置
                mClass = SetActivity.class;
                break;
        }
        Intent intentOrder = new Intent(getActivity(),mClass);
        startActivity(intentOrder);
        getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
}
