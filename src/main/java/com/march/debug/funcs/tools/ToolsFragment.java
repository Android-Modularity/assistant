package com.march.debug.funcs.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.march.common.Common;
import com.march.debug.Debugger;
import com.march.debug.R;
import com.march.debug.base.BaseDebugFragment;
import com.march.debug.common.CopyRunnable;
import com.march.debug.utils.SignUtils;
import com.march.debug.utils.Utils;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.extend.decoration.LinerDividerDecoration;
import com.march.lightadapter.helper.LightManager;
import com.march.lightadapter.listener.SimpleItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class ToolsFragment extends BaseDebugFragment {

    private RecyclerView           mRecyclerView;
    private LightAdapter<ItemWrap> mLightAdapter;
    private List<ItemWrap>         mItemWraps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tools_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.data_rv);
        mItemWraps = new ArrayList<>();
        initDatas();
        updateAdapter();
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initDatas() {
        mItemWraps.clear();
        mItemWraps.add(new ItemWrap("设置", "点击打开设置", new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }));
        mItemWraps.add(new ItemWrap("扫一扫", "点击扫一扫", new Runnable() {
            @Override
            public void run() {
                Utils.openScan(requireActivity());
            }
        }));
        mItemWraps.add(new ItemWrap("上次扫描", Debugger.getInst().getDataSource().getLastScanResult(), new Runnable() {
            @Override
            public void run() {
                String result = Debugger.getInst().getDataSource().getLastScanResult();
                if (!TextUtils.isEmpty(result)) {
                    Debugger.getInst().getInjector().handleScanResult(requireActivity(), result);
                }
            }
        }));
        mItemWraps.add(new ItemWrap("Build.BRAND", android.os.Build.BRAND));
        mItemWraps.add(new ItemWrap("Build.MODEL", android.os.Build.MODEL));
        mItemWraps.add(new ItemWrap("Build.VERSION", android.os.Build.VERSION.RELEASE));
        mItemWraps.add(new ItemWrap("signMd5", SignUtils.getSign(requireActivity())));
        mItemWraps.add(new ItemWrap("packageName", Common.BuildConfig.APPLICATION_ID));
        mItemWraps.add(new ItemWrap("buildType", Common.BuildConfig.BUILD_TYPE));
        mItemWraps.add(new ItemWrap("versionCode", Common.BuildConfig.VERSION_CODE));
        mItemWraps.add(new ItemWrap("versionName", Common.BuildConfig.VERSION_NAME));
        final ActivityManager activityManager = (ActivityManager) requireActivity().getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(info);
            mItemWraps.add(new ItemWrap("MemoryInfo.availMem", info.availMem));
            mItemWraps.add(new ItemWrap("MemoryInfo.lowMemory", info.lowMemory ? "低内存" : "良好"));
            mItemWraps.add(new ItemWrap("MemoryInfo.totalMem", info.totalMem));
            mItemWraps.add(new ItemWrap("getRuntime().maxMemory()", Runtime.getRuntime().maxMemory()));
            mItemWraps.add(new ItemWrap("getMemoryClass", activityManager.getMemoryClass()));
            mItemWraps.add(new ItemWrap("getLargeMemoryClass", activityManager.getLargeMemoryClass()));
        }

    }

    class ItemWrap {
        String   title;
        String   text;
        Runnable runnable;

        public ItemWrap(String title, Object text, Runnable runnable) {
            this.title = title;
            this.text = String.valueOf(text);
            this.runnable = runnable;
        }

        public ItemWrap(String title, Object text) {
            this.title = title;
            this.text = String.valueOf(text);
            this.runnable = new CopyRunnable(requireActivity(), this.text);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
        updateAdapter();
    }

    public void updateAdapter() {
        mLightAdapter = new LightAdapter<ItemWrap>(requireActivity(), mItemWraps, R.layout.common_item) {
            @Override
            public void onBindView(LightHolder holder, ItemWrap data, int pos, int type) {
                holder.setText(R.id.content_tv, Html.fromHtml(data.title + " : " + data.text));
            }
        };
        mLightAdapter.setOnItemListener(new SimpleItemListener<ItemWrap>() {
            @Override
            public void onClick(int pos, LightHolder holder, ItemWrap data) {
                if (data.runnable != null) {
                    data.runnable.run();
                }
            }
        });
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(requireActivity()));
        LinerDividerDecoration.attachRecyclerView(mRecyclerView, R.drawable.divider);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
