package com.szxyyd.xyhl.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.szxyyd.xyhl.R;
import com.szxyyd.xyhl.utils.Bimp;
import com.szxyyd.xyhl.utils.PicassoUtils;
import com.szxyyd.xyhl.zoo.PhotoView;
import com.szxyyd.xyhl.zoo.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用于进行图片浏览时的界面
 * Created by fq on 2016/8/11.
 */
public class GalleryActivity extends Activity {
    private Intent intent;
    // 返回按钮
    private Button back_bt;
    //获取前一个activity传过来的position
    private int position;
    //当前的位置
    private int location = 0;
    //图片的路径
    private String imagePath = null;
    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;
    public List<Bitmap> bmp = new ArrayList<Bitmap>();
    private String type = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        type = intent.getStringExtra("type");
        initView();
    }
    private void initView(){
        back_bt = (Button) findViewById(R.id.gallery_back);
        back_bt.setOnClickListener(new BackListener());
        pager = (ViewPagerFixed) findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(pageChangeListener);
        if(type.equals("comment")){
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                initListViews( Bimp.tempSelectBitmap.get(i).getBitmap() );
            }
        }else if(type.equals("detail")){
            initDetailImage();
        }
        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setPageMargin(10);
        int id =  intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);
    }

    /**
     * 显示护理师生活照
     */
    private void initDetailImage(){
        if (listViews == null)
            listViews = new ArrayList<View>();
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            String imagePath = Bimp.tempSelectBitmap.get(i).imagePath;
            Log.e("GalleryActivity","imagePath=="+imagePath);
            ImageView imageView = new ImageView(this);
            PicassoUtils.loadImageViewHolder(this,imagePath,200,200,R.drawable.teach,imageView);
            listViews.add(imageView);
        }
    }
    private void initListViews(Bitmap bm) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(0xff000000);
        img.setImageBitmap(bm);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }
    // 返回按钮添加的监听器
    private class BackListener implements View.OnClickListener {
        public void onClick(View v) {
           finish();
        }
    }
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };
    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;
        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }
        public void finishUpdate(View arg0) {
        }
        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
