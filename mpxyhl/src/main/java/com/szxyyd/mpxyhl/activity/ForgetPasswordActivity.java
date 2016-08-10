package com.szxyyd.mpxyhl.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.utils.ExampleUtil;

/**
 * 忘記密碼
 * Created by jq on 2016/7/4.
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout ll_content = null;
    private TextView tv_title  = null;
    private EditText et_psdPhone  = null;
    private EditText et_code  = null;
    private EditText et_newpsd  = null;
    private EditText et_resetpsd  = null;
    private TextView tv_getverifiy  = null;
    private Button btn_del  = null;
    private MyCount mc  = null;
    private String phone  = null;
    private String password  = null;
    private String getVerifiy  = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpsd);
        initView();
    }
    private void initView(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.tv_forget));
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        showPassword();
    }
    /**
     * 忘记密码界面
     */
    private void showPassword(){
        ll_content.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.view_forgetpsd, null, false);
        Button btn_reset = (Button) view.findViewById(R.id.btn_reset);
        et_psdPhone = (EditText) view.findViewById(R.id.et_psdPhone);
        et_code = (EditText) view.findViewById(R.id.et_code);
        tv_getverifiy = (TextView) view.findViewById(R.id.tv_getverifiy);
        btn_reset.setOnClickListener(this);
        tv_getverifiy.setOnClickListener(this);
        ll_content.addView(view);
    }
    /**
     * 重置密码界面
     */
    private void showResetPsd(){
        ll_content.removeAllViews();
        tv_title.setText(getString(R.string.btn_set));
        View view = LayoutInflater.from(this).inflate(R.layout.view_resetpsd, null, false);
        et_newpsd = (EditText) view.findViewById(R.id.et_newpsd);
        et_resetpsd = (EditText) view.findViewById(R.id.et_resetpsd);
        btn_del = (Button) view.findViewById(R.id.btn_del);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        ll_content.addView(view);
    }

    /**
     * 请求验证码
     */
    private void submitVerifiyData(){
        phone = et_psdPhone.getText().toString().trim();
        if(phone == null){
            ExampleUtil.showToast("手机号码不能为空",this);
            return;
        }
        if(phone.length() < 11){
            ExampleUtil.showToast("请输入正确的手机号码",this);
            return;
        }
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.getVerifiUrl);
        builder.put("mobile",phone);
        mc = new MyCount(60000, 1000);
        mc.start();
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                getVerifiy = data;
                Log.e("ForgetPasswordActivity","submitVerifiyData--getVerifiy=="+getVerifiy);
                mc.cancel();

            }
        },this));
    }
    /**
     * 提交密码
     */
    private void submitForgetPsdData(){
        String newpsd = et_newpsd.getText().toString();
        String resetpsd = et_resetpsd.getText().toString();
        if(newpsd == null){
            ExampleUtil.showToast("密码不能为空",this);
            return;
        }
        if(!newpsd.equals(resetpsd)){
            ExampleUtil.showToast("输入的密码不一致",this);
            return;
        }
        if(Constant.usrId == null){
            ExampleUtil.showToast("用户不存在，先注册",this);
            return;
        }
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.getPwdUrl);
        builder.put("id","80001989");
        builder.put("pwd",resetpsd);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                Log.e("ForgetPasswordActivity","submitForgetPsdData--data=="+data);
                ExampleUtil.showToast("提交成功",ForgetPasswordActivity.this);
                finish();
            }
        },this));
    }
    /**
     * 定义一个倒计时的内部类
     */
    private class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_getverifiy.setText((millisUntilFinished / 1000) + "");
        }
        @Override
        public void onFinish() {
            tv_getverifiy.setText(getString(R.string.tv_getverifiy));
        }
    }
    @Override
    public void onClick(View view) {
        phone = et_psdPhone.getText().toString().trim();
        switch (view.getId()){
            case R.id.btn_back:
              finish();
                break;
            case R.id.tv_getverifiy:
                if(phone.length() == 0){
                    ExampleUtil.showToast(getString(R.string.tv_phone_null),this);
                    return;
                }
                submitVerifiyData();
                break;
            case R.id.btn_reset:
                String verifiy  = et_code.getText().toString().trim();
                if(phone.length() == 0){
                    ExampleUtil.showToast(getString(R.string.tv_phone_null),this);
                    return;
                }
                if(phone.length() < 11){
                    ExampleUtil.showToast(getString(R.string.tv_phone_right),this);
                    return;
                }
                if(verifiy.length() == 0){
                    ExampleUtil.showToast(getString(R.string.et_writeverify),this);
                    return;
                }
                if(!verifiy.equals(getVerifiy)){
                    ExampleUtil.showToast("输入验证码不正确",this);
                    return;
                }
                showResetPsd();
                break;
            case R.id.btn_del:
                et_newpsd.setText("");
                break;
            case R.id.btn_ok:
                submitForgetPsdData();
                break;
        }
    }
}
