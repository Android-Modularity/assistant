package com.march.debug;

import java.net.URLDecoder;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class Utils {


    public static String decode(String data) {
        String result = data;
        try {
            result = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
