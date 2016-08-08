package com.szxyyd.xyhl.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.activity.Constant;
import com.szxyyd.xyhl.activity.MyActivity;
import com.szxyyd.xyhl.adapter.CollectAdapter;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.modle.NurseList;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 我的收藏
 * @author jq
 *
 */
public class CollectFragment extends Fragment{
	private View rootView;
	private GridView gridview;
	private CollectAdapter adapter;
	private MyActivity mActivity;
	private List<NurseList> listNurse;
	private AlertDialog alertDialog;
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mActivity = (MyActivity) context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_collect, container, false);
		initView();
		lodeCollectData();
		return rootView;
	}
   private void initView(){
	   gridview = (GridView) rootView.findViewById(R.id.gridview);
   }
	private void showCancleDialog(final String id,final int position){
		alertDialog = new AlertDialog.Builder(mActivity).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_cancle);
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
				submitCancleData(id,position);
			}
		});
	}
	/**
	 * 获取收藏列表
	 * cstId 用户id
	 *
	 */
	private void lodeCollectData(){
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.cstFvrnurListUrl);
		builder.put("cstid",Constant.cstId);
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject json = new JSONObject(result);
					String jsonData =  json.getString("nurse");
					Type listType = new TypeToken<LinkedList<NurseList>>(){}.getType();
					Gson gson = new Gson();
					listNurse = gson.fromJson(jsonData, listType);
					if(listNurse.size() != 0 ) {
						adapter = new CollectAdapter(getActivity(), listNurse);
						gridview.setAdapter(adapter);
						adapter.setCancleListener(new CollectAdapter.clickCancleListener() {
							@Override
							public void onSelect(int position, NurseList nurse) {
								Toast.makeText(getActivity(), "position==" + position, Toast.LENGTH_SHORT).show();
								String id = nurse.getId();
								showCancleDialog(id,position);
							}
						});
					}else{
						Toast.makeText(mActivity,"暂无数据",Toast.LENGTH_SHORT).show();
						if(adapter != null){
							adapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		},mActivity));
	}

	/**
	 * 提交取消收藏
	 * collectId 收藏id
	 */
	private void submitCancleData(String collectId,final int position){
		HttpBuilder builder = new HttpBuilder();
		builder.url(Constant.cstFvrnurDelUrl);
		builder.put("id",collectId);
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String data) {
				listNurse.remove(position);
				adapter.notifyDataSetChanged();
				gridview.setAdapter(adapter);
				alertDialog.cancel();
			}
		},mActivity));
	}
}
