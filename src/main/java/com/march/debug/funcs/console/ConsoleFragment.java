package com.march.debug.funcs.console;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.stetho.common.LogUtil;
import com.march.debug.Debug;
import com.march.debug.DebugFragment;
import com.march.debug.R;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.helper.LightManager;

import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class ConsoleFragment extends DebugFragment {

    private RecyclerView               mRecyclerView;
    private LightAdapter<ConsoleModel> mLightAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.console_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.data_rv);
        updateAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    public void updateAdapter() {
        List<ConsoleModel> logMsgs = Debug.getDataSource().getConsoleModels();
        if (mLightAdapter != null) {
            mLightAdapter.update().update(logMsgs);
            return;
        }
        mLightAdapter = new LightAdapter<ConsoleModel>(getActivity(), logMsgs, R.layout.console_item) {
            @Override
            public void onBindView(LightHolder holder, ConsoleModel data, int pos, int type) {
                holder.setText(R.id.tv, data.getMsg());
            }
        };
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(getActivity()));
    }

}
