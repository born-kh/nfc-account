package com.example.account.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public  RdFragment rdFragment = new RdFragment();
    public   PxFragment  pxFragment = new PxFragment();
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return rdFragment;
            case 1:
                return pxFragment;

            default:
                return null;
        }
    }

//    public void addFrag(PxFragment frag_out, String outbound) {
//        public void addFrag(android.support.v4.app.Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//
//        }
//    }
}