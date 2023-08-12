package com.hiwei.test.agent.template;


import com.hiwei.test.agent.call.BaseCall;

import static com.hiwei.test.agent.common.Constant.RETURN_TYPE_NAME;

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
     *     com.hiwei.test.agent.call.BaseCall context = new com.hiwei.test.agent.call.BaseCall();
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
        sb.append("    com.hiwei.test.agent.call.BaseCall context = new ").append(callType).append("();\n");        
        sb.append("    context.type = \"").append(context.type).append("\";\n");
        sb.append("    context.startTime =  System.currentTimeMillis();\n");
        sb.append("    context.className = \"").append(context.className).append("\";\n");
        sb.append("    context.thread = Thread.currentThread().getName();\n");
        sb.append("    context.methodName = \"").append(context.methodName).append("\";\n");
 
        if (context.context.get("names") != null) {
            sb.append("    context.context.put(\"names\",").append(context.context.get("names")).append(");\n");
            sb.append("    context.context.put(\"values\", $args);\n");
        }
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
        String returnTypeName = (String) context.context.get(RETURN_TYPE_NAME);
        sb.append("    java.lang.Object result1 = null;").append('\n');
        sb.append("    ").append(returnTypeName).append(" result2 = ").append(initReturnValue(returnTypeName)).append(";").append('\n');
        sb.append("    try {\n");
        String instance = context.context.get("instance").toString();
        sb.append("        ").append(instance).append(".before(context);\n");
        sb.append("        result2 = ").append(context.methodName).append("$agent($$);\n");
        sb.append("        result1 = ").append(typeConversion(returnTypeName)).append(";\n");
        sb.append("        context.context.put(\"result\", result1);\n");
        sb.append("        context.result = com.alibaba.fastjson.JSON.toJSONString(result1);\n");
        sb.append("        ").append(instance).append(".after(context);\n");
    }

    /**
     * 初始化返回值
     * @param returnTypeName
     * @return
     */
    private String initReturnValue(String returnTypeName) {
        if (boolean.class.getName().equals(returnTypeName)) {
            return "false";
        } else if (int.class.getName().equals(returnTypeName)) {
            return "0";
        } else if (float.class.getName().equals(returnTypeName)) {
            return "0.0";
        } else if (double.class.getName().equals(returnTypeName)) {
            return "0.0D";
        } else if (long.class.getName().equals(returnTypeName)) {
            return "0L";
        }
        return "null";
    }

    /**
     * 初始化返回值
     * @param returnTypeName
     * @return
     */
    private String typeConversion(String returnTypeName) {
        if (boolean.class.getName().equals(returnTypeName)) {
            return "String.valueOf(result2)";
        } else if (int.class.getName().equals(returnTypeName)) {
            return "String.valueOf(result2)";
        } else if (float.class.getName().equals(returnTypeName)) {
            return "String.valueOf(result2)";
        } else if (double.class.getName().equals(returnTypeName)) {
            return"String.valueOf(result2)";
        } else if (long.class.getName().equals(returnTypeName)) {
            return "String.valueOf(result2)";
        }
        return "result2";
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
        sb.append("    return result2;\n");
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