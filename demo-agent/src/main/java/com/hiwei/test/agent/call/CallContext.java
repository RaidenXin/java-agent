package com.hiwei.test.agent.call;

import com.hiwei.test.agent.helper.SnowFlakeHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 调用链上下文对象
 *
 * @author tameti
 */
public final class CallContext {
    private static final ThreadLocal<CallSpan> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<String> TRACE_CONTEXT = new ThreadLocal<>();
 
    public static CallSpan.Span createEntrySpan(String span) {
        CallSpan callSpan = CONTEXT.get();
        if (callSpan == null) {
            callSpan = new CallSpan(span);
            CONTEXT.set(callSpan);
            return callSpan.getCurrentSpan();
        }
        return callSpan.createEntrySpan();
    }
 
    public static void exitSpan() {
        CallSpan callSpan = CONTEXT.get();
        if (callSpan != null) {
            callSpan.exitSpan();
        }
    }
 
    public static CallSpan.Span getCurrentSpan() {
        CallSpan callSpan = CONTEXT.get();
        if (callSpan == null) {
            return null;
        }
        return callSpan.getCurrentSpan();
    }
 
    public static void setTrace(String trace) {
        if (Objects.isNull(trace)) {
            trace = SnowFlakeHelper.getInstance().nextId() + StringUtils.EMPTY;
        }
        TRACE_CONTEXT.set(trace);
    }
 
    public static String getTrace() {
        return TRACE_CONTEXT.get();
    }
}