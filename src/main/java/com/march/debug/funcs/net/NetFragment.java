package com.march.debug.funcs.net;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.march.debug.Debug;
import com.march.debug.DebugFragment;
import com.march.debug.R;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.helper.LightManager;

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
public class NetFragment extends DebugFragment {

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
        updateAdapter();
    }

    public void updateAdapter() {
        List<NetModel> netModels = Debug.getDataSource().getNetModels();
        if (mLightAdapter != null) {
            mLightAdapter.update().update(netModels);
            return;
        }
        mLightAdapter = new LightAdapter<NetModel>(getActivity(), netModels, R.layout.console_item) {
            @Override
            public void onBindView(LightHolder holder, NetModel data, int pos, int type) {
                String detail = new StringBuilder()
                        .append(mTimeFormat.format(new Date(data.getStartTime())))
                        .append("  ·  ")
                        .append(data.getMethod())
                        .append("  ·  ")
                        .append(data.getResponseSize() / 2014f).append("kb")
                        .append("  ·  ")
                        .append(data.getDuration()).append("ms").toString();
                holder.setText(R.id.path_tv, data.getUrl().encodedPath())
                        .setText(R.id.host_tv, data.getUrl().host())
                        .setText(R.id.detail_tv, detail);
            }
        };
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(getActivity()));
    }
}
