package com.march.assistant;

import com.march.assistant.common.StoreHelper;
import com.march.assistant.module.console.ConsoleModel;
import com.march.assistant.module.net.NetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class DataSource implements java.io.Serializable {

    private static final int CONSOLE_LIMIT = 200;
    private static final int NET_LIMIT     = 100;

    public List<ConsoleModel> mConsoleModels;
    public List<NetModel>     mNetModels;

    public DataSource() {
        mConsoleModels = new ArrayList<>();
        mNetModels = new ArrayList<>();
    }

    public List<ConsoleModel> getConsoleModels() {
        return mConsoleModels;
    }

    public List<NetModel> netModels() {
        return mNetModels;
    }

    public void saveLog(ConsoleModel msg) {
        synchronized (DataSource.class) {
            mConsoleModels.add(0, msg);
            flush();
        }
    }

    public void saveNetModel(NetModel netModel) {
        synchronized (DataSource.class) {
            mNetModels.add(0, netModel);
            flush();
        }
    }

    public void flush() {
        synchronized (DataSource.class) {
            if (mConsoleModels.size() > CONSOLE_LIMIT * 2) {
                mConsoleModels = mConsoleModels.subList(0, CONSOLE_LIMIT);
            }
            if (mNetModels.size() > NET_LIMIT * 2) {
                mNetModels = mNetModels.subList(0, NET_LIMIT);
            }
            StoreHelper.getInst().flush();
        }
    }

    public DataSource copy() {
        synchronized (this) {
            DataSource dataSource = new DataSource();
            dataSource.mConsoleModels = new ArrayList<>(mConsoleModels);
            dataSource.mNetModels = new ArrayList<>(mNetModels);
            return dataSource;
        }
    }

}
