package com.szxyyd.mpxyhl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szxyyd.mpxyhl.R;
import com.szxyyd.mpxyhl.adapter.HomeFragmentPagerAdapter;
import com.szxyyd.mpxyhl.fragment.CommFragment;
import com.szxyyd.mpxyhl.fragment.HomePagerFragment;
import com.szxyyd.mpxyhl.fragment.MessageFragment;
import com.szxyyd.mpxyhl.fragment.MyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * 首頁
 * Created by jq on 2016/7/4.
 */
public class HomePagerActivity extends FragmentActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener{
    private TextView tv_cityName = null;
    private TextView tv_home = null;
    private TextView tv_shequ = null;
    private TextView tv_message = null;
    private TextView tv_my = null;
    private ImageView iv_home = null;
    private ImageView iv_shequ = null;
    private ImageView iv_message = null;
    private ImageView iv_my = null;
    private LinearLayout ll_city = null;
    private ViewPager viewPager = null;
    private List<Fragment> listFragment;
    private HomeFragmentPagerAdapter mAdapter = null;
    private long mExitTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepager);
        initView();
        BaseApplication.getInstance().addActivity(this);
    }
    private void initView(){
        ll_city = (LinearLayout) findViewById(R.id.ll_city);
        tv_cityName = (TextView) findViewById(R.id.tv_cityName);
        LinearLayout ll_home = (LinearLayout) findViewById(R.id.ll_home);
        LinearLayout ll_message = (LinearLayout) findViewById(R.id.ll_message);
        LinearLayout ll_shequ = (LinearLayout) findViewById(R.id.ll_shequ);
        LinearLayout ll_my = (LinearLayout) findViewById(R.id.ll_my);
        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_shequ = (TextView) findViewById(R.id.tv_shequ);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_my = (TextView) findViewById(R.id.tv_my);
        iv_home = (ImageView) findViewById(R.id.iv_home);
        iv_shequ = (ImageView) findViewById(R.id.iv_shequ);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_my = (ImageView) findViewById(R.id.iv_my);
        Button btn_feedback = (Button) findViewById(R.id.btn_feedback);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        listFragment = new ArrayList<Fragment>();
        listFragment.add(new HomePagerFragment());
        listFragment.add(new MessageFragment());
        listFragment.add(new CommFragment());
        listFragment.add(new MyFragment());
        mAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(),listFragment);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
        changeTextColor(tv_home,tv_shequ,tv_message,tv_my);
        changeImageBg(R.mipmap.def_home_sel,R.mipmap.sy_faxian,R.mipmap.sy_shequ,R.mipmap.def_my);
        viewPager.setOnPageChangeListener(this);
        ll_home.setOnClickListener(this);
        ll_shequ.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_my.setOnClickListener(this);
        ll_city.setOnClickListener(this);
        btn_feedback.setOnClickListener(this);
    }
    /**
     * 点击改变字体颜色
     */
    private void changeTextColor(TextView tv1,TextView tv2,TextView tv3,TextView tv4){
        tv1.setTextColor(this.getResources().getColor(R.color.color_bule));
        tv2.setTextColor(this.getResources().getColor(R.color.color_six_nine));
        tv3.setTextColor(this.getResources().getColor(R.color.color_six_nine));
        tv4.setTextColor(this.getResources().getColor(R.color.color_six_nine));
    }
    /**
     * 点击图标背景
     */
    private void changeImageBg(int iv1,int iv2,int iv3,int iv4){
        iv_home.setBackgroundResource(iv1);
        iv_message.setBackgroundResource(iv2);
        iv_shequ.setBackgroundResource(iv3);
        iv_my.setBackgroundResource(iv4);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null||"".equals(data)) {
            return;
        } else {
            String city = data.getStringExtra("cityname");
            tv_cityName.setText(city);
            Constant.cityName = city;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    @Override
    public void onPageSelected(int position) {
        //当viewPager滑动的时候
        int currentPage = viewPager.getCurrentItem();
        switch (currentPage) {
            case Constant.PAGE_ONE:
                changeTextColor(tv_home,tv_shequ,tv_message,tv_my);
                changeImageBg(R.mipmap.def_home_sel,R.mipmap.sy_faxian,R.mipmap.sy_shequ,R.mipmap.def_my);
                break;
            case Constant.PAGE_TWO:
              /*  isSelectHome = Constant.PAGE_TWO;
                changeTextColor(tv_message,tv_shequ,tv_home,tv_my);
                changeImageBg(R.mipmap.def_home,R.mipmap.sy_faxian_true,R.mipmap.sy_shequ,R.mipmap.def_my);*/
                break;
            case Constant.PAGE_THREE:
              /*  isSelectHome = Constant.PAGE_THREE;
                changeTextColor(tv_shequ,tv_home,tv_message,tv_my);
                changeImageBg(R.mipmap.def_home,R.mipmap.sy_faxian,R.mipmap.sy_shequ_true,R.mipmap.def_my);*/
                break;
            case Constant.PAGE_FOUR:
                changeTextColor(tv_my,tv_shequ,tv_message,tv_home);
                changeImageBg(R.mipmap.def_home,R.mipmap.sy_faxian,R.mipmap.sy_shequ,R.mipmap.def_my_sel);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
    }
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_city:
                startActivityForResult(new Intent(this, CtiyActivity.class),  1);
                break;
            case R.id.btn_feedback:
                Intent intentAdvice = new Intent(this,AdviceFeedbackActivity.class);
                startActivity(intentAdvice);
                break;
            case R.id.ll_home:
                viewPager.setCurrentItem(Constant.PAGE_ONE);
                break;
            case R.id.ll_message:
            //    viewPager.setCurrentItem(Constant.PAGE_TWO);
                break;
            case R.id.ll_shequ:
           //     viewPager.setCurrentItem(Constant.PAGE_THREE);
                break;
            case R.id.ll_my:
                viewPager.setCurrentItem(Constant.PAGE_FOUR);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - mExitTime) > 2000){
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                BaseApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
