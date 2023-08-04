package com.hiwei.test.agent.call;
 
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
 
public abstract class BaseCall implements Serializable {
    // 主键 ID
    public Long id = SnowFlakeHelper.getInstance(1000).nextId();
 
    // 调用链的类型, SERVLET | WEB | SERVICE | JDBC
    public String type;
 
    // 调用链的唯一标识, 同样的 trace 表示是同一次调用会话
    public String trace;
 
    // 方法位于的调用层级
    public String span;
 
    // 方法抛出的异常信息
    public String error;
 
    // 方法执行的开始时间
    public long startTime;
 
    // 方法执行的结束时间
    public long endTime;
 
    // 方法运行耗时
    public long useTime;
 
    // 方法执行的线程
    public String thread;
 
    // 运行返回的结果
    public String result;
 
    // 方法所在的类的名称
    public String className;
 
    // 方法的名称
    public String methodName;
 
    // 上下文对象, 用于存储一些扩展的数据. 比如: 参数名称, 参数值...
    public Map<String, Object> context = new HashMap<>();
 
    // 必须要由子类进行实现
    public abstract String getData();
}