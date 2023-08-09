package com.hiwei.test.agent;
 
import com.hiwei.test.agent.filter.FilterChain;
import com.hiwei.test.agent.filter.HttpServletFilterChain;
import com.hiwei.test.agent.filter.SpringControllerFilterChain;
import com.hiwei.test.agent.filter.SpringJdbcFilterChain;
import com.hiwei.test.agent.filter.SpringServiceFilterChain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AgentApplication implements ClassFileTransformer {

    private static final Logger LOGGER = LogManager.getLogger(AgentApplication.class);

    private static final List<String> IGNORE_CLASS_NAME_STARTS_WITH = new ArrayList<String>() {{
        add("net.bytebuddy.");
        add("org.slf4j.");
        add("org.groovy.");
        add("sun.reflect");
    }};

    private static final List<String> IGNORE_CLASS_NAME_CONTAINS = new ArrayList<String>() {{
        add("javassist");
        add(".asm.");
        add(".reflectasm.");
    }};

    // kotlin.Metadata
    private static final List<String> IGNORE_CLASS_NAME = new ArrayList<String>() {{
        add("kotlin.Metadata");
    }};

    private final List<FilterChain> chains;
 
    private AgentApplication() {
        chains = new ArrayList<>(4);
        chains.add(new HttpServletFilterChain());
        chains.add(new SpringControllerFilterChain());
        chains.add(new SpringServiceFilterChain());
        chains.add(new SpringJdbcFilterChain());
    }

    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new AgentApplication());
    }
 
 
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (Objects.isNull(className)) {
            return classfileBuffer;
        }
        String finalClassName = StringUtils.replace(className, "/", ".");
        if (ignore(finalClassName)) {
            return classfileBuffer;
        }
        try {
            ClassPool pool = ClassPool.getDefault();
            if (loader != null) {
                pool.insertClassPath(new LoaderClassPath(loader));
            } else {
                pool.insertClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
            }
            CtClass sourceClass = pool.getCtClass(finalClassName);
            if (sourceClass.isAnnotation()) {
                return classfileBuffer;
            }
            for (FilterChain chain : chains) {
                if (chain.isTargetClass(finalClassName, sourceClass)) {
                    LOGGER.info("尝试对类: " + className + " 进行增强");
                    try {
                        return chain.processingAgentClass(loader, sourceClass, finalClassName);
                    } catch (Exception e) {
                        LOGGER.error("无法对类 " + className + " 进行增强, 具体的错误原因是: " + e.getMessage(), e);
                    }
                }
            }
        } catch (NotFoundException e) {
            LOGGER.debug("未找到 className:" + finalClassName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return classfileBuffer;
    }
    
    
    private boolean ignore(final String finalClassName) {
        // 是否包含忽略的 className 片段
        boolean ignoreClassNameContains = IGNORE_CLASS_NAME_CONTAINS.stream().anyMatch(finalClassName::contains);
        // 是否是以忽略的className 前缀开头
        boolean ignoreClassNameStartsWith = IGNORE_CLASS_NAME_STARTS_WITH.stream().anyMatch(finalClassName::startsWith);
        // 是否忽略的类
        boolean ignoreClassName = IGNORE_CLASS_NAME.stream().anyMatch(finalClassName::equalsIgnoreCase);
        return ignoreClassNameContains || ignoreClassNameStartsWith || ignoreClassName;
    }
 
}