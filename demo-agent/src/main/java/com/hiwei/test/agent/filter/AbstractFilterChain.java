package com.hiwei.test.agent.filter;

import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.BaseCall;
import com.hiwei.test.agent.common.Constant;
import com.hiwei.test.agent.helper.MethodHelper;
import javassist.CtMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * 这个类对拦截的方法的具体执行逻辑做了一系列的抽象.
 *
 * @author tameti
 */
public abstract class AbstractFilterChain implements FilterChain {

    private static final Logger LOGGER = LogManager.getLogger(AbstractFilterChain.class);
    /**
     * 增强的方法执行之前执行这个方法, 它会接收一个空的 Map 用来记录方法的运行信息.
     * 然后接收原来的方法里面的所有参数, 并且封装为一个 Object[] 数组.最终返回一个对象
     * 用于替换原来方法执行时产生的 result
     *
     * @param context 上下文对象
     * @return 返回一个用于替换原来的返回结果的对象, 如果为 null, 则不替换原来的返回对象
     */
    public void before(BaseCall context) {
 
    }
 
    /**
     * 增强的方法执行之后执行这个方法, 它会接收 before 这个方法返回的 Map 作为第一个参数, 然后接收
     * 原来的方法的所有参数, 将它封装为一个 Object[] 数组, 最后一个参数接收原来的方法的返回值. 最终
     * 这个方法也可以返回一个 Object 对象用于替换返回值.
     *
     * @param context 上下文对象
     * @return 返回一个用于替换原来的返回结果的对象, 如果为 null, 则不替换原来的返回对象
     */
    public void after(BaseCall context) {
 
    }
 
    /**
     * 增强的方法出现异常后会执行这个方法, 它会接收 before 方法返回的 Map 作为第一个参数, 然后接收原来的
     * 方法中的所有具体的实参, 并且封装为一个 Object[] 数组, 最后一个参数是这个方法运行时抛出的异常信息.
     * 最终这个方法也可以返回一个 Object 对象用于替换返回值.
     *
     * @param context 上下文对象
     * @return 返回一个用于替换原来的返回结果的对象, 如果为 null, 则不替换原来的返回对象
     */
    public void throwing(BaseCall context) {
 
    }
 
    /**
     * 增强的方法最终会执行的这个方法, 执行顺序不同, 和 org.example.filter.AbstractFilterChain.after(Map<\String,Object>, Object[], Object) 差不多
     *
     * @param context 上下文对象
     * @return 返回一个用于替换原来的返回结果的对象, 如果为 null, 则不替换原来的返回对象
     */
    public void finale(BaseCall context) {
        CallContext.exitSpan();
        LOGGER.info(context.getData());
    }
 
    /**
     * 渲染参数列表
     *
     * @param method 字节码方法
     */
    protected String renderParamNames(CtMethod method) {
        List<String> variables = MethodHelper.getVariables(method);
        if (variables.size() > 0) {
            StringBuilder sb = new StringBuilder("new String[] {");
            for (String variable : variables) {
                sb.append("\"").append(variable).append("\"");
                sb.append(",");
            }
            sb.setCharAt(sb.length() - Constant.ONE, '}');
            return  sb.toString();
        }
        return null;
    }
 
    /**
     * 判断方法是否为需要处理的方法
     *
     * @param m
     * @return
     */
    protected boolean processed(CtMethod m) {
        int modifiers = m.getModifiers();
        if (!java.lang.reflect.Modifier.isPublic(modifiers)) {
            return false;
        }
        if (java.lang.reflect.Modifier.isStatic(modifiers)) {
            return false;
        }
        if (Object.class.getName().equals(m.getDeclaringClass().getName())) {
            return false;
        }
        return !java.lang.reflect.Modifier.isNative(modifiers);
    }
 
}