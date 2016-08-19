package com.szxyyd.xyhl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.fragment.OrderFragment;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.http.VolleyRequestUtil;
import com.szxyyd.xyhl.inf.VolleyListenerInterface;
import com.szxyyd.xyhl.modle.Order;
import com.szxyyd.xyhl.pay.PayTools;
import com.szxyyd.xyhl.utils.CommUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 确认订单
 * @author jq
 *
 */
public class ConfirmOrderActivity extends Activity implements View.OnClickListener{
	private CheckBox cb_weixin;
	private CheckBox rb_pay;
	private Button btn_confirm;
	private Button btn_back;
	private TextView tv_title;
	private RelativeLayout rl_addr;
	private Order order;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);
		order = (Order) getIntent().getSerializableExtra("order");
		initView();
	}
   private void initView(){
	   tv_title = (TextView) findViewById(R.id.tv_title);
	   tv_title.setText("订单支付");
	   btn_back = (Button) findViewById(R.id.btn_back);
	   btn_confirm = (Button) findViewById(R.id.btn_confirm);
	   btn_confirm.setOnClickListener(this);
	   btn_back.setOnClickListener(this);
	   cb_weixin = (CheckBox) findViewById(R.id.cb_weixin);
	   rl_addr = (RelativeLayout) findViewById(R.id.rl_addr);
	   rl_addr.setVisibility(View.VISIBLE);
	   ImageView iv_next = (ImageView) findViewById(R.id.iv_next);
	   iv_next.setVisibility(View.GONE);
	   cb_weixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		   @Override
		   public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
				   rb_pay.setClickable(false);
			   }
		   }
	   });
	   rb_pay = (CheckBox) findViewById(R.id.rb_pay);
	   rb_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		   @Override
		   public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			   if(b){
				   cb_weixin.setClickable(false);
			   }
		   }
	   });
	   initData();
   }
	private void initData(){
		((TextView)findViewById(R.id.tv_addr_name)).setText(order.getNursename());
		((TextView)findViewById(R.id.tv_addr_phone)).setText(order.getMobile());
		((TextView)findViewById(R.id.tv_addr)).setText(order.getAddr());
		((TextView)findViewById(R.id.tv_order_name)).setText(order.getNursename());
		((TextView)findViewById(R.id.tv_order_money)).setText(CommUtils.subStr(order.getCstpaysum())+".00");
	}

	/**
	 * 提交支付
	 */
	private void submitData(){
		//nur?a=nurseCmt	id (订单id) cstpaysum (支付金额)
		/*String id = order.getId();
		String cstpaysum = order.getCstpaysum();
		String url = Constant.aliPayUrl + "&id="+id;*/

		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.aliPayUrl);
		builder.put("id",order.getId());
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				Log.e("RegisterActivity","submitVerifiyData--result=="+result);
				confirmPay(result);
			}
		},this));

		/*VolleyRequestUtil volley = new VolleyRequestUtil();
		volley.RequestGet(this, url, "pay",
				new VolleyListenerInterface(this,VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
					@Override
					public void onSuccess(final String result) {
						Log.e("ConfirmOrderActivity", "ConfirmOrderActivity--result=="+result);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								confirmPay(result);
							}
						});
					//	parserData(result);
					}
					@Override
					public void onError(VolleyError error) {

					}
				});*/
	}
	/**
	 * 确定支付
	 */
	private void confirmPay(String sign){
		PayTools payTools = PayTools.getInstance(this);
		payTools.payByZFB(sign);
		payTools.setOnPayResultLinstener(new PayTools.OnPayResultLinstener() {
			@Override
			public void paySuccessfullResult(int payType, String message) {
				Log.e("ConfirmOrderActivity","payType=="+payType);
				Log.e("ConfirmOrderActivity","message=="+message);
				Intent intent = new Intent(ConfirmOrderActivity.this,OrderFragment.class);
				setResult(2, intent);
				finish();
			}
			@Override
			public void faildPayResult(int payType, String message) {

			}
		});
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_back:
				finish();
				break;
			case R.id.btn_confirm:
				submitData();
				break;
		}
	}
}
