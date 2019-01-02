package com.march.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import com.march.assistant.base.BaseAssistActivity;
import com.march.assistant.base.BaseAssistFragment;
import com.march.assistant.model.AssistTab;
import com.march.assistant.utils.AssistantUtils;
import com.march.common.exts.ClipboardX;
import com.march.common.exts.EmptyX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe : 测试界面入口
 *
 * @author chendong
 */
public class EntryActivity extends BaseAssistActivity {

    private BaseAssistFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assistant_activity);
        ViewPager contentVp = findViewById(R.id.content_pager);
        TabLayout titleTabLy = findViewById(R.id.title_tably);

        List<AssistTab> tabs = Assistant.assist().opts().getTabs();
        Collections.sort(tabs, (o1, o2) -> o2.id - o1.id);

        contentVp.setOffscreenPageLimit(tabs.size());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabs);
        contentVp.setAdapter(adapter);
        contentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mFragment = (BaseAssistFragment) adapter.getItem(position);
                Assistant.assist().info().tabIndex = position;
                Assistant.assist().flush();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        titleTabLy.setupWithViewPager(contentVp, true);
        int tabIndex = Assistant.assist().info().tabIndex;
        if (tabIndex < 0 || tabIndex >= tabs.size()) {
            tabIndex = 0;
            Assistant.assist().info().tabIndex = tabIndex;
            Assistant.assist().flush();
        }
        TabLayout.Tab tabAt = titleTabLy.getTabAt(tabIndex);
        if (tabAt != null) {
            tabAt.select();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AssistantUtils.SCAN_REQ_CODE == requestCode) {
            CharSequence text = ClipboardX.pasteText(this);
            if (!EmptyX.isEmpty(text)) {
                IAssistant assist = Assistant.assist();
                assist.opts().getScanResultCallback().onScanResult(this, text);
                assist.info().scanResult = text;
                assist.flush();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null && mFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<AssistTab>                 mAssistTabs;
        private SparseArray<BaseAssistFragment> mArray;

        ViewPagerAdapter(FragmentManager fm, List<AssistTab> tabs) {
            super(fm);
            mAssistTabs = new ArrayList<>(tabs);
            mArray = new SparseArray<>();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mAssistTabs.get(position).title;
        }

        @Override
        public Fragment getItem(int position) {
            BaseAssistFragment fragment = mArray.get(position);
            if (fragment != null) {
                return fragment;
            }
            AssistTab assistTab = mAssistTabs.get(position);
            fragment = assistTab.maker.apply(assistTab);
            mArray.append(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return mAssistTabs.size();
        }
    }
}
