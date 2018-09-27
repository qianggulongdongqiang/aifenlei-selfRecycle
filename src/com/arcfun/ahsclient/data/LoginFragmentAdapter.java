package com.arcfun.ahsclient.data;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LoginFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private String[] tabTitles;

    public LoginFragmentAdapter(FragmentManager fm, List<Fragment> fragments,
            String[] tabTitles) {
        super(fm);
        this.fragments = fragments;
        this.tabTitles = tabTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int pos) {
        return fragments.get(pos);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}