package com.march.assistant.funcs.net;

import android.support.annotation.NonNull;

import com.march.common.utils.LgUtils;
import com.march.assistant.Assistant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * CreateAt : 2017/7/1
 * Describe : 自定义日志打印拦截器，扩展自 HttpLoggingInterceptor
 * REQ_BODY
 * REQ_HEADERS
 * RESP_BODY
 * RESP_HEADERS
 *
 * @author chendong
 */
public final class CharlesInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        try {
            Request request = chain.request();
            if (!Assistant.getInst().getInitCfg().injectAdapter.isInterceptRequest(request.url().toString())) {
                return chain.proceed(chain.request());
            }
            NetModel model = new NetModel();
            Assistant.getInst().getDataSource().storeNetModel(model);
            model.setStartTime(System.currentTimeMillis());
            logRequest(request, model);
            Response response = chain.proceed(request);
            logResponse(response, model);
            model.setDuration(System.currentTimeMillis() - model.getStartTime());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return chain.proceed(chain.request());
        }
    }

    // 打印 request
    private void logRequest(Request request, NetModel model) throws Exception {
        model.setUrl(request.url().toString());
        model.setMethod(request.method());
        Map<String, String> reqHeaders = new HashMap<>();
        model.setRequestHeaders(reqHeaders);
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            reqHeaders.put(headers.name(i), headers.value(i));
        }
        RequestBody requestBody = request.body();
        model.setRequestBody("");
        if (requestBody != null) {
            if (requestBody.contentType() != null) {
                reqHeaders.put("Content-Type:", requestBody.contentType().toString());
            }
            if (requestBody.contentLength() != -1) {
                reqHeaders.put("Content-Length:", String.valueOf(requestBody.contentLength()));
            }
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer) && charset != null) {
                String body = buffer.readString(charset);
                model.setRequestBody(toJson(body));
                model.setRequestSize(body.getBytes().length);
            } else {
                model.setRequestBody("二进制body");
                model.setRequestSize(buffer.size());
            }
        }
    }


    // 打印 response
    private void logResponse(Response response, NetModel model) throws IOException {
        model.setCode(response.code());
        model.setResponseMsg(response.message());
        Map<String, String> respHeaders = new HashMap<>();
        model.setRequestHeaders(respHeaders);
        Headers headers = response.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            respHeaders.put(headers.name(i), headers.value(i));
        }
        ResponseBody responseBody = response.body();
        model.setRequestBody("");
        if (responseBody != null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (!isPlaintext(buffer)) {
                model.setResponseBody("二进制body size = " + buffer.size());
                model.setResponseSize(buffer.size());
            } else if (responseBody.contentLength() != 0) {
                String body = buffer.clone().readString(charset == null ? Charset.forName("utf-8") : charset);
                model.setResponseBody(toJson(body));
                model.setResponseSize(body.getBytes().length);
            }
        }
    }

    private String toJson(String data){
        try {
           return new JSONObject(data).toString(2).replace("\\/", "/");
        } catch (JSONException e) {
            LgUtils.e("不是json返回");
           return "json parse error \n "+ data;
        }
    }


    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
