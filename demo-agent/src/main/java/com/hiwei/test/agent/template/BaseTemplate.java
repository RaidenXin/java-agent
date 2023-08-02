package com.hiwei.test.agent.template;


import com.hiwei.test.agent.call.BaseCall;

/**
 * javassist 方法增强的模板引擎, 里面将方法代理的常规逻辑整理了出来
 *
 * @auhtor tameti
 */
public class BaseTemplate {

    public BaseCall context;

    /**
     * {
     *     Object result = null;
     *     org.example.call.pojo.BaseCall context = new org.example.call.pojo.BaseCall();
     *     context.type = "Controller";
     *     context.startTime = 111110;
     *     context.className = "org.example.controller.DemoController";
     *     context.thread = "pool-1-exec-0";
     *
     *     try {
     *
     *
     * @param sb
     */
    protected void renderStart(StringBuilder sb) {
        String callType = context.context.get("CallType").toString();
 
        sb.append("{").append('\n');
        sb.append("    Object result = null;").append('\n');
        sb.append("    org.example.call.pojo.BaseCall context = new ").append(callType).append("();\n");        
        sb.append("    context.type = \"").append(context.type).append("\";\n");
        sb.append("    context.startTime =  System.currentTimeMillis();\n");
        sb.append("    context.className = \"").append(context.className).append("\";\n");
        sb.append("    context.thread = Thread.currentThread().getName();\n");
        sb.append("    context.methodName = \"").append(context.methodName).append("\";\n");
 
        if (context.context.get("names") != null) {
            sb.append("    context.context.put(\"names\",").append(context.context.get("names")).append(");\n");
            sb.append("    context.context.put(\"values\", $args);\n");
        }
        sb.append("    try {\n");
    }
 
    /**
     *     ${instance}.before(context);
     *     result = ${method}$agent($$);
     *     context.context.put("result", result);
     *     context.result = com.codetool.common.JsonHelper.stringify(result);
     *     ${instance}.after(context);
     *
     * @param sb
     */
    protected void renderCenter(StringBuilder sb) {
        String instance = context.context.get("instance").toString();
        sb.append("        ").append(instance).append(".before(context);\n");
        sb.append("        result = ").append(context.methodName).append("$agent($$);\n");
        sb.append("        context.context.put(\"result\", result);\n");
        sb.append("        context.result = com.codetool.common.JsonHelper.stringify(result);\n");
        sb.append("        ").append(instance).append(".after(context);\n");
    }
 
 
    /**
     *
     * } catch (Throwable e) {
     *     context.error = e.getMessage();
     *     ${instance}.throwing(context);
     * } finally {
     *     context.endTime = System.currentTimeMillis();
     *     context.useTime = context.endTime - context.startTime;
     *     ${instance}.finale(context);
     *     return ($r) context.context.get("result");
     * }
     *
     * @param sb
     */
    protected void renderEnd(StringBuilder sb) {
        String instance = context.context.get("instance").toString();
        sb.append("    } catch (Throwable e) {\n");
        sb.append("        context.error = e.getMessage();\n");
        sb.append("        ").append(instance).append(".throwing(context);\n");
        sb.append("    } finally {\n");
        sb.append("        context.endTime = System.currentTimeMillis();\n");
        sb.append("        context.useTime = context.endTime - context.startTime;\n");
        sb.append("        ").append(instance).append(".finale(context);\n");
        sb.append("    }\n");
    }
 
    protected void renderReturn(StringBuilder sb) {
        sb.append("    return ($r) context.context.get(\"result\");\n");
    }
 
 
 
    protected void renderFinally(StringBuilder sb) {
        sb.append("}\n");
    }
 
    public String render() {
        StringBuilder sb = new StringBuilder();
        renderStart(sb);
        renderCenter(sb);
        renderEnd(sb);
        renderReturn(sb);
        renderFinally(sb);
        return sb.toString();
    }
}