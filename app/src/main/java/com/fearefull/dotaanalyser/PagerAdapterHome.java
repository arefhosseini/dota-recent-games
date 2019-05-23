package com.fearefull.dotaanalyser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterHome extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterHome(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabFragmentHomeOverview();
            case 1:
                return new TabFragmentHomeHistory();
            case 2:
                return new TabFragmentHomeRecords();
            case 3:
                return new TabFragmentHomeTeammates();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
