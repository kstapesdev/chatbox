package com.scatterform.chatabox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    public FragmentAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public int getCount(){
        return 3;
    }

    @Override
    public Fragment getItem(int position){

        Fragment page = null;

        switch (position) {
            case 0: page = ChatMessageFragment.newInstance("One", "Two"); break;
            case 1: page = HistoryFragment.newInstance(1);break;
            case 2: page = MembersFragment.newInstance(1);break;

            default: page = ChatMessageFragment.newInstance("One", "Two"); break;
        }

        //Fragment page = ChatMessageFragment.newInstance("One","Two");
        return page;
    }

    public CharSequence getPageTitle(int position){
        CharSequence result = "";

        switch (position) {
            case 0: result = "Chat"; break;
            case 1: result = "History"; break;
            case 2: result = "Members"; break;

            default: result = "Chat"; break;
        }
        return result;
    }
}


