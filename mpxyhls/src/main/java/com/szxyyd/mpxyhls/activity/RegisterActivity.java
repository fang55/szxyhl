package com.szxyyd.mpxyhls.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.szxyyd.mpxyhls.R;
import com.szxyyd.mpxyhls.http.VolleyRequestUtil;
import com.szxyyd.mpxyhls.inter.VolleyListenerInterface;
import com.szxyyd.mpxyhls.utils.ExampleUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jq on 2016/7/12.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_verifiy;
    private EditText et_password;
    private TextView tv_getverifiy;//获取验证
    private MyCount mc;
    private String phone;
    private String password;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
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
            tv_getverifiy.setText("0");
        }
    }
    private void showToast(String str){
        Toast.makeText(RegisterActivity.this,str,Toast.LENGTH_SHORT).show();
    }
    /**
     * 提交数据
     */
    private void submitData(final String type){
        phone = et_phone.getText().toString().trim();
        password = et_password.getText().toString().trim();
        if(phone == null){
            ExampleUtil.showToast(getString(R.string.tv_phone_null),this);
            return;
        }
        if(phone.length() < 11){
            ExampleUtil.showToast(getString(R.string.tv_phone_null),this);
            return;
        }
        String url = null;
        Map<String,String> map = new HashMap<String,String>();
        if(type.equals("getVer")){  //获取验证码
            mc = new MyCount(60000, 1000);
            mc.start();
            url = Constant.getVerifiUrl;
            map.put("mobile",phone);
        }else if(type.equals("register")){  //注册
            if(password == null){
                showToast("密码不能为空");
            }
            url = Constant.registerUrl;
            map.put("usr",phone);
            map.put("pwd",password);
        }
        VolleyRequestUtil.newInstance().RequestPost(this,url,type,map,new VolleyListenerInterface(this,VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {
            @Override
            public void onSuccess(String result) {
                Log.e("RegisterActivity", "result=="+result);
                parserData(result,type);
            }
            @Override
            public void onError(VolleyError error) {

            }
        });
    }
    /**
     * 解析返回的数据
     * @param result
     */
    private void parserData(String result,String type){
        if(type.equals("getVer")){
            mc.cancel();
            editor.putString("verify", result);
            editor.commit();
        }else{
            editor.putString("usr", phone);
            editor.putString("password", password);
            editor.putString("userId", result);
            editor.commit();
            Toast.makeText(this,"提交成功",Toast.LENGTH_SHORT).show();
            finish();
        }
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
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.tv_getverifiy:
                submitData("getVer");
                break;
            case R.id.btn_register:
                submitData("register");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mc != null){
            mc.cancel();
        }
    }
}
