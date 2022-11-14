package com.hiwei.test.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class DemoTransformer implements ClassFileTransformer {

    private static final String CLASS_NAME = "org.springframework.data.redis.core.DefaultValueOperations";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassPool classPool = ClassPool.getDefault();
        String realClassName = className.replaceAll("/", ".");

        //获取类
        if (CLASS_NAME.equalsIgnoreCase(realClassName)) {
            try {
                CtClass ctClass = classPool.get(realClassName);
                CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
                for (CtMethod m : declaredMethods) {
                    String mName = m.getName();
                    System.err.println("方法名称：" + mName);
                    if (ValueOperationsMethodMatch.INSTANCE.match(mName)) {
                        MethodInfo methodInfo = m.getMethodInfo();
                        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                        LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                        System.err.println("----------------------------------");
                        m.insertAfter("{ com.hiwei.test.agent.utils.PrintUtils.print(" + attribute.variableName(1) + ", $_); }");
                    }
                }
                return ctClass.toBytecode();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}
