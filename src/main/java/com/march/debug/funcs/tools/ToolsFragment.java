package com.march.debug.funcs.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.march.debug.BuildConfig;
import com.march.debug.R;
import com.march.debug.SignUtils;
import com.march.debug.base.BaseDebugFragment;
import com.march.debug.common.CopyRunnable;
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
        initDatas();
        updateAdapter();
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initDatas() {
        mItemWraps = new ArrayList<>();
        mItemWraps.add(new ItemWrap("设置", "点击打开设置", new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }));
        try {
            PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), PackageManager.GET_INSTRUMENTATION);
            mItemWraps.add(new ItemWrap("signMd5", SignUtils.getSign(requireActivity())));
            mItemWraps.add(new ItemWrap("packageName", packageInfo.packageName));
            mItemWraps.add(new ItemWrap("buildType", BuildConfig.BUILD_TYPE));
            mItemWraps.add(new ItemWrap("versionCode", String.valueOf(packageInfo.versionCode)));
            mItemWraps.add(new ItemWrap("versionName", packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
}
