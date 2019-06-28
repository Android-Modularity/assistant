package com.zfy.assistant.module.crash;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.march.common.Common;
import com.march.common.mgrs.ActivityMgr;
import com.march.common.model.WeakContext;
import com.march.common.x.LogX;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * CreateAt : 2018/12/3
 * Describe : 当闪退时弹出 dialog 显示错误信息
 *
 * @author chendong
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private WeakContext   mContext;
    private CrashCallBack mCrashCallBack;

    public static void init(Application app, CrashCallBack crashCallBack) {
        CrashHandler handler = new CrashHandler();
        handler.mContext = new WeakContext(app);
        handler.mCrashCallBack = crashCallBack;
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        // 将异常信息写入SDCard文件中
        dumpExceptionToSDCard(throwable);
        // 异常外部接口
        if (mCrashCallBack != null) {
            mCrashCallBack.getThrowable(throwable);
        }
        // 弹出错误日志对话框
        new Thread(() -> {
            showExceptionDialog(throwable);// 显示错误日志对话框
        }).start();
    }

    // 显示异常信息的 dialog
    private void showExceptionDialog(Throwable throwable) {
        if(!Common.appConfig().isDev()){
            return;
        }
        // 弹出报错并强制退出的对话框
        List<WeakReference<Activity>> activityList = ActivityMgr.getInst().getActivityList();
        Activity topActivity = ActivityMgr.getInst().getTopActivity();
        if (activityList.size() > 0 && topActivity != null) {
            Looper.prepare();
            AlertDialog dialog = new AlertDialog.Builder(topActivity).create();
            dialog.setMessage(Log.getStackTraceString(throwable));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", (dialog1, which) -> {
                // 强制退出程序
                if (dialog1 != null) {
                    dialog1.dismiss();
                }
                ActivityMgr.getInst().removeAll();
                System.exit(0);
            });
            dialog.show();
            Looper.loop();
        }
    }

    // 内存信息写入 sd 卡
    private void dumpExceptionToSDCard(Throwable ex) {
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            if (BuildConfig.DEBUG) {
//                LogX.e("sdcard unmounted,skip dump excepting");
//                return;
//            }
//        }
//
//        File dir = new File(mLogPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        File file = new File(mLogPath + "crash" + currentTime + ".trace");
//
//        try {
//            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
//            pw.println(currentTime);
//            dumpPhoneInfo(pw);
//            pw.println();
//            ex.printStackTrace(pw);
//            pw.close();
//        } catch (IOException e) {
//            LogX.e("dump crash info failed");
//        }
    }


    // 获取设备信息
    private void dumpPhoneInfo(PrintWriter printWriter) {
        try {
            if (mContext.get() == null) {
                return;
            }
            Context context = mContext.get();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            //应用版本号
            printWriter.print("App Version: ");
            printWriter.print(packageInfo.versionName);
            printWriter.print("_");
            printWriter.println(packageInfo.versionCode);
            //Android版本号
            printWriter.print("OS Version: ");
            printWriter.print(Build.VERSION.RELEASE);
            printWriter.print("_");
            printWriter.println(Build.VERSION.SDK_INT);
            //手机制作商
            printWriter.print("Vendor: ");
            printWriter.println(Build.MANUFACTURER);
            //手机型号
            printWriter.print("Model: ");
            printWriter.println(Build.MODEL);
            //CPU 架构
            printWriter.print("CPU ABI: ");
            printWriter.println(Build.CPU_ABI);
        } catch (PackageManager.NameNotFoundException e) {
            LogX.e("get phone info failed");
        }
    }

    public interface CrashCallBack {
        void getThrowable(Throwable ex);
    }
}