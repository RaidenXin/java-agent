package com.hiwei.test.agent.call;
 
public class CallServlet extends BaseCall {
    public String session;
    public String address;
    public String url;
    public String method;
    public String params;
    public String header;
    public String cookies;
    public Integer status;
 
    // 必须要由子类进行实现
    public String getData() {
        return "{" +
                "session='" + session + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", params='" + params + '\'' +
                ", header='" + header + '\'' +
                ", cookies='" + cookies + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", type='" + type + '\'' +
                ", trace='" + trace + '\'' +
                ", span='" + span + '\'' +
                ", error='" + error + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", useTime=" + useTime +
                ", thread='" + thread + '\'' +
                ", result='" + result + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", context=" + context +
                '}';
    }
}