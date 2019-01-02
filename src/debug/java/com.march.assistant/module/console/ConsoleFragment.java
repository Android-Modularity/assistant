package com.march.assistant.module.console;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.march.assistant.Assistant;
import com.march.assistant.AssistantDebugImpl;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistFragment;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.extend.decoration.LinerDividerDecoration;
import com.march.lightadapter.helper.LightManager;

import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class ConsoleFragment extends BaseAssistFragment {

    public static ConsoleFragment newInstance() {
        Bundle args = new Bundle();
        ConsoleFragment fragment = new ConsoleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView               mRecyclerView;
    private LightAdapter<ConsoleModel> mLightAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.console_fragment;
    }

    @Override
    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.data_rv);
        updateAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    public void updateAdapter() {
        List<ConsoleModel> logs = ((AssistantDebugImpl) Assistant.assist()).dataSource().getConsoleModels();
        if (mLightAdapter != null) {
            mLightAdapter.update().update(logs);
            return;
        }
        mLightAdapter = new LightAdapter<ConsoleModel>(getActivity(), logs, R.layout.console_item) {
            @Override
            public void onBindView(LightHolder holder, ConsoleModel data, int pos, int type) {
                if (data != null) {
                    holder.setText(R.id.tv, data.getTag() + " : " + data.getMsg());
                }
            }
        };
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(getActivity()));
        LinerDividerDecoration.attachRecyclerView(mRecyclerView, R.drawable.divider);
    }

}
