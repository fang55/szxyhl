package com.szxyyd.xyhl.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * 忘记密码
 * @author jq
 *
 */
public class ForgetPasswordActivity extends Activity implements OnClickListener{
	private TextView tv_title;
	private TextView tv_code;
	private EditText et_phone;
	private EditText et_code;
	private Button btn_back;
	private LinearLayout ll_content;
	private EditText et_newpsd;
	private EditText et_resetpsd;
	private String getVerify = null;
	private MyCount mc = null;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_forgetpassword);
    	initView();
    	initEvent();
		showPassword();
    }
      private void initView(){
		  tv_title = (TextView) findViewById(R.id.tv_title);
		  btn_back = (Button) findViewById(R.id.btn_back);
		  ll_content = (LinearLayout) findViewById(R.id.ll_content);
      }
      private void initEvent(){
    	  btn_back.setOnClickListener(this);
      }
	/**
	 * 忘记密码
	 */
	private void showPassword(){
		ll_content.removeAllViews();
		tv_title.setText(getString(R.string.tv_forget));
		View view = LayoutInflater.from(this).inflate(R.layout.view_forgetpsd, null, false);
		Button btn_next = (Button) view.findViewById(R.id.btn_next);
		et_phone = (EditText) view.findViewById(R.id.et_phone);
		tv_code = (TextView) view.findViewById(R.id.tv_code);
		et_code = (EditText) view.findViewById(R.id.et_code);
		tv_code.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		ll_content.addView(view);
	}
	/**
	 * 重置密码
	 */
	private void showResetPsd(){
		ll_content.removeAllViews();
		tv_title.setText(getString(R.string.btn_reset));
		View view = LayoutInflater.from(this).inflate(R.layout.view_resetpsd, null, false);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		et_newpsd = (EditText) view.findViewById(R.id.et_newpsd);
		et_resetpsd = (EditText) view.findViewById(R.id.et_resetpsd);
		Button btn_del = (Button) view.findViewById(R.id.btn_del);
		btn_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				et_newpsd.setText("");
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submitPassWordData();
			}
		});
		ll_content.addView(view);
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
			tv_code.setText((millisUntilFinished / 1000) + "");
		}
		@Override
		public void onFinish() {
			tv_code.setText("获取验证码");
			tv_code.setClickable(true);
		}
	}
	/**
	 * 请求验证码数据
	 */
	private void submitGetVerifyData(){
		String phone = et_phone.getText().toString().trim();
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
				Log.e("RegisterActivity","submitVerifiyData--getVerify=="+getVerify);
				mc.cancel();
				tv_code.setClickable(false);
			}
		},this));
	}
	/**
	 * 找回密码
	 */
	private void submitPassWordData(){
		String newPsd = et_newpsd.getText().toString().trim();
		String resetPsd = et_resetpsd.getText().toString().trim();
		if(newPsd.length() == 0){
			showToast("请输入新密码");
			return;
		}
		if(resetPsd.length() == 0){
			showToast("请确认密码");
			return;
		}
		if(!resetPsd.equals(newPsd)){
			showToast("两次输入密码不一致");
			return;
		}
		if(Constant.cstId == null){
			showToast("还未注册");
			return;
		}
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.getPwdUrl);
		builder.put("id",Constant.usrId);
		builder.put("pwd",resetPsd);
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String data) {
			//	Log.e("RegisterActivity","submitPassWordData--data=="+data);
				parserData(data);
			}
		},this));
	}
	/**
	 * 解析数据
	 * @param result
     */
	private void parserData(String result){
		try {
			JSONObject json = new JSONObject(result);
			if(json.isNull("type")){
				showToast("已修改");
				finish();
			}else{
				showToast(json.getString("msg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showToast(String str){
		Toast.makeText(ForgetPasswordActivity.this,str,Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.tv_code:  //获取验证码
				submitGetVerifyData();
				break;
			case R.id.btn_next:
				String phone = et_phone.getText().toString().trim();
				String code = et_code.getText().toString().trim();
				if(phone.length() == 0){
					showToast("手机号码不能为空");
					return;
				}
				if(code.length() == 0){
					showToast("验证码不能为空");
					return;
				}
				if(!code.equals(getVerify)){
					showToast("验证码输入不正确");
					return;
				}
				showResetPsd();
				break;
			default:
				break;
		}

	}
}
