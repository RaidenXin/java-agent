package com.hiwei.test.agent.call;
 
public class CallJdbc extends BaseCall {
    // 编译前的 sql
    public String prepareSql;
 
    // sql 语句使用的 Statement 类型
    public String statementType;
 
    // 数据库类型
    public String databaseType;
 
    // sql 语句的类型, QUERY, EXECUTE, EXECUTE_UPDATE
    public String sqlType;
 
    // 数据库连接地址
    public String jdbcUrl;
 
    // 用户名称
    public String username;
 
    // 最终运行的 sql
    public String finallySql;
 
    // sql 携带的参数
    public String parameters;
 
    @Override
    public String getData() {
        return "{" +
                "prepareSql='" + prepareSql + '\'' +
                ", statementType='" + statementType + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", sqlType='" + sqlType + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", username='" + username + '\'' +
                ", finallySql='" + finallySql + '\'' +
                ", parameters='" + parameters + '\'' +
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