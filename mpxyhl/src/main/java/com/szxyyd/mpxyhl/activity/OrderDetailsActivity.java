package com.szxyyd.mpxyhl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.fragment.MyOrderFragment;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.modle.Order;
import com.szxyyd.mpxyhl.utils.CommUtils;
import com.szxyyd.mpxyhl.utils.PicassoUtils;

/**
 * 订单详情
 * Created by fq on 2016/7/7.
 */
public class OrderDetailsActivity extends BaseActivity implements View.OnClickListener{
    private Button btn_go = null;
    private Button btn_cancle  = null;
    private RelativeLayout rl_addr  = null;
    private ImageView iv_theach  = null;
    private Order order  = null;
    private int orderStates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails);
        order = (Order) getIntent().getSerializableExtra("order");
        initView();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        rl_addr = (RelativeLayout) findViewById(R.id.rl_addr);
        iv_theach = (ImageView) findViewById(R.id.iv_theach);
        ImageView iv_next = (ImageView)findViewById(R.id.iv_next);
        iv_next.setVisibility(View.GONE);
        rl_addr.setVisibility(View.VISIBLE);
        PicassoUtils.loadImageViewRoundTransform(this,Constant.nurseImage + order.getIcon(),150,170,R.mipmap.teach,iv_theach);
        btn_back.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        initData();
    }
    private void initData(){
        if(order != null) {
            ((TextView) findViewById(R.id.tv_addr_name)).setText(order.getNursename());//地址姓名
            ((TextView) findViewById(R.id.tv_addr_phone)).setText(order.getMobile());//地址号码
            ((TextView) findViewById(R.id.tv_addr)).setText(order.getAddr());//地址
            ((TextView) findViewById(R.id.tv_order_id)).setText("订单编号：" + order.getId() + "");//订单编号
            ((TextView) findViewById(R.id.tv_combo)).setText(order.getOrdname());//宝宝洗护师A套餐服务
            ((TextView) findViewById(R.id.tv_states)).setText(order.getCodename()); //状态
            ((TextView) findViewById(R.id.tv_name)).setText(order.getNursename()); //预约护理师：tv_mas
            ((TextView) findViewById(R.id.tv_mas)).setText(order.getNote()); //备注
            ((TextView) findViewById(R.id.tv_servicedate)).setText(CommUtils.showData(order.getAtarrival()));//服务日期：
            ((TextView) findViewById(R.id.tv_startdate)).setText(CommUtils.showData(order.getAtarrival()));//下单日期
            ((TextView) findViewById(R.id.tv_money)).setText(CommUtils.subStr(order.getCstpaysum()));//价格
            changeBtnStates(order.getStatus());
        }
    }
    /**
     * 根据状态
     * @param states
     */
    private void changeBtnStates(String states){
        if(states.equals("200")){ //待响应
            btn_cancle.setText("取消订单");
            btn_go.setVisibility(View.GONE);
            orderStates = 900;
        }else if(states.equals("400")){ //待服务
            btn_go.setText("开始服务");
            btn_cancle.setVisibility(View.GONE);
            orderStates = 800;
        }else if(states.equals("800")){ //完成服务
            btn_go.setText("完成服务");
            btn_cancle.setVisibility(View.GONE);
            orderStates = 1100;
        }else{
            btn_go.setVisibility(View.GONE);
            btn_cancle.setVisibility(View.GONE);
        }
    }
    private void submitData(int state){
        Log.e("OrserDetsilsActivity","status==="+state);
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.odrCstUpdUrl);
        builder.put("id",order.getId());
        builder.put("status",state);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(BaseApplication.getInstance(),"已提交",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderDetailsActivity.this,MyOrderFragment.class);
                setResult(3, intent);
                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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
            case R.id.btn_go:
                submitData(orderStates);
                break;
            case R.id.btn_cancle:
                submitData(orderStates);
                break;
        }
    }
}
