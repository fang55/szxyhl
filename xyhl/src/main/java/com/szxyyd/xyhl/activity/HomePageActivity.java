package com.szxyyd.xyhl.activity;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.modle.NurseType;

/**
 * 首页
 * @author fq
 * 
 */
public class HomePageActivity extends Activity implements OnClickListener {
	private LinearLayout ll_city = null;
	private TextView tv_city = null;
	private TextView tv_app = null;
	private Button btn_home = null;
	private Button btn_comm = null;
	private Button btn_message = null;
	private Button btn_my = null;
	private List<NurseType> list = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepager);
		initView();
		initEvent();
		lodeData();
		BaseApplication.getInstance().addActivity(this);
	}
	private void initView() {
		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_app = ((TextView)findViewById(R.id.tv_app));
		btn_home = (Button) findViewById(R.id.btn_home);
		btn_comm = (Button) findViewById(R.id.btn_comm);
		btn_message = (Button) findViewById(R.id.btn_message);
		btn_my = (Button) findViewById(R.id.btn_my);
		tv_city.setVisibility(View.VISIBLE);
		ll_city = (LinearLayout) findViewById(R.id.ll_city);
		ImageView iv_nurse = (ImageView) findViewById(R.id.iv_nurse);
		ImageView iv_repair = (ImageView) findViewById(R.id.iv_repair);
		ImageView iv_lactation = (ImageView) findViewById(R.id.iv_lactation);
		ImageView iv_clean = (ImageView) findViewById(R.id.iv_clean);
		ImageView iv_baby = (ImageView) findViewById(R.id.iv_baby);
		ImageView iv_mom = (ImageView) findViewById(R.id.iv_mom);
		iv_nurse.setOnClickListener(new actionClickListener(0));
		iv_repair.setOnClickListener(new actionClickListener(3));
		iv_lactation.setOnClickListener(new actionClickListener(4));
		iv_clean.setOnClickListener(new actionClickListener(5));
		iv_baby.setOnClickListener(new actionClickListener(1));
		iv_mom.setOnClickListener(new actionClickListener(2));
	}

	class actionClickListener implements OnClickListener{
		private int index;
		public actionClickListener (int index){
			this.index = index;
		}
		@Override
		public void onClick(View view) {
			if(list.size() != 0) {
				int svrid = list.get(index).getId();
				Constant.svrId = svrid;
				Intent intent = new Intent(HomePageActivity.this, HealthNurseActivity.class);
				intent.putExtra("svrid", svrid);
				intent.putExtra("title", list.get(index).getName());
				startActivity(intent);
			  }
			}
	}
	private void initEvent() {
		ll_city.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		btn_comm.setOnClickListener(this);
		btn_message.setOnClickListener(this);
		btn_my.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_home:
			break;
		case R.id.btn_comm:
			Intent intent = new Intent(this,CommunityActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_message:
			Intent intent2 = new Intent(this,MessageActivity.class);
			startActivity(intent2);
			break;
		case R.id.btn_my:
			Intent intent3 = new Intent(this,MyActivity.class);
			startActivity(intent3);
			finish();
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.ll_city:
			startActivityForResult(new Intent(this, CityActivity.class),  1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null||"".equals(data)) {
			return;
		} else {
			String city = data.getStringExtra("cityname");
			tv_city.setText(city);
			Constant.cityName = city;
		}
	}
	private void getWidthHeigh(){
		 DisplayMetrics dm = new DisplayMetrics();
		 //获取屏幕信息
		         getWindowManager().getDefaultDisplay().getMetrics(dm);
		         Constant.screenWidth = dm.widthPixels;
		         Constant.screenHeigh = dm.heightPixels;
		         Log.e("getWidthHeigh()", "screenWidth=="+Constant.screenWidth);
		         Log.e("getWidthHeigh()", "screenHeigh=="+Constant.screenHeigh);
	}

	/**
	 * 加载数据
	 */

	private void lodeData(){
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.homeUrl);
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				Log.e("HomePageActivity", "result=="+result);
				try {
					JSONObject json = new JSONObject(result);
					String  jsonData = json.getString("svr");
					Type listType = new TypeToken<LinkedList<NurseType>>() {}.getType();
					Gson gson = new Gson();
					list = gson.fromJson(jsonData, listType);
					if(list.size() != 0) {
						tv_app.setText("心悦护理");
						/*((TextView) findViewById(R.id.tv_mom)).setText(list.get(0).getName());
						((TextView)findViewById(R.id.tv_repair)).setText(list.get(3).getName());
						((TextView)findViewById(R.id.tv_lactation)).setText(list.get(4).getName());
						((TextView)findViewById(R.id.tv_clean)).setText(list.get(5).getName());
						((TextView)findViewById(R.id.tv_nurse)).setText(list.get(1).getName());
						((TextView)findViewById(R.id.tv_baby)).setText(list.get(2).getName());*/
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		},this));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			BaseApplication.getInstance().exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
