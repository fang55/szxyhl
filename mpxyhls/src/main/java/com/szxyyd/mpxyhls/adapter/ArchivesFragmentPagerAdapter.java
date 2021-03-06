package com.szxyyd.mpxyhls.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.szxyyd.mpxyhls.activity.Constant;
import com.szxyyd.mpxyhls.fragment.BasicMessageFragment;
import com.szxyyd.mpxyhls.fragment.TrainCertificateFragment;
import com.szxyyd.mpxyhls.fragment.VideoIntroductionFragment;
import com.szxyyd.mpxyhls.fragment.WorkMessageFragment;

/**
 * Created by jq on 2016/7/8.
 */
public class ArchivesFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private Fragment homeFragment = null;
    private Fragment commFragment = null;
    private Fragment messageFragment = null;
    private Fragment myFragment = null;
    public ArchivesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        homeFragment = new BasicMessageFragment();
        commFragment = new WorkMessageFragment();
        messageFragment = new TrainCertificateFragment();
        myFragment = new VideoIntroductionFragment();
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case Constant.PAGE_ONE:
                fragment = homeFragment;
                break;
            case Constant.PAGE_TWO:
                fragment = commFragment;
                break;
            case Constant.PAGE_THREE:
                fragment = messageFragment;
                break;
            case Constant.PAGE_FOUR:
                fragment = myFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

}
