package com.march.assistant.module.net;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.march.assistant.Assistant;
import com.march.assistant.AssistantDebugImpl;
import com.march.assistant.DataSource;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistFragment;
import com.zfy.adapter.LightAdapter;
import com.zfy.adapter.extend.decoration.LinearDividerDecoration;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class NetFragment extends BaseAssistFragment {

    public static NetFragment newInstance() {
        Bundle args = new Bundle();
        NetFragment fragment = new NetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static WeakReference<NetModel> mCurNetModelRef = new WeakReference<>(null);

    private RecyclerView           mRecyclerView;
    private LightAdapter<NetModel> mLightAdapter;
    private SimpleDateFormat       mTimeFormat;

    @Override
    public int getLayoutId() {
        return R.layout.net_fragment;
    }

    @Override
    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.data_rv);
        view.findViewById(R.id.btn).setOnClickListener(v -> {
            DataSource dataSource = ((AssistantDebugImpl) Assistant.assist()).dataSource();
            dataSource.netModels().clear();
            mLightAdapter.getDatas().clear();
            mLightAdapter.notifyItem().change();
            dataSource.flush();
        });
        mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        updateAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurNetModelRef.clear();
        updateAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCurNetModelRef.clear();
    }

    public void updateAdapter() {
        List<NetModel> netModels = ((AssistantDebugImpl) Assistant.assist()).dataSource().netModels();
        if (mLightAdapter != null) {
            mLightAdapter.setDatas(netModels);
            mLightAdapter.notifyItem().change();
            return;
        }
        mLightAdapter = new LightAdapter<>(netModels, R.layout.net_item);
        mLightAdapter.setBindCallback((holder, data, extra) -> {
            String detail = new StringBuilder()
                    .append(mTimeFormat.format(new Date(data.getStartTime())))
                    .append("  ·  ")
                    .append(data.getMethod())
                    .append("  ·  ")
                    .append(String.format(Locale.getDefault(), "%.2f", data.getResponseSize() / 2014f)).append("kb")
                    .append("  ·  ")
                    .append(data.getDuration()).append("ms").toString();
            HttpUrl httpUrl = data.parseHttpUrl();
            holder.setText(R.id.path_tv, httpUrl.encodedPath())
                    .setText(R.id.host_tv, httpUrl.host())
                    .setText(R.id.detail_tv, detail);
        });
        mLightAdapter.setClickEvent((holder, data, extra) -> {
            mCurNetModelRef = new WeakReference<>(data);
            NetDetailActivity.startActivity(getActivity());
        });
        mRecyclerView.addItemDecoration(new LinearDividerDecoration(getContext(), LinearDividerDecoration.VERTICAL, R.drawable.divider));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLightAdapter);
    }
}
