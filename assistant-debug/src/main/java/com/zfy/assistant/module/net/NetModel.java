package com.zfy.assistant.module.net;

import android.text.TextUtils;

import java.util.Map;

import okhttp3.HttpUrl;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class NetModel implements java.io.Serializable{

    private String url;
    private int    code;
    private String method;
    private long   startTime;
    private long   duration;

    private Map<String, String> requestHeaders;
    private String              requestBody;
    private String              postForms;
    private long                requestSize;

    private Map<String, String> responseHeaders;
    private String              responseBody;
    private long                responseSize;
    private String              responseMsg;
    private HttpUrl             httpUrl;

    public NetModel() {
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public HttpUrl parseHttpUrl() {
        if (httpUrl == null) {
            if (TextUtils.isEmpty(url)) {
                url = "http://www.unknow.com/shayemeiyou?reason=解析失败的默认";
            }
            httpUrl = HttpUrl.parse(url);
        }
        return httpUrl;
    }

    public void setHttpUrl(HttpUrl httpUrl) {
        this.httpUrl = httpUrl;
        this.url = httpUrl.toString();
    }


    public String getPostForms() {
        return postForms;
    }

    public void setPostForms(String postForms) {
        this.postForms = postForms;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public long getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(long requestSize) {
        this.requestSize = requestSize;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(long responseSize) {
        this.responseSize = responseSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
