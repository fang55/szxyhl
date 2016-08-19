package com.szxyyd.mpxyhls.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.szxyyd.mpxyhls.R;
import com.szxyyd.mpxyhls.adapter.HomeFragmentPagerAdapter;

/**
 * Created by jq on 2016/7/12.
 */
public class HomePagerActivity extends FragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {
    private TextView tv_title;
    private TextView tv_home;
    private TextView tv_shequ;
    private TextView tv_find;
    private TextView tv_my;
    private ImageView iv_home;
    private ImageView iv_shequ;
    private ImageView iv_faxian;
    private ImageView iv_my;
    private ViewPager viewPager;
    private HomeFragmentPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepager);
        initView();
        BaseApplication.getInstance().addActivity(this);
    }
    private void initView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_shequ = (TextView) findViewById(R.id.tv_shequ);
        tv_find = (TextView) findViewById(R.id.tv_faxian);
        tv_my = (TextView) findViewById(R.id.tv_my);
        iv_home = (ImageView) findViewById(R.id.iv_home);
        iv_shequ = (ImageView) findViewById(R.id.iv_shequ);
        iv_faxian = (ImageView) findViewById(R.id.iv_faxian);
        iv_my = (ImageView) findViewById(R.id.iv_my);
        LinearLayout ll_home = (LinearLayout) findViewById(R.id.ll_home);
        LinearLayout ll_shequ = (LinearLayout) findViewById(R.id.ll_shequ);
        LinearLayout ll_faxian = (LinearLayout) findViewById(R.id.ll_faxian);
        LinearLayout ll_my = (LinearLayout) findViewById(R.id.ll_my);
        LinearLayout ll_city = (LinearLayout) findViewById(R.id.ll_city);
        ll_city.setVisibility(View.VISIBLE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setVisibility(View.GONE);
        mAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(Constant.PAGE_ONE);
        viewPager.setOnPageChangeListener(this);
        changeTextColor(tv_home, tv_shequ,tv_find ,tv_my);
        changeImageBg(R.mipmap.def_home_sel,R.mipmap.sy_shequ,R.mipmap.sy_faxian,R.mipmap.def_my);
        tv_title.setText("心悦护理师");
        ll_home.setOnClickListener(this);
        /*ll_shequ.setOnClickListener(this);
        ll_faxian.setOnClickListener(this);*/
        ll_my.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.ll_home:
                 viewPager.setCurrentItem(Constant.PAGE_ONE);
                 break;
             case R.id.ll_shequ:
                 viewPager.setCurrentItem(Constant.PAGE_TWO);
                 break;
             case R.id.ll_faxian:
                 viewPager.setCurrentItem(Constant.PAGE_THREE);
                 break;
             case R.id.ll_my:
                 viewPager.setCurrentItem(Constant.PAGE_FOUR);
                 break;
         }
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
        iv_shequ.setBackgroundResource(iv2);
        iv_faxian.setBackgroundResource(iv3);
        iv_my.setBackgroundResource(iv4);
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    @Override
    public void onPageSelected(int i) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (viewPager.getCurrentItem()) {
                case Constant.PAGE_ONE:
                    tv_title.setText("心悦护理师");
                    changeTextColor(tv_home, tv_shequ,tv_find ,tv_my);
                    changeImageBg(R.mipmap.def_home_sel,R.mipmap.sy_shequ,R.mipmap.sy_faxian,R.mipmap.def_my);
                    break;
              /*  case Constant.PAGE_TWO:
                    tv_title.setText("社区");
                    changeTextColor(tv_shequ, tv_home, tv_find,tv_my);
                    changeImageBg(R.mipmap.def_home,R.mipmap.sy_shequ_true,R.mipmap.sy_faxian,R.mipmap.def_my);
                    break;
                case Constant.PAGE_THREE:
                    tv_title.setText("发现");
                    changeTextColor(tv_find,tv_home, tv_shequ, tv_my);
                    changeImageBg(R.mipmap.def_home,R.mipmap.sy_shequ,R.mipmap.sy_faxian_true,R.mipmap.def_my);
                    break;*/
                case Constant.PAGE_FOUR:
                    tv_title.setText("个人中心");
                    changeTextColor(tv_my,tv_home, tv_shequ, tv_find);
                    changeImageBg(R.mipmap.def_home,R.mipmap.sy_shequ,R.mipmap.sy_faxian,R.mipmap.def_my_sel);
                    break;
            }
        }
    }

}
