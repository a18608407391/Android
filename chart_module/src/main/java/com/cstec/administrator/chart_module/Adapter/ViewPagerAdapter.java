package com.cstec.administrator.chart_module.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmList;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragmList = fragments;
    }

    @Override
    public Fragment getItem(int index) {
        return mFragmList.get(index);
    }

    @Override
    public int getCount() {
        return mFragmList.size();
    }
}