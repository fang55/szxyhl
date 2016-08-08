package com.szxyyd.mpxyhls.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.mpxyhls.R;
import com.szxyyd.mpxyhls.activity.Constant;
import com.szxyyd.mpxyhls.adapter.OrderAdapter;
import com.szxyyd.mpxyhls.http.HttpBuilder;
import com.szxyyd.mpxyhls.http.OkHttp3Utils;
import com.szxyyd.mpxyhls.http.ProgressCallBack;
import com.szxyyd.mpxyhls.http.ProgressCallBackListener;
import com.szxyyd.mpxyhls.modle.OrderList;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
/**
 * 订单
 * Created by jq on 2016/7/13.
 */
public class OrderFragment extends Fragment{
    private View rootView ;
    private RadioGroup rgChannel;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private LinearLayout ll_notorder;
    private GridView gv_order;
    private OrderAdapter adapter;
    private List<OrderList> listData;
    private int ordCode;
    private int ORDER_STATES ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_order,container,false);
        initView();
        return rootView;
    }
    private void initView(){
        ll_notorder = (LinearLayout) rootView.findViewById(R.id.ll_notorder);
        gv_order = (GridView) rootView.findViewById(R.id.gv_order);
        rgChannel = (RadioGroup) rootView.findViewById(R.id.rgChannel);
        rb1 = (RadioButton) rootView.findViewById(R.id.rb1);
        rb2 = (RadioButton) rootView.findViewById(R.id.rb2);
        rb3 = (RadioButton) rootView.findViewById(R.id.rb3);
        rb4 = (RadioButton) rootView.findViewById(R.id.rb4);
        rgChannel.setOnCheckedChangeListener(new rgOnCheckedChangeListener());
        rb2.setChecked(true);

    }
    class rgOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.rb1:
                    ORDER_STATES = 0;
                    changeTextColor(rb1,rb2,rb3,rb4);
                    break;
                case R.id.rb2:
                    ORDER_STATES = 200;
                    changeTextColor(rb2,rb1,rb3,rb4);
                    break;
                case R.id.rb3:
                    ORDER_STATES = 400;
                    changeTextColor(rb3,rb2,rb1,rb4);
                    break;
                case R.id.rb4:
                    ORDER_STATES = 800;
                    changeTextColor(rb4,rb2,rb3,rb1);
                    break;
            }
            lodeOrderData(ORDER_STATES);
        }
    }
    private void changeTextColor(RadioButton tv1,RadioButton tv2,RadioButton tv3,RadioButton tv4){
        tv1.setTextColor(getResources().getColor(R.color.color_bule));
        tv2.setTextColor(getResources().getColor(R.color.color_six_six));
        tv3.setTextColor(getResources().getColor(R.color.color_six_six));
        tv4.setTextColor(getResources().getColor(R.color.color_six_six));
    }
    /**
     * 加载数据
     */
    private void lodeOrderData(int index){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.orderListUrl);
        builder.put("nurseid",Constant.nurId);
        if(index != 0){
            builder.put("status",index);
        }
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener(){
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String jsonData = json.getString("orderList");
                    Gson gson = new Gson();
                    Type OrderType = new TypeToken<LinkedList<OrderList>>() {}.getType();
                    listData = gson.fromJson(jsonData, OrderType);
                    if(listData.size() != 0 && listData != null) {
                        ll_notorder.setVisibility(View.GONE);
                        adapter = new OrderAdapter(getActivity(), listData);
                    }else {
                        listData.clear();
                        adapter = new OrderAdapter(getActivity(), listData);
                        ll_notorder.setVisibility(View.VISIBLE);
                        //    Toast.makeText(getActivity(),"暂无数据",Toast.LENGTH_SHORT).show();
                    }
                    gv_order.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setclickListener(new OrderAdapter.onSelectListener() {
                        @Override
                        public void onSelect(int position, int code) {
                            OrderList orderList = listData.get(position);
                            ordCode = Integer.parseInt(orderList.getStatus());
                            //   Log.e("MyOrderActivity","ordCode=="+ordCode);
                            submitRefuseData(orderList,code);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },getActivity()));
    }
    /**
     * 提交
     */
    private void submitRefuseData(OrderList orderList,int code){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.odrespUpdUrl);
        builder.put("odrid",orderList.getId());
        builder.put("nurseid",orderList.getNurseid());
        builder.put("status",code);
        builder.put("note",orderList.getNote());
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String result) {
                if(ordCode == 0){
                    ordCode = 0;
                }
                Log.e("OrderFragment", "handler---ordCode=="+ordCode);
                lodeOrderData(ordCode);
                adapter.notifyDataSetChanged();
                gv_order.setAdapter(adapter);
            }
        },getActivity()));
    }
}
