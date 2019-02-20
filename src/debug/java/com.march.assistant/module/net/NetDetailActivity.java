package com.march.assistant.module.net;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;

import com.march.assistant.R;
import com.march.assistant.base.BaseAssistActivity;
import com.march.assistant.common.CopyRunnable;
import com.march.assistant.module.browser.ViewJsonActivity;
import com.march.assistant.module.browser.ViewTextActivity;
import com.march.assistant.utils.AssistantUtils;
import com.march.common.x.ClipboardX;
import com.march.common.x.LogX;
import com.march.common.x.ToastX;
import com.zfy.adapter.LightAdapter;
import com.zfy.adapter.extend.decoration.LinearDividerDecoration;

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
            ItemWrap wrap = new ItemWrap("response body", "点击查看/长按复制", () -> viewText(mNetModel.getResponseBody()));
            wrap.pressAction = () -> {
                ClipboardX.copy(this, mNetModel.getResponseBody());
                LogX.e(mNetModel.getResponseBody());
                ToastX.show("已复制，日志同步打印");

            };
            mItemWraps.add(wrap);
        }
        mItemWraps.add(new ItemWrap("----------------------- request ------------------------"));
        mItemWraps.add(new ItemWrap("request size", getSizeFormat(mNetModel.getRequestSize())));
        if (TextUtils.isEmpty(mNetModel.getRequestBody())) {
            mItemWraps.add(new ItemWrap("request body", "没有内容"));
        } else {
            ItemWrap wrap = new ItemWrap("request body", "点击查看内容", () -> {
                viewText(mNetModel.getRequestBody());
            });
            wrap.pressAction = () -> {
                ClipboardX.copy(this, mNetModel.getRequestBody());
                LogX.e(mNetModel.getRequestBody());
                ToastX.show("已复制，日志同步打印");
            };
            mItemWraps.add(wrap);
        }
        mItemWraps.add(new ItemWrap("form", mNetModel.getPostForms()));
        mItemWraps.add(new ItemWrap("query", httpUrl.encodedQuery()));
        Set<String> requestQueryKeys = httpUrl.queryParameterNames();
        if (!requestQueryKeys.isEmpty()) {
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
        mLightAdapter = new LightAdapter<>(mItemWraps, R.layout.common_item);
        mLightAdapter.setBindCallback((holder, data, extra) -> {
            holder.setText(R.id.content_tv, Html.fromHtml(data.desc, null, null));

        });
        mLightAdapter.setClickEvent((holder, data, extra) -> {
            if (data.clickAction != null) {
                data.clickAction.run();
            }
        });
        mLightAdapter.setLongPressEvent((holder, data, extra) -> {
            if (data.pressAction != null) {
                data.pressAction.run();
            }
        });
        mRecyclerView.addItemDecoration(new LinearDividerDecoration(this, LinearDividerDecoration.VERTICAL, R.drawable.divider));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mLightAdapter);
    }

    class ItemWrap {
        String   title;
        String   text;
        Runnable clickAction;
        Runnable pressAction;
        String   desc;

        public ItemWrap(String title, String text, Runnable runnable) {
            this.desc = concat(title, text);
            this.clickAction = runnable;
        }

        public ItemWrap(String title, String text) {
            this.title = title;
            this.text = text;
            this.desc = concat(title, text);
            this.clickAction = new CopyRunnable(this.text);
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


