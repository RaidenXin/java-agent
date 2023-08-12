package com.hiwei.test.agent.call;

import com.hiwei.test.agent.common.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


/**
 * 吸取了 TreeSpan 的精华, 去其糟粕. 底层是基于两个指针实现的, 1 个指针维护调用链的父节点, 另一个
 * 指针维护调用链的最大子节点. 并且重写了 clone() 方法, 从而在跨 Thread 的情况下实现值得深拷贝
 *
 * @author tameti
 */
public class CallSpan implements Cloneable {
    // 记录当前节点轨迹
    private Span currentSpan;
 
    public CallSpan() {
        this(null);
    }
 
    public CallSpan(String span) {
        if (StringUtils.isBlank(span)) {
            currentSpan = new Span(Constant.ZERO, null);
        } else {
            String[] spans = StringUtils.split(span, Constant.POINT);
            Span tmpSpan = null;
            for (String s : spans) {
                int v = Integer.parseInt(s);
                tmpSpan = new Span(v, tmpSpan);
            }
            currentSpan = tmpSpan;
        }
    }
 
    public Span createEntrySpan() {
        if (Objects.isNull(currentSpan)) {
            currentSpan = new Span(Constant.ZERO, null);
            return currentSpan;
        }
        Span childSpan = currentSpan.childSpan;
        int value = Constant.ONE;
        // 如果当前节点下不存在子节点, 那么我们就直接创建一个 0 作为第一个子节点
        // 如果已经存在子节点了, 那么我们就取这个栈中最后一个元素的值, 然后 +1 作为新的节点的值
        if (childSpan != null) {
            value = childSpan.value + Constant.ONE;
        }
 
        // 接下来开始创建 Span 节点, 它的父亲节点是我们的轨迹节点
        Span newSpan = new Span(value, currentSpan);
        currentSpan.childSpan = newSpan;
        // 最后修改轨迹的节点的指针
        currentSpan = newSpan;
        return newSpan;
    }
 
    public void exitSpan() {
        if (currentSpan != null) {
            // 1. 拿到轨迹节点的父亲节点
            // 2. 然后修改轨迹节点的指针
            currentSpan = currentSpan.parentSpan;
        }
    }
 
    public Span getCurrentSpan() {
        return currentSpan;
    }
 
 
    @Override
    protected CallSpan clone() {
        return new CallSpan(currentSpan.toString());
    }
 
    public static class Span {
        private int value;
        private Span parentSpan;
        private Span childSpan;
 
        private Span(int value, Span parentSpan) {
            this.value = value;
            this.parentSpan = parentSpan;
        }
 
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            Span currentSpan = parentSpan;
            while (currentSpan != null) {
                sb.append(Constant.POINT).append(currentSpan.value);
                currentSpan = currentSpan.parentSpan;
            }
            return sb.toString();
        }
    }
}