package com.march.assistant.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.march.common.x.ToastX;

import java.net.URLDecoder;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class AssistantUtils {

    public static final int SCAN_REQ_CODE = 101;


    // 显示 toast
    public static void toast(String msg) {
        ToastX.show(msg);
    }

    // 转码
    public static String decode(String data) {
        String result = data;
        try {
            result = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 打开扫码界面
    public static void openScan(Activity activity) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            activity.startActivityForResult(intent, SCAN_REQ_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://weex.apache.org/cn/tools/playground.html"));
            activity.startActivity(intent);
            toast("请下载扫码工具");
        }
    }
}
