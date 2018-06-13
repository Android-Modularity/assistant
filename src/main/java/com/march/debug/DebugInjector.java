package com.march.debug;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public interface DebugInjector {

    DebugInjector EMPTY = new DebugInjector() {
        @Override
        public boolean checkNetModel(String url) {
            return true;
        }
    };

    /**
     * @param url url
     * @return true save, false not save
     */
    boolean checkNetModel(String url);


}
