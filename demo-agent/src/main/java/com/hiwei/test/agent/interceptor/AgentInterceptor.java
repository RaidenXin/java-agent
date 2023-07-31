package com.hiwei.test.agent.interceptor;

/**
 * Agent 拦截器接口
 * @author 疆戟
 * @date 2023/7/31 09:30
 */
public interface AgentInterceptor {

    default void postHandle(Object param) throws Exception {
    }

    default void afterCompletion(Object handler) throws Exception {
    }
}
