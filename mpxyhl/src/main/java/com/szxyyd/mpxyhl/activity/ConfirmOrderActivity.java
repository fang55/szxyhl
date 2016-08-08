package com.szxyyd.mpxyhl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.fragment.MyOrderFragment;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.http.VolleyRequestUtil;
import com.szxyyd.mpxyhl.inter.VolleyListenerInterface;
import com.szxyyd.mpxyhl.modle.Order;
import com.szxyyd.mpxyhl.pay.PayTools;
import com.szxyyd.mpxyhl.utils.CommUtils;

/**
 * 支付界面
 * Created by fq on 2016/7/7.
 */
public class ConfirmOrderActivity extends Activity implements View.OnClickListener{
    private CheckBox cb_weixin;
    private CheckBox rb_pay;
    private Button btn_confirm;
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
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单支付");
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_confirm = (Button) findViewById(R.id.btn_next);
        btn_confirm.setText("确定支付");
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
                }else{
                    rb_pay.setClickable(true);
                }
            }
        });
        rb_pay = (CheckBox) findViewById(R.id.rb_pay);
        rb_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    cb_weixin.setClickable(false);
                }else{
                    cb_weixin.setClickable(true);
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
       /* String id = order.getId();
        String cstpaysum = order.getCstpaysum();
       // String url = Constant.odrPayUrl + "&id="+id+"&cstpaysum="+cstpaysum;
        String url = Constant.aliPayUrl + "&id="+id;*/
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.aliPayUrl);
        builder.put("id",order.getId());
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                confirmPay(result);
            }
        },this));
       /* VolleyRequestUtil.newInstance().RequestGet(this, url, "pay",
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
                Intent intent = new Intent(ConfirmOrderActivity.this,MyOrderFragment.class);
                setResult(1, intent);
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
            case R.id.btn_next:
                submitData();
                break;
        }
    }
}
