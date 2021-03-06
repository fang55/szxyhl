package com.szxyyd.mpxyhl.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.ServiceAddressAdapter;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.HttpMethods;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.inter.SubscriberOnNextListener;
import com.szxyyd.mpxyhl.modle.JsonBean;
import com.szxyyd.mpxyhl.modle.ProgressSubscriber;
import com.szxyyd.mpxyhl.modle.Reladdr;
import com.szxyyd.mpxyhl.view.PopupDialog;
import java.io.StringReader;
import java.util.List;

/**
 * 服务地址
 * Created by fq on 2016/7/6.
 */
public class ServiceAddressActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_add  = null;
    private GridView gv_address  = null;
    private ServiceAddressAdapter addressAdapter  = null;
    private List<Reladdr> listAddr;
    private Reladdr reladdr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceaddress);
        initView();
    }
    private void  initView(){
        gv_address = (GridView)findViewById(R.id.gv_address);
        ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.my_address));
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setVisibility(View.VISIBLE);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        gv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences preferences = getSharedPreferences(Constant.cstId+"defaddr", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name", listAddr.get(position).getName());
                editor.putString("mobile", listAddr.get(position).getMobile());
                editor.putString("addr", listAddr.get(position).getAddr());
                editor.commit();
                finish();
            }
        });
        tv_add.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAddrListData();
    }
    SubscriberOnNextListener getAddrListOnNext = new SubscriberOnNextListener<JsonBean>() {
        @Override
        public void onNext(JsonBean jsonBean) {
            listAddr = jsonBean.getReladdr();
            addressAdapter = new ServiceAddressAdapter(ServiceAddressActivity.this,listAddr);
            addressAdapter.setSelectListener(new ServiceAddressAdapter.selectOnclickListener() {
                @Override
                public void selectDelect(int position, String type) {
                    reladdr = listAddr.get(position);
                    if(type.equals("edit")){  //编辑
                        Intent intent = new Intent(ServiceAddressActivity.this,EditAddressActivity.class);
                        intent.putExtra("type","edit");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("reladdr",reladdr);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }else if(type.equals("delect")){  //删除
                        showDelectDialog();
                    }else{
                        delectAddrData("checked");

                    }
                }
            });
            gv_address.setAdapter(addressAdapter);
        }
    };
    /**
     * 获取服务地址列表
     */
    private void loadAddrListData(){
       HttpMethods.getInstance().getAddressListData("addressList",Constant.cstId,new ProgressSubscriber<JsonBean>(getAddrListOnNext,this));
    }

    /**
     * 弹出询问是否删除地址
     */
    private void showDelectDialog(){
        PopupDialog dialog = new PopupDialog(ServiceAddressActivity.this,"addr");
        dialog.setonSelectListener(new PopupDialog.onSelectClickListener() {
            @Override
            public void onSure() {
                delectAddrData("delect");
            }
        });
        dialog.initView();
    }
    private SubscriberOnNextListener getResultOnNext = new SubscriberOnNextListener<String>() {
        @Override
        public void onNext(String result) {
            Toast.makeText(ServiceAddressActivity.this,"onNext==",Toast.LENGTH_SHORT).show();
            JsonReader jsonReader = new JsonReader(new StringReader(result));
            jsonReader.setLenient(true);
            Toast.makeText(ServiceAddressActivity.this,"result=="+result,Toast.LENGTH_SHORT).show();
            loadAddrListData();
        }
    };
    /**
     * delect：删除
     * checked：设置默认地址
     */
    private void delectAddrData(final String type){
        int id = reladdr.getId();
        if(type.equals("delect")){
            HttpMethods.getInstance().submitDelAddrData("delAddres",String.valueOf(id),new ProgressSubscriber<String>(getResultOnNext,this));
        }else if(type.equals("checked")){
            HttpBuilder builder = new HttpBuilder();
            builder.url(Constant.saveAddressByIdUrl);
            builder.put("id",id);
            builder.put("cstid",Constant.cstId);
            OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
                @Override
                public void onSuccess(String data) {
                    Log.e("EditAddressActivity", "checked--data==" + data);
                    if(type.equals("checked")){
                        SharedPreferences preferences = getSharedPreferences(Constant.cstId+"defaddr", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("name", reladdr.getName());
                        editor.putString("mobile", reladdr.getMobile());
                        editor.putString("addr", reladdr.getAddr());
                        editor.commit();
                    }
                        loadAddrListData();
                }
            },this));

        }
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_back:
               finish();
               overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
               break;
           case R.id.tv_add:
               Intent intent = new Intent(ServiceAddressActivity.this,EditAddressActivity.class);
               intent.putExtra("type","add");
               Bundle bundle = new Bundle();
               bundle.putSerializable("reladdr",reladdr);
               intent.putExtras(bundle);
               startActivity(intent);
               overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
               break;
       }
    }
}
