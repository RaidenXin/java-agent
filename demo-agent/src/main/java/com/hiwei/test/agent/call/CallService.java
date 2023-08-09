package com.hiwei.test.agent.call;
 
public class CallService extends BaseCall {
    @Override
    public String getData() {
        return "{" +
                "id=" + id +
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