package com.hiwei.test.agent.filter;
 
import javassist.CtClass;
 
/**
 * 这个接口的作用是滤我们需要代理的目标类
 *
 * @author tameti
 */
public interface FilterChain {
 
    /**
     * 是否是需要代理的目标类
     *
     * @param className 类的全路径名称
     * @param ctClass   类的字节码对象
     * @return
     */
    boolean isTargetClass(String className, CtClass ctClass);
 
    /**
     * 具体的代理逻辑方法入口
     *
     * @param loader    当前类的类加载器
     * @param ctClass   类的字节码对象
     * @param className 类的全路径名称
     * @return 返回处理后的字节数组
     */
    byte[] processingAgentClass(ClassLoader loader, CtClass ctClass, String className) throws Exception;
 
}