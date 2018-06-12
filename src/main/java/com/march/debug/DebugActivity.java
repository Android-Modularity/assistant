package com.march.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.march.debug.funcs.console.ConsoleFragment;
import com.march.debug.funcs.net.NetFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class DebugActivity extends AppCompatActivity {

    public static final String CONSOLE = "控制台";
    public static final String NET     = "网络";

    private ViewPager    mContentVp;
    private TabLayout    mTitleTabLy;
    private List<String> mTabList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
        mContentVp = findViewById(R.id.content_pager);
        mTitleTabLy = findViewById(R.id.title_tably);

        mTabList = new ArrayList<>();
        mTabList.add(CONSOLE);
        mTabList.add(NET);


        mDebugFragmentMaker = new DebugFragment.DebugFragmentMaker() {
            @Override
            public DebugFragment make(String title) {
                DebugFragment fragment = null;
                switch (title) {
                    case CONSOLE:
                        fragment = new ConsoleFragment();
                        break;
                    case NET:
                        fragment = new NetFragment();
                        break;
                }
                if (fragment != null) {
                    fragment.setTitle(title);
                }
                return fragment;
            }
        };
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(adapter);
        mContentVp.setOffscreenPageLimit(mTabList.size());
        mTitleTabLy.setupWithViewPager(mContentVp, true);
    }

    private DebugFragment.DebugFragmentMaker mDebugFragmentMaker;

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private Map<String, DebugFragment> mDebugFragmentMap;

        public DebugFragment getFragment(int pos) {
            String title = mTabList.get(pos);
            DebugFragment fragment = mDebugFragmentMap.get(title);
            if (fragment == null) {
                fragment = mDebugFragmentMaker.make(title);
            }
            mDebugFragmentMap.put(title, fragment);
            return fragment;
        }

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mDebugFragmentMap = new HashMap<>();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getFragment(position).getTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return mTabList.size();
        }
    }

}
