package com.szxyyd.xyhl.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.activity.BaseApplication;
import com.szxyyd.xyhl.activity.ConfirmOrderActivity;
import com.szxyyd.xyhl.activity.Constant;
import com.szxyyd.xyhl.activity.DetailsActivity;
import com.szxyyd.xyhl.activity.MyActivity;
import com.szxyyd.xyhl.activity.OrderCommentActivity;
import com.szxyyd.xyhl.adapter.OrderAdapter;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.modle.Order;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 订单界面
 * @author fq
 */
public class OrderFragment extends Fragment {
	private View rootView;
	private GridView listview;
	private RadioGroup rgChannel = null;
	public RadioButton rb1 = null;
	private RadioButton rb2 = null;
	private RadioButton rb3 = null;
	private RadioButton rb4 = null;
	private RadioButton rb5 = null;
	private RadioButton rb6 = null;
	private OrderAdapter adapter;
	private MyActivity mActivity;
	private AlertDialog alertDialog;
	private List<Order> orderList;
	private int ordPosition = 0;
	private int ordCode = 0;
	private int ORDER_STATES = 0;
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mActivity = (MyActivity) context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_order, container, false);
		initView();
		return rootView;
	}
	private void initView() {
		rgChannel=(RadioGroup)rootView.findViewById(R.id.rgChannel);
		rb1 = (RadioButton) rootView.findViewById(R.id.rb1);
		rb2 = (RadioButton)rootView.findViewById(R.id.rb2);
		rb3 = (RadioButton) rootView.findViewById(R.id.rb3);
		rb4 = (RadioButton) rootView.findViewById(R.id.rb4);
		rb5 = (RadioButton) rootView.findViewById(R.id.rb5);
		rb6 = (RadioButton) rootView.findViewById(R.id.rb6);
		rgChannel.setOnCheckedChangeListener(onChecked);
		rb2.setChecked(true);
		listview = (GridView) rootView.findViewById(R.id.listview);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Order order = orderList.get(position);
				String states = order.getStatus();
				ordCode = Integer.valueOf(states);
				ordPosition = position;
				if(states.equals("300")){  //待支付
					showPayDialog(order);
				}else if(states.equals("1100")){
					showCommentDialog(order);
				}else{
					showDetailsDialog(order);
				}
			}
		});
	}
private RadioGroup.OnCheckedChangeListener onChecked = new RadioGroup.OnCheckedChangeListener() {
	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
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
		getOrderData(ORDER_STATES);
	}
};
	/**
	 * 评价界面
	 * @param order
     */
	private void showCommentDialog(Order order){
		Intent intent = new Intent(mActivity, OrderCommentActivity.class);
		intent.putExtra("order",order);
		startActivityForResult(intent,  1);
	}
	/**
	 * 订单详情界面
	 * @param order
     */
	public void showDetailsDialog(Order order){
		Intent intent = new Intent(mActivity, DetailsActivity.class);
		intent.putExtra("order",order);
		startActivityForResult(intent,  3);
	}
	/**
	 * 订单支付界面
	 * @param order
     */
	public void showPayDialog(Order order){
		Intent intent = new Intent(mActivity,ConfirmOrderActivity.class);
		intent.putExtra("order",order);
		startActivityForResult(intent,  2);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 if (data == null||"".equals(data)) {
			return;
		 }  if(requestCode == 1){
			ordCode = 1100;
			getOrderData(ordCode );
		}else if (requestCode == 2) {
				ordCode = 300;
				getOrderData(ordCode);
			} else if (requestCode == 3) {
					int states = data.getIntExtra("orderStates", 0);
					String getCode = data.getStringExtra("getCode");
					submitData(ordPosition, states, getCode);
			}
	}
	private void changeTextColor(RadioButton tv1,RadioButton tv2,RadioButton tv3,RadioButton tv4,RadioButton tv5,RadioButton tv6){
		tv1.setTextColor(mActivity.getResources().getColor(R.color.tv_login));
		tv2.setTextColor(Color.BLACK);
		tv3.setTextColor(Color.BLACK);
		tv4.setTextColor(Color.BLACK);
		tv5.setTextColor(Color.BLACK);
		tv6.setTextColor(Color.BLACK);
	}
	private void delectOrder(){
		alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_cancle);
		TextView tv_dialog_title = (TextView) window.findViewById(R.id.tv_dialog_title);
		tv_dialog_title.setText("确定取消订单？");
		TextView tv_dialog_cancle = (TextView) window.findViewById(R.id.tv_dialog_cancle);
		tv_dialog_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.cancel();
			}
		});
		TextView tv_dialog_sure = (TextView) window.findViewById(R.id.tv_dialog_sure);
		tv_dialog_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				submitData(ordPosition,900,String.valueOf(ordCode));
				alertDialog.cancel();
			}
		});
	}

	/**
	 * 根据状态获取订单数据
	 * @param states
     */
	private void getOrderData(int states){
		if(Constant.cstId == null){
			return;
		}
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.mktOrderListUrl);
		if(states == 0) {
			builder.put("cstid",Constant.cstId);
		}else{
			builder.put("cstid",Constant.cstId);
			builder.put("status",states);
		}
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				parserData(result);
			}
		},getActivity()));
	}

	/**
	 * 根据状态提交订单数据
     */
	private void submitData(int position,int state,String getCode){
		Order order =  orderList.get(position);
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.odrCstUpdUrl);
		builder.put("id",order.getId());
		if(getCode.equals("800")){
			builder.put("status","1100");
		}else{
			builder.put("status",state);
		}
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				getOrderData(ordCode);
			}
		},getActivity()));

	}
	private void parserData(String result) {
		try {
				JSONObject json = new JSONObject(result);
				String jsonData = json.getString("orderList");
				if("[]".equals(jsonData)){
					orderList = new ArrayList<Order>();
					orderList.clear();
					adapter = new OrderAdapter(mActivity, orderList);
					listview.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					Toast.makeText(BaseApplication.getInstance(),"暂无数据",Toast.LENGTH_SHORT).show();
				}else{
					Type listType = new TypeToken<LinkedList<Order>>(){}.getType();
					Gson gson = new Gson();
					orderList = gson.fromJson(jsonData, listType);
					orderList();
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	  }

	private void orderList(){
		Log.e("OrderFragment","orderList.size()==="+orderList.size());
		if(orderList.size() != 0 ){
			adapter = new OrderAdapter(mActivity, orderList);
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			adapter.setclickListener(new OrderAdapter.onSelectListener() {
				@Override
				public void onSelect(int position,int code,String getCode) {
					if(code == 900){//取消
						ordCode = Integer.valueOf(getCode);
						if(getCode.equals("1100")){ //待评价
							showCommentDialog(orderList.get(position));
						}else{
							delectOrder();
							ordPosition = position;
						}
					}else{
						ordCode = Integer.valueOf(getCode);
						if(getCode.equals("300")){
							showPayDialog(orderList.get(position));
						}else{
							submitData(position,code,getCode);
						}
					}
				}
			});
		}
	}

}
