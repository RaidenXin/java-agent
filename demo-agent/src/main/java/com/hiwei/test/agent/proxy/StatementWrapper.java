package com.hiwei.test.agent.proxy;

import com.alibaba.fastjson.JSON;
import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.CallJdbc;
import com.hiwei.test.agent.call.CallSpan;
import com.hiwei.test.agent.helper.SnowFlakeHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
 
public class StatementWrapper {

    private static final Logger LOGGER = LogManager.getLogger(StatementWrapper.class);

    protected CallJdbc context;
    private static final String QUERY = "QUERY";
    private static final String EXECUTE = "EXECUTE";
    private static final String EXECUTE_UPDATE = "EXECUTE_UPDATE";
    private static final String EXECUTE_BATCH = "EXECUTE_BATCH";
    private static final String BATCH = "BATCH";
 
    StatementWrapper(CallJdbc context) {
        LOGGER.info("new Statement ...");
        CallSpan.Span span = CallContext.createEntrySpan(null);
        this.context = onStartUp(context);
        this.context.id = SnowFlakeHelper.getInstance().nextId();
        this.context.thread = Thread.currentThread().getName();
        this.context.startTime = System.currentTimeMillis();
        this.context.trace = CallContext.getTrace();
        this.context.span = span.toString();
    }
 
    void handleStatement() {
        // 1. 获取 sql 携带的参数
        Object parameters = context.context.get("parameters");
        if (parameters != null) {
            context.parameters = JSON.toJSONString(parameters);
        }
        LOGGER.info(context.getData());
        CallContext.exitSpan();
    }
 
    private CallJdbc onStartUp(CallJdbc context) {
        CallJdbc app = new CallJdbc();
        app.jdbcUrl = context.jdbcUrl;
        app.username = context.username;
        app.databaseType = context.databaseType;
        app.className = context.className;
        app.type = context.type;
        app.prepareSql = context.prepareSql;
        return app;
    }
 
 
    /*==============================================================================*/
    /*================================= 查询切面 ====================================*/
    /*==============================================================================*/
 
    void queryBefore(String sql) {
        context.sqlType = QUERY;
        context.finallySql = sql;
        context.startTime = System.currentTimeMillis();
    }
 
    void queryEnd(ResultSet resultSet) throws SQLException {
        context.endTime = System.currentTimeMillis();
        context.useTime = context.endTime - context.startTime;
 
        resultSet.last();
        context.result = String.valueOf(resultSet.getRow());
        resultSet.first();
    }
 
 
    /*==============================================================================*/
    /*================================= 执行切面 ====================================*/
    /*==============================================================================*/
 
    void executeBefore(String sql) {
        context.sqlType = EXECUTE;
        context.finallySql = sql;
        context.startTime = System.currentTimeMillis();
    }
 
    void executeEnd(boolean status) {
        context.endTime = System.currentTimeMillis();
        context.useTime = context.endTime - context.startTime;
        context.result = String.valueOf(status);
    }
 
 
    /*==============================================================================*/
    /*================================= 修改切面 ====================================*/
    /*==============================================================================*/
 
    void executeUpdateBefore(String sql) {
        context.sqlType = EXECUTE_UPDATE;
        context.finallySql = sql;
        context.startTime = System.currentTimeMillis();
    }
 
    void executeUpdateEnd(int row) {
        context.endTime = System.currentTimeMillis();
        context.useTime = context.endTime - context.startTime;
        context.result = String.valueOf(row);
    }
 
 
    /*==============================================================================*/
    /*================================= 批量切面 ====================================*/
    /*==============================================================================*/
 
    void executeBatchBefore(String sql) {
        context.sqlType = EXECUTE_BATCH;
        context.finallySql = sql;
        context.startTime = System.currentTimeMillis();
    }
 
    void executeBatchEnd(int[] result) {
        context.endTime = System.currentTimeMillis();
        context.useTime = context.endTime - context.startTime;
        context.result = Arrays.toString(result);
    }
 
 
    /*==============================================================================*/
    /*================================= 添加批量 ====================================*/
    /*==============================================================================*/
 
    void batchBefore(String sql) {
        context.sqlType = BATCH;
        context.finallySql += sql;
    }
 
    void batchClear() {
        context.finallySql = null;
    }
 
}