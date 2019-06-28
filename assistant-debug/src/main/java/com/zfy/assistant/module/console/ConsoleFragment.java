package com.zfy.assistant.module.console;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zfy.assistant.Assistant;
import com.zfy.assistant.AssistantDebugImpl;
import com.march.assistant.R;
import com.zfy.assistant.base.BaseAssistFragment;
import com.zfy.adapter.LightAdapter;
import com.zfy.adapter.extend.decoration.LinearDividerDecoration;

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
            mLightAdapter.setDatas(logs);
            mLightAdapter.notifyItem().change();
            return;
        }
        mLightAdapter = new LightAdapter<>(logs, R.layout.console_item);
        mLightAdapter.setBindCallback((holder, data, extra) -> {
            if (data != null) {
                holder.setText(R.id.tv, data.getTag() + " : " + data.getMsg());
            }
        });
        mRecyclerView.addItemDecoration(new LinearDividerDecoration(getContext(), LinearDividerDecoration.VERTICAL, R.drawable.divider));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLightAdapter);
    }

}
