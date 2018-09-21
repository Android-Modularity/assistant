package com.march.assistant;

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

import com.march.assistant.adapter.FragmentMakeAdapter;
import com.march.assistant.base.BaseAssistantActivity;
import com.march.assistant.base.BaseAssistantFragment;
import com.march.assistant.funcs.console.ConsoleFragment;
import com.march.assistant.funcs.files.FileFragment;
import com.march.assistant.funcs.net.NetFragment;
import com.march.assistant.funcs.tools.ToolsFragment;
import com.march.assistant.utils.Utils;
import com.march.common.extensions.BarUI;

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
public class AssistantActivity extends BaseAssistantActivity {

    public static final String CONSOLE = "控制台";
    public static final String NET     = "网络";
    public static final String FILE    = "文件";
    public static final String TOOL    = "工具";

    private ViewPager             mContentVp;
    private TabLayout             mTitleTabLy;

    private FragmentMakeAdapterWrap mFragmentMakeAdapterWrap;
    private BaseAssistantFragment mCurFragment;

    class FragmentMakeAdapterWrap implements FragmentMakeAdapter {

        private  List<String> titles;

        public FragmentMakeAdapterWrap() {
            if (Assistant.getInst().getInitCfg().fragmentMakeAdapter == null) {
                titles = new ArrayList<>();
            } else {
                titles = new ArrayList<>(Assistant.getInst().getInitCfg().fragmentMakeAdapter.getTitles());
            }
            titles = new ArrayList<>();
            titles.add(TOOL);
            titles.add(CONSOLE);
            titles.add(NET);
            titles.add(FILE);
        }

        @Override
        public List<String> getTitles() {
            return titles;
        }

        @Override
        public BaseAssistantFragment makeFragment(String title) {
            BaseAssistantFragment fragment = null;
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
            if (fragment == null && Assistant.getInst().getInitCfg().fragmentMakeAdapter != null) {
                fragment = Assistant.getInst().getInitCfg().fragmentMakeAdapter.makeFragment(title);
            }
            if (fragment != null) {
                fragment.setTitle(title);
            }
            return fragment;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assistant_activity);
        BarUI.setStatusBarLightMode(this);
        mFragmentMakeAdapterWrap = new FragmentMakeAdapterWrap();
        mContentVp = findViewById(R.id.content_pager);
        mTitleTabLy = findViewById(R.id.title_tably);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(adapter);
        mContentVp.setOffscreenPageLimit(mFragmentMakeAdapterWrap.titles.size());
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurFragment = adapter.getFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTitleTabLy.setupWithViewPager(mContentVp, true);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private Map<String, BaseAssistantFragment> mDebugFragmentMap;

        public BaseAssistantFragment getFragment(int pos) {
            String title = mFragmentMakeAdapterWrap.titles.get(pos);
            BaseAssistantFragment fragment = mDebugFragmentMap.get(title);
            if (fragment == null) {
                fragment = mFragmentMakeAdapterWrap.makeFragment(title);
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
            return mFragmentMakeAdapterWrap.titles.size();
        }
    }


    @Override
    public void onBackPressed() {
        if(mCurFragment!=null && mCurFragment.onBackPressed()){
            return;
        }
        super.onBackPressed();
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
                        Assistant.getInst().getInitCfg().injectAdapter.handleScanResult(this, text);
                        Assistant.getInst().getDataSource().setLastScanResult(text.toString());
                    }
                }
            }
        }
    }
}
