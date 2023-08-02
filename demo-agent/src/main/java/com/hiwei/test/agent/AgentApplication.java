package com.hiwei.test.agent;
 
import com.hiwei.test.agent.filter.FilterChain;
import com.hiwei.test.agent.filter.HttpServletFilterChain;
import com.hiwei.test.agent.filter.SpringControllerFilterChain;
import com.hiwei.test.agent.filter.SpringJdbcFilterChain;
import com.hiwei.test.agent.filter.SpringServiceFilterChain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
 
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
 
public class AgentApplication implements ClassFileTransformer {
    private final List<FilterChain> chains = new ArrayList<>();
    private static final byte[] NO_TRANSFORM = null;
 
    private AgentApplication() {
        chains.add(new HttpServletFilterChain());
        chains.add(new SpringControllerFilterChain());
        chains.add(new SpringServiceFilterChain());
        chains.add(new SpringJdbcFilterChain());
    }
 
 
    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new AgentApplication());
    }
 
 
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (className == null) {
                return NO_TRANSFORM;
            }
            String finalClassName = className.replace("/", ".");
 
            ClassPool pool = new ClassPool(true);
            if (loader != null) {
                pool.insertClassPath(new LoaderClassPath(loader));
            } else {
                pool.insertClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
            }
 
            for (FilterChain chain : chains) {
                CtClass sourceClass = pool.getCtClass(finalClassName);
                if (chain.isTargetClass(finalClassName, sourceClass)) {
                    System.err.println("尝试对类: " + className + " 进行增强");
                    try {
                        return chain.processingAgentClass(loader, sourceClass, finalClassName);
                    } catch (Exception e) {
                        System.out.println("无法对类 " + className + " 进行增强, 具体的错误原因是: " + e.toString());
                    }
                }
            }
 
        } catch (Exception e) {
            // TODO ...
        }
 
        return NO_TRANSFORM;
    }
 
 
}