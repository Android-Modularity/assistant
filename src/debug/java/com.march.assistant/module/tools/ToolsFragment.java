package com.march.assistant.module.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.march.assistant.Assistant;
import com.march.assistant.IAssistant;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistFragment;
import com.march.assistant.utils.AssistantUtils;
import com.march.assistant.utils.SignUtils;
import com.march.common.Common;
import com.march.common.model.AppBuildConfig;
import com.zfy.adapter.LightAdapter;
import com.zfy.adapter.extend.decoration.LinearDividerDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class ToolsFragment extends BaseAssistFragment {

    public static ToolsFragment newInstance() {
        Bundle args = new Bundle();
        ToolsFragment fragment = new ToolsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private RecyclerView             mRecyclerView;
    private LightAdapter<ToolsModel> mLightAdapter;
    private List<ToolsModel>         mToolsModels;

    @Override
    public int getLayoutId() {
        return R.layout.tools_fragment;
    }

    @Override
    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.data_rv);
        mToolsModels = new ArrayList<>();
        initDatas();
        updateAdapter();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initDatas() {
        IAssistant assist = Assistant.assist();
        mToolsModels.clear();
        mToolsModels.add(new ToolsModel("设置", "点击打开设置", () -> startActivity(new Intent(Settings.ACTION_SETTINGS))));
        mToolsModels.add(new ToolsModel("扫一扫", "点击扫一扫", () -> AssistantUtils.openScan(requireActivity())));
        mToolsModels.add(new ToolsModel("上次扫描", assist.info().scanResult, () -> {
            CharSequence result = Assistant.assist().info().scanResult;
            if (!TextUtils.isEmpty(result)) {
                assist.opts().getScanResultCallback().onScanResult(requireActivity(), result);
                assist.info().scanResult = result;
                assist.flush();
            }
        }));
        AppBuildConfig buildConfig = Common.exports.appConfig;
        mToolsModels.add(new ToolsModel("Build.BRAND", android.os.Build.BRAND));
        mToolsModels.add(new ToolsModel("Channel", Common.appConfig().CHANNEL));
        mToolsModels.add(new ToolsModel("Build.MODEL", android.os.Build.MODEL));
        mToolsModels.add(new ToolsModel("Build.VERSION", android.os.Build.VERSION.RELEASE));
        mToolsModels.add(new ToolsModel("signMd5", SignUtils.getSign(requireActivity())));
        mToolsModels.add(new ToolsModel("packageName", buildConfig.APPLICATION_ID));
        mToolsModels.add(new ToolsModel("buildType", buildConfig.BUILD_TYPE));
        mToolsModels.add(new ToolsModel("versionCode", buildConfig.VERSION_CODE));
        mToolsModels.add(new ToolsModel("versionName", buildConfig.VERSION_NAME));
        final ActivityManager activityManager = (ActivityManager) requireActivity().getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(info);
            mToolsModels.add(new ToolsModel("MemoryInfo.availMem", info.availMem));
            mToolsModels.add(new ToolsModel("MemoryInfo.lowMemory", info.lowMemory ? "低内存" : "良好"));
            mToolsModels.add(new ToolsModel("MemoryInfo.totalMem", info.totalMem));
            mToolsModels.add(new ToolsModel("getRuntime().maxMemory()", Runtime.getRuntime().maxMemory()));
            mToolsModels.add(new ToolsModel("getMemoryClass", activityManager.getMemoryClass()));
            mToolsModels.add(new ToolsModel("getLargeMemoryClass", activityManager.getLargeMemoryClass()));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
        updateAdapter();
    }

    public void updateAdapter() {
        mLightAdapter = new LightAdapter<>(mToolsModels, R.layout.common_item);
        mLightAdapter.setBindCallback((holder, data, extra) -> {
            holder.setText(R.id.content_tv, Html.fromHtml(data.title + " : " + data.text));
        });
        mLightAdapter.setClickEvent((holder, data, extra) -> {
            if (data.runnable != null) {
                data.runnable.run();
            }
        });
        mRecyclerView.addItemDecoration(new LinearDividerDecoration(getContext(), LinearDividerDecoration.VERTICAL, R.drawable.divider));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLightAdapter);
    }

}
