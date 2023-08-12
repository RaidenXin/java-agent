package com.hiwei.test.agent.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 返回类型枚举
 * @Author 疆戟
 * @Date 2023/8/12 23:15
 * @Version 1.0
 */
public enum ReturnTypeEnums {

    BOOLEAN(boolean.class.getName(), "false"),
    INT(boolean.class.getName(), "0"),
    FLOAT(boolean.class.getName(), "0.0"),
    DOUBLE(boolean.class.getName(), "0,0D"),
    LONG(boolean.class.getName(), "0L"),
    OBJECT(null, "null") {
        @Override
        public String typeConversion(String returnValueName) {
            return returnValueName;
        }
    };


    private static final Map<String, ReturnTypeEnums> ENUM_CACHE;

    static {
        ENUM_CACHE = Stream.of(ReturnTypeEnums.values())
                .filter(item -> item != OBJECT)
                .collect(Collectors.toMap(ReturnTypeEnums::getName, item -> item, (a, b) -> a));
    }

    private final String name;

    private final String initializeValue;

    private ReturnTypeEnums(String name,String initializeValue) {
        this.name = name;
        this.initializeValue = initializeValue;
    }

    public String getName() {
        return name;
    }


    public static ReturnTypeEnums of(String name) {
        return ENUM_CACHE.getOrDefault(name, OBJECT);
    }

    public String getInitializeValue() {
        return initializeValue;
    }


    /**
     * 初始化返回值
     * @param returnValueName
     * @return
     */
    public String typeConversion(String returnValueName) {
        return "String.valueOf(" + returnValueName + ")";
    }
}
