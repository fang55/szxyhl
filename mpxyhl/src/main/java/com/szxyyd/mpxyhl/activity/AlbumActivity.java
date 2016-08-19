package com.szxyyd.mpxyhl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.AlbumGridViewAdapter;
import com.szxyyd.mpxyhl.modle.ImageItem;
import com.szxyyd.mpxyhl.utils.AlbumHelper;
import com.szxyyd.mpxyhl.utils.Bimp;
import com.szxyyd.mpxyhl.utils.ImageBucket;

import java.util.ArrayList;
import java.util.List;


/**
 * 这个是进入相册显示所有图片的界面
 * Created by jq on 2016/7/16.
 */
public class AlbumActivity extends BaseActivity{
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    //gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    //完成按钮
    private Button okButton;
    // 返回按钮
    private Button back;
    private Intent intent;
    private ArrayList<ImageItem> dataList;
    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    public static Bitmap bitmap;
    private String type = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_album);
        type = getIntent().getStringExtra("type");
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_unfocused);
        init();
        initListener();
        //这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
        BaseApplication.getInstance().addActivity(this);
    }
    // 完成按钮的监听
    private class AlbumSendListener implements View.OnClickListener {
        public void onClick(View v) {
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            finish();
        }
    }
    // 返回按钮监听
    private class BackListener implements View.OnClickListener {
        public void onClick(View v) {
            Bimp.tempSelectBitmap.clear();
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            finish();
        }
    }
    // 初始化，给一些对象赋值
    private void init() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for(int i = 0; i<contentList.size(); i++){
            dataList.addAll(contentList.get(i).imageList);
        }

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new BackListener());
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList, Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
                + "/"+ Constant.num+")");
    }
    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked,Button chooseBt) {
                        if (Bimp.tempSelectBitmap.size() >= Constant.num) {
                            toggleButton.setChecked(false);
                            chooseBt.setVisibility(View.GONE);
                            if (!removeOneData(dataList.get(position))) {
                                Toast.makeText(AlbumActivity.this, getString(R.string.only_choose_num), Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        if (isChecked) {
                            chooseBt.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            if(type.equals("comment")){
                                okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
                                        + "/"+Constant.num+")");
                            }else{  //进入截取圆形头像界面
                               Intent intent = new Intent(AlbumActivity.this,ClipHeaderActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("bitmap",dataList.get(position));
                                intent.putExtras(bundle);
                              startActivity(intent);
                                finish();
                            }
                        } else {
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            chooseBt.setVisibility(View.GONE);
                            okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+Constant.num+")");
                        }
                        isShowOkBt();
                    }
                });
        okButton.setOnClickListener(new AlbumSendListener());
    }

    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            okButton.setText(getString(R.string.finish)+"(" +Bimp.tempSelectBitmap.size() + "/"+Constant.num+")");
            return true;
        }
        return false;
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+Constant.num+")");
            okButton.setPressed(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
        } else {
            okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+Constant.num+")");
            okButton.setPressed(false);
            okButton.setClickable(false);
            okButton.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }
}
