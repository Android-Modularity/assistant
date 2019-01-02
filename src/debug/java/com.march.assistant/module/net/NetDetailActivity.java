package com.march.assistant.module.net;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;

import com.march.assistant.R;
import com.march.assistant.base.BaseAssistActivity;
import com.march.assistant.common.CopyRunnable;
import com.march.assistant.module.browser.ViewJsonActivity;
import com.march.assistant.module.browser.ViewTextActivity;
import com.march.assistant.utils.AssistantUtils;
import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.LightInjector;
import com.march.lightadapter.extend.decoration.LinerDividerDecoration;
import com.march.lightadapter.helper.LightManager;
import com.march.lightadapter.listener.SimpleItemListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class NetDetailActivity extends BaseAssistActivity {

    public static final String MODEL = "model";

    private RecyclerView           mRecyclerView;
    private LightAdapter<ItemWrap> mLightAdapter;
    private SimpleDateFormat       mTimeFormat;
    private NetModel               mNetModel;
    private List<ItemWrap>         mItemWraps;

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, NetDetailActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_detail_activity);
        mRecyclerView = findViewById(R.id.data_rv);
        mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mNetModel = NetFragment.mCurNetModelRef.get();
        NetFragment.mCurNetModelRef.clear();
        if (mNetModel == null) {
            return;
        }
        initDatas();
        updateAdapter();
    }

    private String getSizeFormat(long size) {
        return String.format(Locale.getDefault(), "%.2f", size / 2014f) + "kb";
    }

    private String concat(String title, String content) {
        return title + "  :  " + content;
    }

    private void initDatas() {
        mItemWraps = new ArrayList<>();
        HttpUrl httpUrl = mNetModel.parseHttpUrl();
        mItemWraps.add(new ItemWrap("url", AssistantUtils.decode(httpUrl.toString())));
        mItemWraps.add(new ItemWrap("host", httpUrl.host()));
        mItemWraps.add(new ItemWrap("path", httpUrl.encodedPath()));

        mItemWraps.add(new ItemWrap("method", mNetModel.getMethod()));
        mItemWraps.add(new ItemWrap("code", String.valueOf(mNetModel.getCode())));
        mItemWraps.add(new ItemWrap("start time", mTimeFormat.format(new Date(mNetModel.getStartTime()))));
        mItemWraps.add(new ItemWrap("duration", mNetModel.getDuration() + "ms"));
        mItemWraps.add(new ItemWrap("----------------------- response ------------------------"));
        mItemWraps.add(new ItemWrap("response size", getSizeFormat(mNetModel.getResponseSize())));
        Map<String, String> responseHeaders = mNetModel.getResponseHeaders();
        if (responseHeaders != null && !responseHeaders.isEmpty()) {
            for (String key : responseHeaders.keySet()) {
                mItemWraps.add(new ItemWrap("[Header]" + key, responseHeaders.get(key)));
            }
        }
        if (TextUtils.isEmpty(mNetModel.getResponseBody())) {
            mItemWraps.add(new ItemWrap("response body", "没有内容"));
        } else {
            mItemWraps.add(new ItemWrap("response body", "点击查看内容", () -> viewText(mNetModel.getResponseBody())));
        }
        mItemWraps.add(new ItemWrap("----------------------- request ------------------------"));
        mItemWraps.add(new ItemWrap("request size", getSizeFormat(mNetModel.getRequestSize())));
        if (TextUtils.isEmpty(mNetModel.getRequestBody())) {
            mItemWraps.add(new ItemWrap("request body", "没有内容"));
        } else {
            mItemWraps.add(new ItemWrap("request body", "点击查看内容", () -> {
                viewText(mNetModel.getRequestBody());
            }));
        }
        mItemWraps.add(new ItemWrap("form", mNetModel.getPostForms()));
        mItemWraps.add(new ItemWrap("query", httpUrl.encodedQuery()));
        Set<String> requestQueryKeys = httpUrl.queryParameterNames();
        if (requestQueryKeys != null && !requestQueryKeys.isEmpty()) {
            for (String key : requestQueryKeys) {
                mItemWraps.add(new ItemWrap("[Query]" + key, AssistantUtils.decode(httpUrl.queryParameter(key))));
            }
        }
        Map<String, String> requestHeaders = mNetModel.getRequestHeaders();
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            for (String key : requestHeaders.keySet()) {
                mItemWraps.add(new ItemWrap("[Header]" + key, requestHeaders.get(key)));
            }
        }
    }

    public void updateAdapter() {
        mLightAdapter = new LightAdapter<ItemWrap>(this, mItemWraps, R.layout.common_item) {
            @Override
            public void onBindView(LightHolder holder, ItemWrap data, int pos, int type) {
                holder.setText(R.id.content_tv, Html.fromHtml(data.desc, null, null));
            }
        };
        mLightAdapter.setOnItemListener(new SimpleItemListener<ItemWrap>() {
            @Override
            public void onClick(int pos, LightHolder holder, ItemWrap data) {
                if (data.runnable != null) {
                    data.runnable.run();
                }
            }
        });
        LightInjector.initAdapter(mLightAdapter, this, mRecyclerView, LightManager.vLinear(this));
        LinerDividerDecoration.attachRecyclerView(mRecyclerView, R.drawable.divider);
    }

    class ItemWrap {
        String   title;
        String   text;
        Runnable runnable;
        String   desc;

        public ItemWrap(String title, String text, Runnable runnable) {
            this.desc = concat(title, text);
            this.runnable = runnable;
        }

        public ItemWrap(String title, String text) {
            this.title = title;
            this.text = text;
            this.desc = concat(title, text);
            this.runnable = new CopyRunnable(this.text);
        }

        public ItemWrap(String desc) {
            this.desc = desc;
        }
    }


    private void viewText(String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            if (jsonObject.length() > 0) {
                ViewJsonActivity.startActivity(this, text, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ViewTextActivity.startActivity(this, text, null);
        }
    }
}


