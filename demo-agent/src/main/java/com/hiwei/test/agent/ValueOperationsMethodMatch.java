package com.hiwei.test.agent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2022/11/14 21:10
 */
public enum  ValueOperationsMethodMatch {

    INSTANCE;


    private static final Set<String> METHOD_NAMES = new HashSet<String>(){{add("get");}};

    public boolean match(String methodName) {
        return METHOD_NAMES.contains(methodName);
    }
}
