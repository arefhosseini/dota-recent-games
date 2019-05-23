package com.fearefull.dotaanalyser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterDetail extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterDetail(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabFragmentDetailOverview();
            case 1:
                return new TabFragmentDetailFarm();
            case 2:
                return new TabFragmentDetailDamage();
            case 3:
                return new TabFragmentDetailItems();
            case 4:
                return new TabFragmentDetailBuild();
            case 5:
                return new TabFragmentDetailGraphs();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
