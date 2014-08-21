package com.ruenzuo.babel.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ruenzuo.babel.fragments.GuessOptionsFragment;
import com.ruenzuo.babel.fragments.SourceCodeFragment;
import com.ruenzuo.babel.models.enums.BabelFragmentType;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class BabelFragmentPagerAdapter extends FragmentPagerAdapter {

    public BabelFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BabelFragmentType babelFragmentType = BabelFragmentType.values()[position];
        Fragment fragment;
        switch (babelFragmentType) {
            case BABEL_FRAGMENT_TYPE_SOURCE_CODE: {
                fragment = new SourceCodeFragment();
                break;
            }
            case BABEL_FRAGMENT_TYPE_GUESS_OPTIONS: {
                fragment = new GuessOptionsFragment();
                break;
            }
            default: {
                fragment = null;
                break;
            }
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return BabelFragmentType.values().length;
    }

}
