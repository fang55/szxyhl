package com.szxyyd.mpxyhl.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.EvaluateAdapter;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.modle.NurseList;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 评价
 * Created by jq on 2016/7/29.
 */
public class EvaluateActivity extends BaseActivity{
    private ListView lv_evaluate;
    private  EvaluateAdapter adapter;
    private NurseList nurse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        nurse = (NurseList) getIntent().getSerializableExtra("nurse");
        initView();
        lodeEvaluateData();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText("评价");
        Button btn_back = (Button) findViewById(R.id.btn_back);
        lv_evaluate = (ListView) findViewById(R.id.lv_evaluate);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    /**
     * 获取护理师评论列表
     */
    private void lodeEvaluateData(){
         HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.nurseCmtAllUrl);
        builder.put("nurseid",nurse.getNursvrid());
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject json = new JSONObject(data);
                    String jsonData = json.getString("nurse");
                    Type priceLvlType = new TypeToken<LinkedList<NurseList>>() {}.getType();
                    Gson gson = new Gson();
                    List<NurseList> nurseLists = gson.fromJson(jsonData, priceLvlType);
                    if(nurseLists.size() > 0){
                        adapter = new EvaluateAdapter(EvaluateActivity.this,nurseLists);
                        lv_evaluate.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this));
    }
}
