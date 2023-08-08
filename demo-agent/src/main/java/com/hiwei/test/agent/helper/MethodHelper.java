package com.hiwei.test.agent.helper;

import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 方法工具
 * @Author 疆戟
 * @Date 2023/8/5 15:12
 * @Version 1.0
 */
public final class MethodHelper {

    private static final Logger LOGGER = LogManager.getLogger(MethodHelper.class);

    public static List<String> getVariables(CtMethod ctMethod) {
        if (Objects.isNull(ctMethod)) {
            return new ArrayList<>();
        }
        try {
            // 获取方法信息
            final MethodInfo methodInfo = ctMethod.getMethodInfo();
            //获取属性变量相关
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            if (Objects.isNull(codeAttribute)) {
                return new ArrayList<>();
            }
            int parametersCount = ctMethod.getParameterTypes().length;
            // 获取方法本地变量信息，包括方法声明和方法体内的变量
            // 需注意，若方法为非静态方法，则第一个变量名为this
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (Objects.isNull(attr)) {
                return new ArrayList<>();
            }
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            List<String> parameterNames = new ArrayList<>();
            for (int i = 0; i < parametersCount; i++) {
                String paramName = attr.variableName(i + pos);
                parameterNames.add(paramName);
            }
        } catch (NotFoundException e) {
            LOGGER.info(e.getMessage(), e);
        }
        return new ArrayList<>();
    }
}
