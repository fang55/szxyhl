package com.szxyyd.mpxyhl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.MyOrderAdapter;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.HttpMethods;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.inter.SubscriberOnNextListener;
import com.szxyyd.mpxyhl.modle.JsonBean;
import com.szxyyd.mpxyhl.modle.Order;
import com.szxyyd.mpxyhl.modle.ProgressSubscriber;
import com.szxyyd.mpxyhl.view.PopupDialog;

import java.util.List;

/**
 * 我的订单
 * Created by fq on 2016/7/6.
 */
public class MyOrderActivity extends Activity {
    private RadioGroup rgChannel = null;
    public  RadioButton rb1 = null;
    private RadioButton rb2 = null;
    private RadioButton rb3 = null;
    private RadioButton rb4 = null;
    private RadioButton rb5 = null;
    private RadioButton rb6 = null;
    private GridView gv_order = null;
    private List<Order> list = null;
    private MyOrderAdapter adapter = null;
    private String ordCode = "0";
    private LinearLayout ll_notorder = null;
    public  int ORDER_STATES = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);
        initView();
        BaseApplication.getInstance().addActivity(this);
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的订单");
        Button btn_bakc = (Button) findViewById(R.id.btn_back);
        ll_notorder = (LinearLayout)findViewById(R.id.ll_notorder);
        gv_order = (GridView) findViewById(R.id.gv_order);
        gv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Order order = list.get(position);
                String states = order.getStatus();
                ordCode = states;
                showDetailsView(states,order);
            }
        });
        rgChannel=(RadioGroup)findViewById(R.id.rgChannel);
        rb1 = (RadioButton)findViewById(R.id.rb1);
        rb2 = (RadioButton)findViewById(R.id.rb2);
        rb3 = (RadioButton)findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);
        rb5 = (RadioButton)findViewById(R.id.rb5);
        rb6 = (RadioButton) findViewById(R.id.rb6);
        rgChannel.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rb1:
                                ORDER_STATES = 0;
                                changeTextColor(rb1,rb2,rb3,rb4,rb5,rb6);
                                break;
                            case R.id.rb2:
                                ORDER_STATES = 200;
                                changeTextColor(rb2,rb1,rb3,rb4,rb5,rb6);
                                break;
                            case R.id.rb3:
                                ORDER_STATES = 300;
                                changeTextColor(rb3,rb2,rb1,rb4,rb5,rb6);
                                break;
                            case R.id.rb4:
                                ORDER_STATES = 400;
                                changeTextColor(rb4,rb2,rb3,rb1,rb5,rb6);
                                break;
                            case R.id.rb5:
                                ORDER_STATES = 800;
                                changeTextColor(rb5,rb2,rb3,rb4,rb1,rb6);
                                break;
                            case R.id.rb6:
                                ORDER_STATES = 1100;
                                changeTextColor(rb6,rb2,rb3,rb4,rb5,rb1);
                                break;
                        }
                        getData(ORDER_STATES);
                    }
                });
        rb2.setChecked(true);
        btn_bakc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(MyOrderActivity.this,HomePagerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }
    private void changeTextColor(RadioButton tv1,RadioButton tv2,RadioButton tv3,RadioButton tv4,RadioButton tv5,RadioButton tv6){
        tv1.setTextColor(getResources().getColor(R.color.color_bule));
        tv2.setTextColor(getResources().getColor(R.color.color_six_six));
        tv3.setTextColor(getResources().getColor(R.color.color_six_six));
        tv4.setTextColor(getResources().getColor(R.color.color_six_six));
        tv5.setTextColor(getResources().getColor(R.color.color_six_six));
        tv6.setTextColor(getResources().getColor(R.color.color_six_six));
    }
    /**
     * 根据状态提交数据
     * @param getCode
     */
    private void sumbitTyoeData(String getCode,Order order){
        if(getCode.equals("300")){ //待支付
            showDetailsView(ordCode,order);
        }else if(getCode.equals("400") ){ //待服务
            ordCode = "400";
            submitData(order,"800");
        }else if(getCode.equals("800")){ //服务中
            ordCode = "800";
            submitData(order,"1100");
        }else if(getCode.equals("1100")){ //待评价
            ordCode = "1100";
            showDetailsView(ordCode,order);
        }
    }
    /**
     * 根据状态不同进入详情、支付、评价界面
     * @param states
     */
   private void showDetailsView(String states,Order order){
       if(states.equals("300")){  //待支付
           Intent intentConfirm = new Intent(this,ConfirmOrderActivity.class);
           intentConfirm.putExtra("order",  order);
           startActivityForResult(intentConfirm,  1);
       }else if(states.equals("1100")){ //评价
           Intent intentComment = new Intent(this,OrderCommentActivity.class);
           intentComment.putExtra("order", order);
           startActivityForResult(intentComment,  2);
       }else{ //订单详情
           Intent intentDetails = new Intent(this,OrderDetailsActivity.class);
           intentDetails.putExtra("order", order);
           startActivityForResult(intentDetails,  3);
       }
   }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null||"".equals(data)) {
            return;
        } else {
            getData(Integer.parseInt(ordCode));
        }
    }
    /**
     * 取消订单
     * @param order
     */
    private void cancleOrder(final Order order){
        PopupDialog dialog = new PopupDialog(this,"order");
        dialog.initView();
        dialog.setonSelectListener(new PopupDialog.onSelectClickListener() {
            @Override
            public void onSure() {
                submitData(order,"900");
            }
        });
    }
    /**
     * 提交订单
     * @param order
     * @param status
     */
    private void submitData(Order order,String status){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.odrCstUpdUrl);
        builder.put("id",order.getId());
        builder.put("status",status);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                getData(Integer.parseInt(ordCode));
            }
        },this));
    }
    /**
     * 根据状态获取订单
     * @param states
     */
    public void getData(int states){
        if(states == 0) {
            HttpMethods.getInstance().getOrderListData("mktOrderList",Constant.cstId,new ProgressSubscriber<JsonBean>(getOrderOnNext,this));
        }else{
            HttpMethods.getInstance().getOrderListData("mktOrderList",Constant.cstId,states,new ProgressSubscriber<JsonBean>(getOrderOnNext,this));
        }
    }
    private SubscriberOnNextListener getOrderOnNext = new SubscriberOnNextListener<JsonBean>() {
        @Override
        public void onNext(JsonBean jsonBean) {
            list = jsonBean.getOrderList();
            if(list.size() != 0) {
                ll_notorder.setVisibility(View.GONE);
                adapter = new MyOrderAdapter(MyOrderActivity.this,list);
                gv_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.setclickListener(new MyOrderAdapter.onSelectListener() {
                    @Override
                    public void onSelect(int position,int code,String getCode) {
                        Order order = list.get(position);
                        ordCode = getCode;
                        if(code == 900){//取消按钮状态
                            cancleOrder(order);
                        }else{ //接受按钮状态
                            sumbitTyoeData(getCode,order);
                        }
                    }
                });
            }else{
                ll_notorder.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            Intent intent = new Intent(MyOrderActivity.this,HomePagerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
