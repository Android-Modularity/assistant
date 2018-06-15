package com.march.debug.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.march.common.utils.ToastUtils;

import java.net.URLDecoder;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class Utils {


    public static final int SCAN_REQ_CODE = 101;

    public static String decode(String data) {
        String result = data;
        try {
            result = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void openScan(Activity activity) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            activity.startActivityForResult(intent, SCAN_REQ_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://weex.apache.org/cn/tools/playground.html"));
            activity.startActivity(intent);
            ToastUtils.show("请下载扫码工具");
        }
    }
}
