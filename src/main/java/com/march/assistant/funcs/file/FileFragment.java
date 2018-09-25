package com.march.assistant.funcs.file;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.march.assistant.R;
import com.march.assistant.base.BaseAssistantFragment;
import com.march.assistant.funcs.browser.BrowserTextActivity;
import com.march.common.Common;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.extend.decoration.LinerDividerDecoration;
import com.march.lightadapter.helper.LightManager;
import com.march.lightadapter.listener.SimpleItemListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class FileFragment extends BaseAssistantFragment {

    private RecyclerView            mRecyclerView;
    private LightAdapter<FileModel> mLightAdapter;
    private FileModel               mCurFileModel;

    @Override
    public int getLayoutId() {
        return R.layout.file_fragment;
    }

    @Override
    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.data_rv);
        initDatas();
        updateAdapter();
    }


    @TargetApi(Build.VERSION_CODES.FROYO)
    private void initDatas() {
        if (getActivity() == null) {
            return;
        }
        mCurFileModel = new FileModel(null);
        mCurFileModel.setTop(true);

        List<FileModel> list = new ArrayList<>();
        list.add(new FileModel("FilesDir",getActivity().getFilesDir(), mCurFileModel));
        list.add(new FileModel("CacheDir",getActivity().getCacheDir(), mCurFileModel));
        list.add(new FileModel("ExternalCacheDir",getActivity().getExternalCacheDir(), mCurFileModel));
        list.add(new FileModel("存储卡",Environment.getExternalStorageDirectory(), mCurFileModel));
        list.add(new FileModel(new File(Environment.getExternalStorageDirectory(), "weex-cache"), mCurFileModel));
        mCurFileModel.setChildren(list);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    public void updateAdapter() {
        List<FileModel> children = mCurFileModel.getChildren();
        if (mLightAdapter != null) {
            mLightAdapter.update().update(children);
            if(mCurFileModel.getIndex()>0 && mCurFileModel.getIndex() < mLightAdapter.getDatas().size()) {
                mRecyclerView.scrollToPosition(mCurFileModel.getIndex());
            }
            return;
        }
        mLightAdapter = new LightAdapter<FileModel>(getActivity(), children, R.layout.file_item) {
            @Override
            public void onBindView(LightHolder holder, FileModel data, int pos, int type) {
                boolean isDir = data.getFile().isDirectory();
                holder.setText(R.id.file_name_tv, data.getName())
                        .setText(R.id.file_type_tv, isDir ? "目录" : "文件")
                        .setBgColor(R.id.file_type_tv, Color.parseColor(isDir ? "#fdb325" : "#1eb271"))
                        .setText(R.id.file_desc_tv, data.getFile().getAbsolutePath());
            }
        };
        mLightAdapter.setOnItemListener(new SimpleItemListener<FileModel>() {
            @Override
            public void onClick(int pos, LightHolder holder, FileModel data) {
                boolean isDir = data.getFile().isDirectory();
                if (isDir) {
                    mCurFileModel.setIndex(pos);
                    mCurFileModel = data;
                    updateAdapter();
                } else {
                    openFile(data.getFile());
                }
            }
        });
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(getActivity()));
        LinerDividerDecoration.attachRecyclerView(mRecyclerView, R.drawable.divider);
        if(mCurFileModel.getIndex()>0 && mCurFileModel.getIndex() < mLightAdapter.getDatas().size()) {
            mRecyclerView.scrollToPosition(mCurFileModel.getIndex());
        }
    }


    private boolean checkSuffix(String path,String ... suffixs){
        String lowerCase = path.toLowerCase();
        for (String suffix : suffixs) {
            if(lowerCase.endsWith(suffix)){
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openFile(File file) {
        if(checkSuffix(file.getName(),"log","txt","js","html","htm",".0")){
            BrowserTextActivity.startActivity(getActivity(),file.getAbsolutePath());
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(requireActivity(), Common.getInst().getBuildConfig().APPLICATION_ID + ".fileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else{
            uri = Uri.fromFile(file);
        }
        if(checkSuffix(file.getName(),"png","jpg","gif","jpeg","webp")){
            intent.setDataAndType(uri,"image/*");
        } else if(checkSuffix(file.getName(),"log","txt","js","html","htm")) {
            intent.setDataAndType(uri,"text/*");
        }
        intent.setClipData(new ClipData("clip",new String[]{},new ClipData.Item(file.getAbsolutePath())));
        startActivity(intent);
    }

    @Override
    public boolean onBackPressed() {
        if (mCurFileModel.getParent() == null) {
            return false;
        } else {
            mCurFileModel = mCurFileModel.getParent();
            for (FileModel fileModel : mCurFileModel.getChildren()) {
                fileModel.setIndex(0);
            }
            updateAdapter();
            return true;
        }
    }
}
