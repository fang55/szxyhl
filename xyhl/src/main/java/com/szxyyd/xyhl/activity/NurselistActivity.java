package com.szxyyd.xyhl.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.adapter.NurseAdapter;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.http.VolleyRequestUtil;
import com.szxyyd.xyhl.inf.VolleyListenerInterface;
import com.szxyyd.xyhl.modle.City;
import com.szxyyd.xyhl.modle.NurseList;
import com.szxyyd.xyhl.modle.PriceLvl;
import com.szxyyd.xyhl.view.BasePopupWindow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 护理师列表
 * @author fq
 *
 */
public class NurselistActivity extends Activity implements OnClickListener{
	private TextView tv_title;
//	private LinearLayout ll1;  //级别
	private LinearLayout ll2; //年龄
	private LinearLayout ll3;   //籍贯
	private LinearLayout ll4; //排序
//	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private  Button btn_back;
	private GridView gv_nurse;
	private NurseAdapter adapter;
	private String[] data1 ; //级别信息
	private String[] dataTag1 = new String[]{"1000","2000","3000"};
	private String[] data2 = new String[]{"20岁以下","20~29","30~39","40以上"};
	private String[] dataTag2 = new String[]{"1","2","3","4"};
	private List<String> cityName;   //储存城市的名字
	private String[] dataTag3;   //储存城市的编号
	private String[] data4 = new String[]{"服务次数","服务评分","距离"};
	private String[] dataTag4 = new String[]{"1","2","3"};
	private List<NurseList> list;
	private ProgressDialog proDialog;
	private DbUtils dbUtils;
	private Map<String, String> params = new HashMap<>();
	private String addressName;
	private String cstgislng;
	private String cstgislat;
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
				case Constant.SUCCEED:
                    List<City> listCity = (List<City>) msg.obj;
					Log.e("NurselistActivity", "listCity.size()=="+listCity.size());
					if(listCity.size() != 0){
						cityName = new ArrayList<>();
						dataTag3 = new String[listCity.size()];
                       for(int i = 0; i < listCity.size(); i++){
						   cityName.add(listCity.get(i).getName());
						   dataTag3[i] = listCity.get(i).getIid();
					   }
					}
					break;
			case Constant.LIST:
				list = (List<NurseList>) msg.obj;
				Log.e("NurselistActivity", "list.size()=="+list.size());
				if(list.size() != 0 && list != null){
						adapter = new NurseAdapter(NurselistActivity.this,list);
						gv_nurse.setAdapter(adapter);
						adapter.notifyDataSetChanged();
				}else{
					Toast.makeText(NurselistActivity.this,"暂无数据",Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nurselis);
		initView();
		initEvent();
		dbUtils = BaseApplication.getdbUtils();
		lodeData(0,null);
		lodeOrigoData();
		getLevelData();
		BaseApplication.getInstance().addActivity(this);
	}
  private void initView(){
	  tv_title = (TextView) findViewById(R.id.tv_title);
	  if(Constant.cityName != null){
		  tv_title.setText(Constant.cityName);
	  }
	  LinearLayout ll_topright = (LinearLayout) findViewById(R.id.ll_topright);
	  ll_topright.setVisibility(View.GONE);
	  gv_nurse = (GridView) findViewById(R.id.gv_nurse);
	  tv2 = (TextView) findViewById(R.id.tv2);
	  tv3 = (TextView) findViewById(R.id.tv3);
	  tv4 = (TextView) findViewById(R.id.tv4);
	  ll2 = (LinearLayout) findViewById(R.id.ll2);
	  ll3 = (LinearLayout) findViewById(R.id.ll3);
	  ll4 = (LinearLayout) findViewById(R.id.ll4);
	  btn_back = (Button) findViewById(R.id.btn_back);
	  gv_nurse.setOnItemClickListener(new OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
			  NurseList nurse = list.get(position);
			  Intent intent = new Intent(NurselistActivity.this,NurseDetailsActivity.class);
			  Bundle bundle = new Bundle();
			  bundle.putSerializable("nurse", nurse);
			  intent.putExtras(bundle);
			  startActivity(intent);
		  }
	  });
  }
  private void initEvent(){
	  ll2.setOnClickListener(this);
	  ll3.setOnClickListener(this);
	  ll4.setOnClickListener(this);
	  btn_back.setOnClickListener(this);
  }

	/**
	 * 获取级别数据
	 */
	private void getLevelData(){
		try {
			List<PriceLvl> list = dbUtils.findAll(PriceLvl.class);
			data1 = new String[list.size()];
			if(null != list) {
				for (int i = 0; i < list.size(); i++) {
					data1[i] = list.get(i).getName();
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取籍贯
	 */
	private void lodeOrigoData(){
		String url = Constant.findOrigoUrl;
		VolleyRequestUtil volley = new VolleyRequestUtil();
		volley.RequestGet(this, url, "origo",
				new VolleyListenerInterface(this,VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
					@Override
					public void onSuccess(String result) {
						Log.e("NurselistActivity", "lodeOrigoData---result=="+result);
						parserData(result, "origo");
					}
					@Override
					public void onError(VolleyError error) {

					}
				});
	}
	private void lodeData(int index,Map<String, String> param){
		//nur?a=listbycst&city(城市)&lvl（级别）&sex（性别）married（婚后）
		/*String url = null;
		Constant.cityId = "440300";
		if(index == 0){ //加载全部
			url = Constant.nurseListUrl+"&city="+Constant.cityId+"&svrid="+Constant.svrId+
					"&lvl="+Constant.lvlId+"&cstgislng="+"113.23333"+"&cstgislat="+"23.16667";
			params.put("&lvl=",Constant.lvlId);
		}else if(index == 1){
			StringBuffer sb = new StringBuffer();
			for (String key : param.keySet()) {
				Log.e("NurseliActivyr","key= "+ key + "and value=" + param.get(key));
				sb.append(key).append(param.get(key));
			}
			String result = sb.toString();
			Log.e("NurseliActivyr","key="+ result);
			Log.e("NurseliActivyr","Constant.cityId==="+ Constant.cityId);
			Log.e("NurseliActivyr","Constant.svrId==="+ Constant.svrId);
			url = Constant.nurseListUrl+"&city="+Constant.cityId
					+ "&svrid="+Constant.svrId +"&cstgislng="+"113.23333"+"&cstgislat="+"23.16667"+ result;
		}
		Log.e("NurseliActivyr","url==="+ url);*/
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.nurseListUrl);
		builder.put("svrid",Constant.svrId);
		if(index == 0) { //加载全部
			builder.put("city",Constant.cityId);
			builder.put("lvl",Constant.lvlId);
			builder.put("cstgislng","113.23333");
			builder.put("cstgislat","23.16667");
			params.put("&lvl=",Constant.lvlId);
		}else{
			for (String key : param.keySet()) {
				Log.e("NurseliActivyr","key= "+ key + "and value=" + param.get(key));
				builder.put(key,param.get(key));
			}
			builder.put("cstgislng","113.23333");
			builder.put("cstgislat","23.16667");
		}
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String jsonData = json.getString("nurse");
					Type listType = new TypeToken<LinkedList<NurseList>>(){}.getType();
					Gson gson = new Gson();
					List<NurseList> list = gson.fromJson(jsonData, listType);
					if(list.size() != 0 && list != null){
						adapter = new NurseAdapter(NurselistActivity.this,list);
						gv_nurse.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}else{
						Toast.makeText(NurselistActivity.this,"暂无数据",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		},this));

	/*	proDialog = ProgressDialog.show(NurselistActivity.this, "", "加载中");
		VolleyRequestUtil volley = new VolleyRequestUtil();
		volley.RequestGet(this, url, "nurselist",
				new VolleyListenerInterface(this,VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
					@Override
					public void onSuccess(String result) {
						Log.e("NurselistActivity", "result=="+result);
						if(proDialog != null){
							proDialog.cancel();
						}
						parserData(result,"list");
					}
					@Override
					public void onError(VolleyError error) {

					}
				});*/
	}
	/**
	 * 解析返回来的数据
	 * @param result
	 */
	private void parserData(String result,String type){
		try {
			JSONObject json = new JSONObject(result);
			String  jsonData = null;
			if(type.equals("origo")){
				jsonData = json.getString("origo");
				Type listCity = new TypeToken<LinkedList<City>>(){}.getType();
				Gson gson = new Gson();
				List<City> list = gson.fromJson(jsonData, listCity);
				Message message = new Message();
				message.what = Constant.SUCCEED;
				message.obj = list;
				handler.sendMessage(message);
			}else{
				jsonData = json.getString("nurse");
				Type listType = new TypeToken<LinkedList<NurseList>>(){}.getType();
				Gson gson = new Gson();
				List<NurseList> list = gson.fromJson(jsonData, listType);
				Message message = new Message();
				message.what = Constant.LIST;
				message.obj = list;
				handler.sendMessage(message);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void changeTextContent(int index, String result,String tag){
		switch (index){
			case 2:
				tv2.setText(result);
				if(result != null){
					params.put("&age=",tag);
				}
				break;
			case 3:
				tv3.setText(result);
				if(result != null){
					params.put("&origo=",tag);
				}
				break;
			case 4:
				tv4.setText(result);
				if(result != null){
					params.put("&sort=",tag);
				}
				break;
		}
		lodeData(1,params);
	}
	/**
	 * 显示距离弹出框
	 * @param rootView
	 */
	private void showDistance(View rootView,final int index){
		BasePopupWindow popupWindow = new BasePopupWindow(this);
		popupWindow.setOnPopuItemClickListener(new BasePopupWindow.OnPopuItemClickListener() {
			@Override
			public void onItemClick(int pos, String result,String tag) {
				Log.e("NurselistActivity","--showDistance==index=="+index);
				Log.e("NurselistActivity","--showDistance==tag=="+tag);
				Log.e("NurselistActivity","--showDistance==result=="+result);
				changeTextContent(index,result,tag);
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		List<String> data = new ArrayList<>();
		switch (index){
			case 1:
				for(int i = 0;i<data1.length;i++){
					data.add(data1[i].toString());
				}
				popupWindow.showRootView(rootView,data,dataTag1);
				break;
			case 2:
				for(int i = 0;i<data2.length;i++){
					data.add(data2[i].toString());
				}
				popupWindow.showRootView(rootView,data,dataTag2);
				break;
			case 3:
					popupWindow.showRootView(rootView, cityName, dataTag3);
				break;
			case 4:
				for(int i = 0;i<data4.length;i++){
					data.add(data4[i].toString());
				}
				popupWindow.showRootView(rootView,data,dataTag4);
				break;
		}
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.ll2:
				showDistance(ll2,2);
				break;
			case R.id.ll3:
				if(cityName == null  ) {
					Toast.makeText(NurselistActivity.this,"无数据",Toast.LENGTH_SHORT).show();
					return;
				}else{
					showDistance(ll3,3);
				}
				break;
			case R.id.ll4:
				showDistance(ll4,4);
				break;
			default:
				break;
		}
	}

}
