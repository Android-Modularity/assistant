package com.march.assistant.funcs.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.march.assistant.Assistant;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistantFragment;
import com.march.assistant.common.CopyRunnable;
import com.march.assistant.utils.SignUtils;
import com.march.assistant.utils.AssistantUtils;
import com.march.common.Common;
import com.march.common.model.AppBuildConfig;
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
public class ToolsFragment extends BaseAssistantFragment {

    private RecyclerView           mRecyclerView;
    private LightAdapter<ItemWrap> mLightAdapter;
    private List<ItemWrap>         mItemWraps;

    @Override
    public int getLayoutId() {
        return R.layout.tools_fragment;
    }

    @Override
    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.data_rv);
        mItemWraps = new ArrayList<>();
        initDatas();
        updateAdapter();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initDatas() {
        mItemWraps.clear();
        mItemWraps.add(new ItemWrap("设置", "点击打开设置", () -> startActivity(new Intent(Settings.ACTION_SETTINGS))));
        mItemWraps.add(new ItemWrap("扫一扫", "点击扫一扫", () -> AssistantUtils.openScan(requireActivity())));
        mItemWraps.add(new ItemWrap("上次扫描", Assistant.getInst().getDataSource().getLastScanResult(), () -> {
            String result = Assistant.getInst().getDataSource().getLastScanResult();
            if (!TextUtils.isEmpty(result)) {
                Assistant.getInst().getDataSource().setLastScanResult(result);
                Assistant.getInst().getScanResultAdapter().onScanResult(requireActivity(), result);
            }
        }));
        AppBuildConfig buildConfig = Common.exports.appConfig;
        mItemWraps.add(new ItemWrap("Build.BRAND", android.os.Build.BRAND));
        mItemWraps.add(new ItemWrap("Build.MODEL", android.os.Build.MODEL));
        mItemWraps.add(new ItemWrap("Build.VERSION", android.os.Build.VERSION.RELEASE));
        mItemWraps.add(new ItemWrap("signMd5", SignUtils.getSign(requireActivity())));
        mItemWraps.add(new ItemWrap("packageName",buildConfig.APPLICATION_ID));
        mItemWraps.add(new ItemWrap("buildType", buildConfig.BUILD_TYPE));
        mItemWraps.add(new ItemWrap("versionCode", buildConfig.VERSION_CODE));
        mItemWraps.add(new ItemWrap("versionName", buildConfig.VERSION_NAME));
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
