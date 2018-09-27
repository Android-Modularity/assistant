package com.march.assistant.funcs.console;

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

import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class ConsoleFragment extends BaseAssistantFragment {

    private RecyclerView mRecyclerView;
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
        List<ConsoleModel> logMsgs = Assistant.getInst().getDataSource().getConsoleModels();
        if (mLightAdapter != null) {
            mLightAdapter.update().update(logMsgs);
            return;
        }
        mLightAdapter = new LightAdapter<ConsoleModel>(getActivity(), logMsgs, R.layout.console_item) {
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
