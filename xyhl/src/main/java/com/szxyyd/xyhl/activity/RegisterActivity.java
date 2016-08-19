package com.szxyyd.xyhl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册界面
 * @author fq
 *
 */
public class RegisterActivity extends Activity implements OnClickListener{
	private TextView tv_title;
	private Button btn_get; //获取验证
	private Button btn_back;
	private Button btn_register;
	private EditText et_phone;
	private EditText et_password;
	private EditText et_authcode;
	private MyCount mc;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private String phone;
	private String password;
	private String getVerify = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
    	initEvent();
		preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	private void initView(){
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.tv_register));
	  et_phone = (EditText) findViewById(R.id.et_phone);
	  et_password = (EditText) findViewById(R.id.et_password);
		et_authcode = (EditText) findViewById(R.id.et_getcode);
  	  btn_back = (Button) findViewById(R.id.btn_back);
  	  btn_register = (Button) findViewById(R.id.btn_register);
		btn_get = (Button) findViewById(R.id.btn_get);
    }
    private void initEvent(){
  	  btn_back.setOnClickListener(this);
  	  btn_register.setOnClickListener(this);
		btn_get.setOnClickListener(this);
    }
	/**
	 * 定义一个倒计时的内部类
	 */
	private class MyCount extends CountDownTimer{

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			btn_get.setText((millisUntilFinished / 1000) + "");
		}
		@Override
		public void onFinish() {
			btn_get.setText("获取");
		}
	}
	private void showToast(String str){
		Toast.makeText(RegisterActivity.this,str,Toast.LENGTH_SHORT).show();
	}
	/**
	 * 请求验证码数据
	 */
	private void submitGetVerifyData(){
		phone = et_phone.getText().toString().trim();
		if(phone.length() == 0){
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
				getVerify = data;
			//	Log.e("RegisterActivity","submitVerifiyData--getVerify=="+getVerify);
				mc.cancel();
			}
		},this));
	}
	/**
	 * 请求注册数据
	 */
	private void submitRegisterData(){
		phone = et_phone.getText().toString().trim();
		password = et_password.getText().toString().trim();
		if(phone.length() == 0){
			showToast("手机号码不能为空");
			return;
		}
		if(phone.length() < 11){
			showToast("请输入正确的手机号码");
			return;
		}
		if(password.length() == 0){
			showToast("密码不能为空");
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
				//	Log.e("RegisterActivity","submitVerifiyData--json.length()=="+json.length());
					if(json.isNull("type")){
						Toast.makeText(RegisterActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
						finish();
					}else{
						String str = json.getString("msg");
						if(str.equals("手机号已经注册过了!")){
							Toast.makeText(RegisterActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(RegisterActivity.this,json.getString("msg"),Toast.LENGTH_SHORT).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		},this));
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.btn_get:
				submitGetVerifyData();
				break;
			case R.id.btn_register:  //80001912
				submitRegisterData();
				break;
			default:
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
