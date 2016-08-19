package com.szxyyd.mpxyhls.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.exception.DbException;
import com.szxyyd.mpxyhls.R;
import com.szxyyd.mpxyhls.activity.BaseApplication;
import com.szxyyd.mpxyhls.activity.Constant;
import com.szxyyd.mpxyhls.activity.HomePagerActivity;
import com.szxyyd.mpxyhls.activity.OrderActivity;
import com.szxyyd.mpxyhls.adapter.ImagePaperAdapter;
import com.szxyyd.mpxyhls.http.BitmapCache;
import com.szxyyd.mpxyhls.modle.Nurse;
import com.szxyyd.mpxyhls.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jq on 2016/7/14.
 */
public class HomePagerFragment extends Fragment{
    private View rootView;
    private HomePagerActivity mActivity;
    private selectInterface mSelectInterface;
    private ViewPager mviewPager = null;
    private LinearLayout dotLayout =null;
    private int currentItem  = 0;//当前页面
    private boolean isAutoPlay = true;//是否自动轮播
    private ScheduledExecutorService scheduledExecutorService;
    /**用于小圆点图片*/
    private List<ImageView> dotViewList;
    /**用于存放轮播效果图片*/
    private List<ImageView> list;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 100){
                mviewPager.setCurrentItem(currentItem);
            }
        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomePagerActivity) context;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_homepager,container,false);
        initView();
        initData();
        return rootView;
    }
    private void initView(){
        mviewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        dotLayout = (LinearLayout)rootView.findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();
        initImageView();
        if(isAutoPlay){
            startPlay();
        }
    }

    private void initData(){
        LinearLayout ll_orderCurrent = (LinearLayout) rootView.findViewById(R.id.ll_orderCurrent);
        ll_orderCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),OrderActivity.class);
                startActivity(intent);
            }
        });
    }
    public void initImageView(){
        dotViewList = new ArrayList<ImageView>();
        list = new ArrayList<ImageView>();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);
        for (int i = 0; i < 3; i++) {
            ImageView dotView = new ImageView(mActivity);
            if(i == 0){
                dotView.setBackgroundResource(R.mipmap.group_select);
            }else{
                dotView.setBackgroundResource(R.mipmap.group_not);
            }
            dotLayout.addView(dotView, lp);
            dotViewList.add(dotView);
            //上面是动态添加了四个小圆点
        }
        int[] imageViewIds = new int[] { R.mipmap.vager1,R.mipmap.vager2,R.mipmap.vager3};
        for (int i = 0; i < 3; i++) {
            View imageVage = LayoutInflater.from(mActivity).inflate(R.layout.scroll_vew_item,null);
            ImageView imageViews = (ImageView) imageVage.findViewById(R.id.imger_vager);
            imageViews.setImageResource(imageViewIds[i]);
            list.add(imageViews);
        }
        ImagePaperAdapter adapter = new ImagePaperAdapter((ArrayList)list);
        mviewPager.setAdapter(adapter);
        mviewPager.setCurrentItem(0);
        mviewPager.setOnPageChangeListener(new MyPageChangeListener());
    }
    /**
     * 开始轮播图切换
     */
    public void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 2, 4, TimeUnit.SECONDS);
        //根据他的参数说明，第一个参数是执行的任务，第二个参数是第一次执行的间隔，第三个参数是执行任务的周期；
    }
    /**
     *执行轮播图切换任务
     *
     */
    private class SlideShowTask implements Runnable{
        @Override
        public void run() {
            synchronized (mviewPager) {
                currentItem = (currentItem+1)%list.size();
                handler.sendEmptyMessage(100);
            }
        }
    }
    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        boolean isAutoPlay = false;
        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    System.out.println(" 手势滑动，空闲中");
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    System.out.println(" 界面切换中");
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (mviewPager.getCurrentItem() == mviewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        mviewPager.setCurrentItem(0);
                        System.out.println(" 滑动到最后一张");
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (mviewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        mviewPager.setCurrentItem(mviewPager.getAdapter().getCount() - 1);
                        System.out.println(" 滑动到第一张");
                    }
                    break;
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageSelected(int pos) {
            //这里面动态改变小圆点的被背景，来实现效果
            currentItem = pos;
            for(int i=0;i < dotViewList.size();i++){
                if(i == pos){
                    ((View)dotViewList.get(pos)).setBackgroundResource(R.mipmap.group_select);
                }else {
                    ((View)dotViewList.get(i)).setBackgroundResource(R.mipmap.group_not);
                }
            }
        }
    }
    public interface selectInterface{
        public void onSelect(String type);
    }
    @Override
    public void onPause() {
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
        }
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
        }
        super.onStop();
    }
}
