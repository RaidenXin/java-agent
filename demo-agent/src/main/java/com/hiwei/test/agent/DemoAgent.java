package com.hiwei.test.agent;

import com.hiwei.test.agent.utils.StringUtils;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;

public class DemoAgent {
    /**
     * 该方法在main方法之前运行
     * @param arg 文件路径
     * @param instrumentation
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        instrumentation.addTransformer(new DemoTransformer());
    }
}
