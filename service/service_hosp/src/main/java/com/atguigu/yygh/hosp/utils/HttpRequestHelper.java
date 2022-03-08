package com.atguigu.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月07日 10:26
 */
public class HttpRequestHelper {

    public static Map<String, Object> switchMap(Map<String, String[]> map) {
        HashMap<String, Object> result = new HashMap<>();

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue()[0]);
        }
        return result;
    }

}
