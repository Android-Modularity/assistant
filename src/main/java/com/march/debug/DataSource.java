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

    private List<ConsoleModel> mConsoleModels;
    private List<NetModel>     mNetModels;

    public DataSource() {
        mConsoleModels = new ArrayList<>();
        mNetModels = new ArrayList<>();
    }

    public void storeLog(ConsoleModel msg) {
        mConsoleModels.add(msg);
    }

    public void storeNetModel(NetModel netModel) {
        mNetModels.add(netModel);
    }

    public List<ConsoleModel> getConsoleModels() {
        return mConsoleModels;
    }

    public List<NetModel> getNetModels() {
        return mNetModels;
    }
}
