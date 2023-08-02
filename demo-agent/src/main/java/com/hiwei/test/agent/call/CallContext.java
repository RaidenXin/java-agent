package com.hiwei.test.agent.call;

import java.util.Objects;

/**
 * 调用链上下文对象
 *
 * @author tameti
 */
public class CallContext {
    private static ThreadLocal<CallSpan> context = new ThreadLocal<>();
    private static ThreadLocal<String> traceContext = new ThreadLocal<>();
 
    public static CallSpan.Span createEntrySpan(String span) {
        CallSpan callSpan = context.get();
        if (callSpan == null) {
            callSpan = new CallSpan(span);
            context.set(callSpan);
            return callSpan.getCurrentSpan();
        }
        return callSpan.createEntrySpan();
    }
 
    public static void exitSpan() {
        CallSpan callSpan = context.get();
        if (callSpan != null) {
            callSpan.exitSpan();
        }
    }
 
    public static CallSpan.Span getCurrentSpan() {
        CallSpan callSpan = context.get();
        if (callSpan == null) {
            return null;
        }
        return callSpan.getCurrentSpan();
    }
 
    public static void setTrace(String trace) {
        if (Objects.isNull(trace)) {
            trace = SnowFlakeHelper.getInstance(100).nextId() + "";
        }
        traceContext.set(trace);
    }
 
    public static String getTrace() {
        return traceContext.get();
    }
 
    public static void main(String[] args) {
        CallContext.createEntrySpan(null);
        CallContext.exitSpan();
    }
}