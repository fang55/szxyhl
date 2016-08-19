package com.szxyyd.xyhl.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.activity.Constant;
import com.szxyyd.xyhl.activity.MyActivity;
import com.szxyyd.xyhl.adapter.ServiceLocationAdapter;
import com.szxyyd.xyhl.http.HttpBuilder;
import com.szxyyd.xyhl.http.OkHttp3Utils;
import com.szxyyd.xyhl.http.ProgressCallBack;
import com.szxyyd.xyhl.http.ProgressCallBackListener;
import com.szxyyd.xyhl.modle.Reladdr;
import com.szxyyd.xyhl.view.EditDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ServiceLocationFragment extends Fragment {
	private View rootView;
	private GridView listview;
	private ServiceLocationAdapter adapter;
	private MyActivity mActivity;
	private List<Reladdr> list;
	private AlertDialog alertDialog;
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mActivity = (MyActivity) context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_service_location, container, false);
		initView();
		loadAddrListData("list","0");
		return rootView;
	}
  private void initView(){
	  listview = (GridView) rootView.findViewById(R.id.listview);
	  Button btn_add = (Button) rootView.findViewById(R.id.btn_add);
	  btn_add.setOnClickListener(new View.OnClickListener() {
		  @Override
		  public void onClick(View view) {
			  EditDialog dialog = new EditDialog(getActivity(),null,"add",0);
			  dialog.init();
			  dialog.setOnFinshClickListener(new EditDialog.onFinshClickListener() {
				  @Override
				  public void onfinsh() {
					  loadAddrListData("list","0");
				  }
			  });
		  }
	  });
  }
	private void showCancleDialog(final int position){
		alertDialog = new AlertDialog.Builder(mActivity).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_cancle);
		TextView tv_dialog_title = (TextView) window.findViewById(R.id.tv_dialog_title);
		tv_dialog_title.setText("是否删除地址？");
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
				loadAddrListData("delect", String.valueOf(list.get(position).getId()));
				alertDialog.cancel();
			}
		});
	}
	/**
	 * 获取服务地址列表
	 */
	private void loadAddrListData(final String type,String id){
		HttpBuilder builder = new HttpBuilder();
		if(type.equals("delect")){
			builder.url(Constant.delAddresUrl);
			builder.put("id",id);
		}else if(type.equals("checked")){
			builder.url(Constant.saveAddressByIdUrl);
			builder.put("id",id);
			builder.put("cstid",Constant.cstId);
		}else{
			builder.url(Constant.locationUrl);
			builder.put("cstid",Constant.cstId);
		}
		OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
			@Override
			public void onSuccess(String result) {
				Log.e("ServiceLocationFragment","result=="+result);
				parserData(result,type);
			}
		},mActivity));
	}
	private void parserData(String result,String type){
		try {
			if(result.equals("yes")) {
				if(type.equals("delect")){

				}else{
					loadAddrListData("list","0");
				}
			}else{
				JSONObject json = new JSONObject(result);
				String  jsonData = json.getString("reladdr");
				Type listType = new TypeToken<LinkedList<Reladdr>>(){}.getType();
				Gson gson = new Gson();
				list = gson.fromJson(jsonData, listType);
				if(list.size() != 0 ) {
					adapter = new ServiceLocationAdapter(getActivity(), list, new ServiceLocationAdapter.selectOnclickListener() {
						@Override
						public void selectDelect(int position,String type) {
							Reladdr reladdr = list.get(position);
							if(type.equals("edit")){
								EditDialog dialog = new EditDialog(mActivity,reladdr,"edit",position);
								dialog.init();
								dialog.setOnFinshClickListener(new EditDialog.onFinshClickListener() {
									@Override
									public void onfinsh() {
										loadAddrListData("list","0");
									}
								});
							}else if(type.equals("checked")){
								loadAddrListData("checked",String.valueOf(reladdr.getId()));
							}
							else {
								showCancleDialog(position);
							}
						}
					});
					listview.setAdapter(adapter);
				}else{
					Toast.makeText(mActivity,"暂无数据",Toast.LENGTH_SHORT).show();
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
