package com.march.debug.funcs.net;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.march.debug.Debugger;
import com.march.debug.base.BaseDebugFragment;
import com.march.debug.R;
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

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class NetFragment extends BaseDebugFragment {

    public static WeakReference<NetModel> mCurNetModelRef = new WeakReference<>(null);

    private RecyclerView           mRecyclerView;
    private LightAdapter<NetModel> mLightAdapter;
    private SimpleDateFormat       mTimeFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.net_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.data_rv);
        mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        updateAdapter();
        return view;
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
        List<NetModel> netModels = Debugger.getInst().getDataSource().getNetModels();
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
                        .append(String.format(Locale.getDefault(),"%.2f",data.getResponseSize() / 2014f)).append("kb")
                        .append("  ·  ")
                        .append(data.getDuration()).append("ms").toString();
                holder.setText(R.id.path_tv, data.getUrl().encodedPath())
                        .setText(R.id.host_tv, data.getUrl().host())
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
        LinerDividerDecoration.attachRecyclerView(mRecyclerView,R.drawable.divider);
    }
}
