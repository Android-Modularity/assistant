package com.march.debug;

import com.march.debug.funcs.console.ConsoleModel;
import com.march.debug.funcs.net.NetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class DataSource {

    public static final int CONSOLE_LIMIT = 200;
    public static final int NET_LIMIT     = 100;

    private List<ConsoleModel> mConsoleModels;
    private List<NetModel>     mNetModels;
    private String             mLastScanResult;

    public DataSource() {
        mConsoleModels = new ArrayList<>();
        mNetModels = new ArrayList<>();
    }

    public void storeLog(ConsoleModel msg) {
       synchronized (DataSource.class) {
           mConsoleModels.add(0,msg);
           checkStore();
       }
    }

    public void storeNetModel(NetModel netModel) {
        synchronized (DataSource.class) {
            mNetModels.add(0, netModel);
            checkStore();
        }
    }

    public List<ConsoleModel> getConsoleModels() {
        return mConsoleModels;
    }

    public List<NetModel> getNetModels() {
        return mNetModels;
    }


    public void checkStore() {
        synchronized (DataSource.class) {
            if (mConsoleModels.size() > CONSOLE_LIMIT * 2) {
                mConsoleModels = mConsoleModels.subList(0, CONSOLE_LIMIT);
            }
            if (mNetModels.size() > NET_LIMIT * 2) {
                mNetModels = mNetModels.subList(0, NET_LIMIT);
            }
            Debugger.getInst().getStorageInfoManager().store();
        }
    }

    public String getLastScanResult() {
        return mLastScanResult;
    }

    public void setLastScanResult(String lastScanResult) {
        mLastScanResult = lastScanResult;
    }

    public DataSource copy() {
        synchronized (this) {
            DataSource dataSource = new DataSource();
            dataSource.mConsoleModels = new ArrayList<>(mConsoleModels);
            dataSource.mNetModels = new ArrayList<>(mNetModels);
            dataSource.mLastScanResult = mLastScanResult;
            return dataSource;
        }
    }

    public void backUp(DataSource source){
        mNetModels = new ArrayList<>(source.mNetModels);
        mConsoleModels = new ArrayList<>(source.mConsoleModels);
        mLastScanResult = source.mLastScanResult;
    }
}
