package com.hiwei.test.agent.template;
 
public class VoidTemplate extends BaseTemplate {
 
    @Override
    protected void renderCenter(StringBuilder sb) {
        sb.append("    try {\n");
        String instance = context.context.get("instance").toString();
        sb.append("        ").append(instance).append(".before(context);\n");
        sb.append("        ").append(context.methodName).append("$agent($$);\n");
        sb.append("        ").append(instance).append(".after(context);\n");
    }
 
    @Override
    protected void renderReturn(StringBuilder sb) {}
}