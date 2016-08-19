package com.szxyyd.mpxyhls.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.szxyyd.mpxyhls.R;
import com.szxyyd.mpxyhls.adapter.IncomeAdapter;
import com.szxyyd.mpxyhls.http.HttpBuilder;
import com.szxyyd.mpxyhls.http.OkHttp3Utils;
import com.szxyyd.mpxyhls.http.ProgressCallBack;
import com.szxyyd.mpxyhls.http.ProgressCallBackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 我的收益
 * Created by fq on 2016/8/9.
 */
public class IncomeActivity extends Activity {
    private GridView gv_income;
    private IncomeAdapter adapter;
    private Map<String,String> mapData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        initView();
        lodeIncomeData();
        BaseApplication.getInstance().addActivity(this);
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的收益");
        gv_income = (GridView) findViewById(R.id.gv_income);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }
    private void lodeIncomeData(){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.getNursePriceUrl);
        builder.put("nurseid",Constant.nurId);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String price = json.getString("price");
                    String orderNum = json.getString("orderNum");
                    mapData = new HashMap<String, String>();
                    mapData.put("price",price);
                    mapData.put("orderNum",orderNum);
                    adapter = new IncomeAdapter(IncomeActivity.this,mapData);
                    gv_income.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }
}
