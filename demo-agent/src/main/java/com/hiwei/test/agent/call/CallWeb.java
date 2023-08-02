package com.hiwei.test.agent.call;
 
 
public class CallWeb extends BaseCall {
 
    @Override
    public String getData() {
        return "INSERT INTO `call_web`(" + "`id`, `trace`, `span`, `result`, " +
                "`class_name`, `method_name`, `thread`, `start_time`, " +
                "`end_time`, `use_time`, `error`) VALUES(" +
                id + ", \"" + trace + "\", \"" + span + "\", '" + result + "', " +
                "\"" + className + "\", \"" + methodName + "\", \"" + thread + "\", " +
                startTime + "," + endTime + "," + useTime + ",\"" + error + "\");";
    }
 
}