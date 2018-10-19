package com.march.assistant.funcs.net;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.march.assistant.Assistant;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistantFragment;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.extend.decoration.LinerDividerDecoration;
import com.march.lightadapter.helper.LightManager;
import com.march.lightadapter.listener.SimpleItemListener;

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
public class NetFragment extends BaseAssistantFragment {

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
            Assistant.getInst().getDataSource().getNetModels().clear();
            mLightAdapter.getDatas().clear();
            mLightAdapter.update().notifyDataSetChanged();
            Assistant.getInst().getDataSource().checkStore();
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
        List<NetModel> netModels = Assistant.getInst().getDataSource().getNetModels();
        if (mLightAdapter != null) {
            mLightAdapter.update().update(netModels);
            return;
        }
        mLightAdapter = new LightAdapter<NetModel>(getActivity(), netModels, R.layout.net_item) {
            @Override
            public void onBindView(LightHolder holder, NetModel data, int pos, int type) {
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
            }
        };
        mLightAdapter.setOnItemListener(new SimpleItemListener<NetModel>() {
            @Override
            public void onClick(int pos, LightHolder holder, NetModel data) {
                mCurNetModelRef = new WeakReference<>(data);
                NetDetailActivity.startActivity(getActivity());
            }
        });
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(getActivity()));
        LinerDividerDecoration.attachRecyclerView(mRecyclerView, R.drawable.divider);
    }
}
