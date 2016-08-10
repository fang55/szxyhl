package com.szxyyd.mpxyhl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册
 * Created by jq on 2016/7/4.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private EditText et_phone = null;
    private EditText et_verifiy = null;
    private EditText et_password = null;
    private TextView tv_getverifiy = null;//获取验证
    private MyCount mc = null;
    private String phone = null;
    private String password = null;
    private String getVerifiy = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.btn_register));
        TextView tv_login = (TextView) findViewById(R.id.tv_login);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_verifiy = (EditText) findViewById(R.id.et_verifiy);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_getverifiy = (TextView) findViewById(R.id.tv_getverifiy);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        Button btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_getverifiy.setOnClickListener(this);
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
    private void showToast(String str){
        Toast.makeText(RegisterActivity.this,str,Toast.LENGTH_SHORT).show();
    }
    /**
     * 请求验证码
     */
    private void submitVerifiyData(){
        phone = et_phone.getText().toString().trim();
        if(phone == null){
            showToast("手机号码不能为空");
            return;
        }
        if(phone.length() < 11){
            showToast("请输入正确的手机号码");
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
                Log.e("RegisterActivity","submitVerifiyData--getVerifiy=="+getVerifiy);
                mc.cancel();

            }
        },this));
    }

    /**
     * 请求注册
     */
    private void submitRegisterData(){
        password = et_password.getText().toString().trim();
        String verifiy = et_verifiy.getText().toString().trim();
        if(password == null){
            showToast("密码不能为空");
            return;
        }
       if(verifiy.length() == 0){
           showToast("验证不能为空");
           return;
       }
        Log.e("RegisterActivity","submitRegisterData--getVerifiy=="+getVerifiy);
        if(!verifiy.equals(getVerifiy)){
            showToast("验证不正确");
            return;
        }
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.registerUrl);
        builder.put("usr",phone);
        builder.put("pwd",password);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    Log.e("RegisterActivity","submitVerifiyData--json.length()=="+json.length());
                    if(json.isNull("type")){
                        Toast.makeText(RegisterActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }else{
                        Toast.makeText(RegisterActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },this));
    }
    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.btn_back:
              finish();
              overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
              break;
          case R.id.tv_login:
              Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
              startActivity(intent);
              overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
              break;
          case R.id.tv_getverifiy:
              submitVerifiyData();
              break;
          case R.id.btn_register:
              submitRegisterData();
              break;
      }
    }
}
