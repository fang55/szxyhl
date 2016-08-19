package com.szxyyd.mpxyhl.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jq on 2016/7/8.
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private List<Fragment> listFragments;
    public HomeFragmentPagerAdapter(FragmentManager fm,List<Fragment> al) {
        super(fm);
        listFragments = al;
    }
    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }
    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

}
