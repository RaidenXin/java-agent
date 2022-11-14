package com.hiwei.test.agent.utils;

import java.util.Objects;

/**
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2022/11/13 20:09
 */
public final class PrintUtils {

    public static final void print(Object param,Object result) {
        printParam(param);
        printResult(result);
    }

    private static void printParam(Object param) {
        if (param instanceof String) {
            System.err.println("入参是字符串 param:" + param);
        }
        if (param instanceof Number) {
            System.err.println("入参是包装数字 param:" + param);
        }
    }

    private static void printResult(Object result) {
        if (Objects.isNull(result)) {
            System.err.println("返回结果是字符串 result: null");
        }
        if (result instanceof String) {
            System.err.println("返回结果是字符串 result:" + result);
        }
        if (result instanceof Number) {
            System.err.println("返回结果是包装数字 result:" + result);
        }
    }
}
