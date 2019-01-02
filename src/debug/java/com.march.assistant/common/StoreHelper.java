package com.march.assistant.common;

import com.march.assistant.Assistant;
import com.march.assistant.AssistantDebugImpl;
import com.march.assistant.DataSource;
import com.march.common.mgrs.KVMgr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class StoreHelper {

    public static final int TIME_LIMIT = 10_000;

    private volatile static StoreHelper sInst;

    public static StoreHelper getInst() {
        if (sInst == null) {
            synchronized (StoreHelper.class) {
                if (sInst == null) {
                    sInst = new StoreHelper();
                }
            }
        }
        return sInst;
    }


    private StoreHelper() {
        mExecutorService = Executors.newSingleThreadExecutor();
        mStorageRunnable = new StorageRunnable();
    }

    private ExecutorService mExecutorService;
    private StorageRunnable mStorageRunnable;
    private long            mLastStoreTime;

    public void flush() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastStoreTime > TIME_LIMIT) {
            mLastStoreTime = curTime;
            mExecutorService.execute(mStorageRunnable);
        }
    }

    // 将文件存储起来
    class StorageRunnable implements Runnable {
        @Override
        public void run() {
            try {
                DataSource ds = ((AssistantDebugImpl) Assistant.assist()).dataSource().copy();
                KVMgr.getInst().putObj(AssistValues.KEY_SOURCE, ds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}