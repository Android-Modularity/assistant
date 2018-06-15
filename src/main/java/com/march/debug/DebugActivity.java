package com.march.debug;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.TextUtils;

import com.march.common.utils.ToastUtils;
import com.march.debug.base.BaseDebugActivity;
import com.march.debug.base.BaseDebugFragment;
import com.march.debug.funcs.console.ConsoleFragment;
import com.march.debug.funcs.files.FileFragment;
import com.march.debug.funcs.net.NetFragment;
import com.march.debug.funcs.tools.ToolsFragment;
import com.march.debug.utils.Utils;

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
public class DebugActivity extends BaseDebugActivity {

    public static final String CONSOLE = "控制台";
    public static final String NET     = "网络";
    public static final String FILE    = "文件";
    public static final String TOOL    = "工具";

    private ViewPager         mContentVp;
    private TabLayout         mTitleTabLy;
    private List<String>      mTabList;
    private BaseDebugFragment mDebugFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
        mContentVp = findViewById(R.id.content_pager);
        mTitleTabLy = findViewById(R.id.title_tably);

        mTabList = new ArrayList<>();
        mTabList.add(TOOL);
        mTabList.add(CONSOLE);
        mTabList.add(NET);
        mTabList.add(FILE);

        mDebugFragmentMaker = new BaseDebugFragment.DebugFragmentMaker() {
            @Override
            public BaseDebugFragment make(String title) {
                BaseDebugFragment fragment = null;
                switch (title) {
                    case TOOL:
                        fragment = new ToolsFragment();
                        break;
                    case CONSOLE:
                        fragment = new ConsoleFragment();
                        break;
                    case NET:
                        fragment = new NetFragment();
                        break;
                    case FILE:
                        fragment = new FileFragment();
                        break;
                }
                if (fragment != null) {
                    fragment.setTitle(title);
                }
                return fragment;
            }
        };
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(adapter);
        mContentVp.setOffscreenPageLimit(mTabList.size());
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDebugFragment = adapter.getFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTitleTabLy.setupWithViewPager(mContentVp, true);
    }

    private BaseDebugFragment.DebugFragmentMaker mDebugFragmentMaker;

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private Map<String, BaseDebugFragment> mDebugFragmentMap;

        public BaseDebugFragment getFragment(int pos) {
            String title = mTabList.get(pos);
            BaseDebugFragment fragment = mDebugFragmentMap.get(title);
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

    private long lastTime = -1;

    @Override
    public void onBackPressed() {
        if(mDebugFragment!=null && mDebugFragment.onBackPressed()){
            return;
        }
        long curTime = System.currentTimeMillis();
        if(curTime - lastTime < 1500){
            super.onBackPressed();
        }else {
            lastTime = curTime;
            ToastUtils.show("再按一次退出～");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Utils.SCAN_REQ_CODE == requestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager != null) {
                    CharSequence text = clipboardManager.getText();
                    if (!TextUtils.isEmpty(text)) {
                        Debugger.getInst().getInjector().handleScanResult(this, text);
                        Debugger.getInst().getDataSource().setLastScanResult(text.toString());
                    }
                }
            }
        }
    }
}
