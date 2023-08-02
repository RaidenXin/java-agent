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
        return "INSERT INTO `call_servlet`(" + "`id`, `trace`, `span`, `session`, " +
                "`address`, `url`, `method`, `params`," +
                "`header`, `cookies`, `thread`, `status`," +
                "`start_time`, `end_time`, `use_time`, `error`) VALUES(" +
                id + ", \"" + trace + "\", \"" + span + "\", \"" + session + "\", " +
                "\"" + address + "\", \"" + url + "\", \"" + method + "\", '" + params + "', " +
                "'" + header + "', '" + cookies + "', \"" + thread + "\", " + status + ", " +
                startTime + "," + endTime + "," + useTime + ", \"" + error + "\");";
    }
}