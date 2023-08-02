package com.hiwei.test.agent.template;
 
import java.util.HashMap;
import java.util.Map;

public class TemplateFactory {
    private static Map<Boolean, BaseTemplate> TEMPLATE_MAP = new HashMap<>();

    static {
        TEMPLATE_MAP.put(true, new BaseTemplate());
        TEMPLATE_MAP.put(false, new VoidTemplate());
    }

    public static BaseTemplate getTemplate(boolean mode) {
        return TEMPLATE_MAP.get(mode);
    }
}