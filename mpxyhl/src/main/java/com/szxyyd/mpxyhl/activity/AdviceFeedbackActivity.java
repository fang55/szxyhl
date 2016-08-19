package com.szxyyd.mpxyhl.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;

/**
 * 意见反馈
 * Created by jq on 2016/7/21.
 */
public class AdviceFeedbackActivity extends BaseActivity {
    private EditText et_question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);
        initView();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("反馈");
        TextView tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setVisibility(View.VISIBLE);
        tv_add.setText("提交");
        Button btn_back = (Button) findViewById(R.id.btn_back);
        et_question = (EditText) findViewById(R.id.et_question);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAdviceData();
            }
        });
    }
private void submitAdviceData(){
    String question = et_question.getText().toString().trim();
    if(question.length() == 0){
        Toast.makeText(this,"请填写问题描述",Toast.LENGTH_SHORT).show();
        return;
    }
    HttpBuilder builder = new HttpBuilder();
    builder.url(Constant.opnAddUrl);
    builder.put("uid",Constant.usrId);
    builder.put("opn",question);
    OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
        @Override
        public void onSuccess(String data) {
            Toast.makeText(AdviceFeedbackActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
        }
    },this));
}

}
