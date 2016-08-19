package com.szxyyd.mpxyhls.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.szxyyd.mpxyhls.R;
import com.szxyyd.mpxyhls.http.HttpBuilder;
import com.szxyyd.mpxyhls.http.OkHttp3Utils;
import com.szxyyd.mpxyhls.http.ProgressCallBack;
import com.szxyyd.mpxyhls.http.ProgressCallBackListener;

/**
 * 图片预览
 * Created by fq on 2016/8/16.
 */
public class ImagePreviewActivity extends Activity{
    private ImageView iv_preview;
    private String type = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        type = getIntent().getStringExtra("type");
        initView();
        lodeImageData();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("图片预览");
        TextView tv_submit = (TextView) findViewById(R.id.tv_submit);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 获取证件的图片
     */
    private void lodeImageData(){
        HttpBuilder builder = new HttpBuilder();
        builder.url(Constant.nurFilesUrl);
        builder.put("nurseid ",type);
        OkHttp3Utils.getInstance().callAsyn(builder,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {

            }
        },this));
    }
}
