package com.szxyyd.xyhl.activity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.xyhl.R;
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
/**
 * Created by fq on 2016/8/5.
 */
public class ServiceAddrActivity extends Activity implements View.OnClickListener{
    private ListView lvAddr = null;
    private List<Reladdr> listAddr = null;
    private ServiceLocationAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order_location);
        initView();
        loadAddrListData();
    }
    private void initView(){
        Button btn_back = (Button) findViewById(R.id.btn_back);
        Button btn_add = (Button) findViewById(R.id.btn_add);
        lvAddr = (ListView)findViewById(R.id.lv_order);
        lvAddr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences preferences = getSharedPreferences(Constant.cstId+"defaddr", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name", listAddr.get(position).getName());
                editor.putString("mobile", listAddr.get(position).getMobile());
                editor.putString("addr", listAddr.get(position).getAddr());
                editor.putString("ifdef", listAddr.get(position).getIfdef());
                editor.commit();
                finish();
            }
        });
        btn_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }
    /**
     * 获取服务地址列表
     */
    private void loadAddrListData(){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.locationUrl);
        builder.put("cstid",Constant.cstId);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("reladdr");
                    Type listType = new TypeToken<LinkedList<Reladdr>>() {}.getType();
                    Gson gson = new Gson();
                    listAddr = gson.fromJson(jsonData, listType);
                    showAddressList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }

    /**
     * 显示服务地址列表
     */
    private void showAddressList(){
        if(listAddr.size()!= 0 && listAddr != null){
            adapter = new ServiceLocationAdapter(this, listAddr, new ServiceLocationAdapter.selectOnclickListener() {
                @Override
                public void selectDelect(int position,String type) {
                    int id = listAddr.get(position).getId();
                    Reladdr reladdr = listAddr.get(position);
                    if(type.equals("edit")){  //编辑
                        EditDialog dialog = new EditDialog(ServiceAddrActivity.this,reladdr,"edit",position);
                        dialog.init();
                        dialog.setOnFinshClickListener(new EditDialog.onFinshClickListener() {
                            @Override
                            public void onfinsh() {
                                loadAddrListData();
                            }
                        });
                    }else if(type.equals("checked")){  //修改默认地址
                        delectAddrData(id,"checked");
                    }
                    else{
                        delectAddrData(id,"delect");

                    }
                }
            });
            lvAddr.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    /**
     *删除或者设置默认地址
     */
    private void delectAddrData(int id,String type){
        HttpBuilder builder = new HttpBuilder();
        builder.put("id",id);
        if(type.equals("delect")){
            builder.url(Constant.delAddresUrl);
        }else{
            builder.url(Constant.saveAddressByIdUrl);
            builder.put("cstid",Constant.cstId);
        }
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                   loadAddrListData();
            }
        },this));
    }
    /**
     * 添加服务地址
     */
    private void addSerAddr() {
        EditDialog dialog = new EditDialog(this, null, "add",0);
        dialog.init();
        dialog.setOnFinshClickListener(new EditDialog.onFinshClickListener() {
            @Override
            public void onfinsh() {
                loadAddrListData();
            }
        });
    }
    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_back:
                 finish();
               break;
           case R.id.btn_add:
               addSerAddr();
               break;
       }
    }
}
