package com.ruenzuo.babel.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.adapters.BabelFragmentPagerAdapter;
import com.ruenzuo.babel.extensions.AnimatedActivity;
import com.ruenzuo.babel.managers.BabelManager;
import com.ruenzuo.babel.models.enums.BabelFragmentType;
import com.ruenzuo.babel.models.enums.DifficultyType;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class BabelActivity extends AnimatedActivity {

    @InjectView(R.id.vwPager)
    ViewPager vwPager;

    private BabelManager babelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.babel_activity_layout);
        ButterKnife.inject(this);
        vwPager.setAdapter(new BabelFragmentPagerAdapter(getSupportFragmentManager()));
        vwPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (BabelFragmentType type : BabelFragmentType.values()) {
            getActionBar().addTab(getActionBar().newTab().setText(type.toPrint()).setTabListener(tabListener));
        }
        String token = getIntent().getStringExtra("Token");
        DifficultyType difficultyType = (DifficultyType) getIntent().getSerializableExtra("DifficultyType");
        setupManager(difficultyType, token);
    }

    private void setupManager(DifficultyType difficultyType, String token) {
        babelManager = new BabelManager(difficultyType, token);
        babelManager.setupQueue(this);
    }

    private ActionBar.TabListener tabListener = new ActionBar.TabListener() {

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            vwPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

    };

}
