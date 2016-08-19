package com.szxyyd.mpxyhl.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.http.HttpBuilder;
import com.szxyyd.mpxyhl.http.OkHttp3Utils;
import com.szxyyd.mpxyhl.http.ProgressCallBack;
import com.szxyyd.mpxyhl.http.ProgressCallBackListener;
import com.szxyyd.mpxyhl.modle.ImageItem;
import com.szxyyd.mpxyhl.utils.FileUtils;
import com.szxyyd.mpxyhl.view.WrhImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * 截取圆形头像
 * Created by fq on 2016/8/18.
 */
public class ClipHeaderActivity extends BaseActivity{
    private WrhImageView img_clip;
    private Button btn_clipSure;
    private ImageItem imageItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipheader);
        imageItem = (ImageItem) getIntent().getSerializableExtra("bitmap");
        initView();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("移动与缩放");
        Button btn_back = (Button) findViewById(R.id.btn_back);
        img_clip = (WrhImageView) findViewById(R.id.img_clip);
        btn_clipSure = (Button) findViewById(R.id.btn_clipSure);
        Bitmap bitmap = imageItem.getBitmap();
        img_clip.setImageBitmap(bitmap);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        btn_clipSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bm = img_clip.clipBitmap();
                FileUtils.saveBitmap(bm, "clipheader");
                String imagePath = FileUtils.getImagePath("clipheader");
                Log.e("ClipHeaderActivity","imagePath=="+imagePath);
                submitHeaderData(imagePath);
            }
        });
    }
    /**
     * 上传头像
     */
    private void submitHeaderData(String imagePath){
        Log.e("ClipHeaderActivity","Constant.usrId=="+Constant.usrId);
      //  String imagePath = FileUtils.getImagePath("clipheader");
        Map<String,String> map = new HashMap<>();
        map.put("id",Constant.usrId);
        OkHttp3Utils.getInstance().callAsynImageData(Constant.iconUpdUrl,map,imagePath,new ProgressCallBack(new ProgressCallBackListener() {
            @Override
            public void onSuccess(String data) {
                Log.e("ClipHeaderActivity","submitHeaderData---data=="+data);
                Toast.makeText(ClipHeaderActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        },this));
    }
}
