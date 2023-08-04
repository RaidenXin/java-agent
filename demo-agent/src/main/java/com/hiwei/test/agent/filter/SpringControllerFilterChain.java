package com.hiwei.test.agent.filter;
 

import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.BaseCall;
import com.hiwei.test.agent.call.CallSpan;
import com.hiwei.test.agent.call.CallWeb;
import com.hiwei.test.agent.template.BaseTemplate;
import com.hiwei.test.agent.template.TemplateFactory;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.util.HashSet;
import java.util.Set;

public class SpringControllerFilterChain extends AbstractFilterChain {
    
    // 如果类上包含这两个注解中的任意一个则进行插桩
    private static final Set<String> CONTROLLER_ANNOTATIONS = new HashSet<String>() {{
        add("@org.springframework.web.bind.annotation.RestController");
        add("@org.springframework.stereotype.Controller");
    }};
 
    // 如果方法上包含 6 个注解中的任意一个则进行插桩
    private static final Set<String> MAPPING_ANNOTATIONS = new HashSet<String>() {{
        add("@org.springframework.web.bind.annotation.RequestMapping");
        add("@org.springframework.web.bind.annotation.GetMapping");
        add("@org.springframework.web.bind.annotation.PostMapping");
        add("@org.springframework.web.bind.annotation.PutMapping");
        add("@org.springframework.web.bind.annotation.DeleteMapping");
        add("@org.springframework.web.bind.annotation.PatchMapping");
    }};
 
 
    @Override
    public void before(BaseCall context) {
        CallSpan.Span span = CallContext.createEntrySpan(null);
        context.trace = CallContext.getTrace();
        context.span = span.toString();
    }
 
 
    @Override
    public boolean isTargetClass(String className, CtClass ctClass) {
        // 不处理 BasicErrorController 类
        if (className.equals("org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController")) {
            return false;
        }
 
        // 不处理注解
        if (!ctClass.isAnnotation()) {
            try {
                for (Object annotation : ctClass.getAnnotations()) {

                }
            } catch (ClassNotFoundException e) {
                // System.err.println(e.getMessage());
            }
        }
        return false;
    }
 
 
    @Override
    public byte[] processingAgentClass(ClassLoader loader, CtClass ctClass, String className) throws Exception {
        CtMethod[] methods = ctClass.getMethods();
 
        for (CtMethod method : methods) {
            if (!processed(method)) {
                continue;
            }
 
            boolean status = false;
 
            // 必须包含指定的注解
            for (String annotation : MAPPING_ANNOTATIONS) {
                // 反复与运算, 只要包含一个 Mapping 注解. 那么这个方法就是我们需要处理的方法
                status = status || AnnotationHelper.getAnnotationValue(method.getAnnotations(), annotation) != null;
            }
 
            // 如果不包含指定的注解, 那么就不处理这个方法
            if (!status) {
                continue;
            }
 
            String methodName = method.getName();
 
            BaseCall context = new CallWeb();
            context.type = "CONTROLLER";
            context.className = className;
            context.methodName = methodName;
            context.context.put("CallType", "org.example.call.pojo.CallWeb");
            context.context.put("instance", "org.example.filter.support.SpringControllerFilterChain.INSTANCE");
            context.context.put("names", renderParamNames(method));
 
            ctClass.addMethod(CtNewMethod.copy(method, methodName + "$agent", ctClass, null));
            BaseTemplate baseTemplate = TemplateFactory.getTemplate(method.getReturnType() != CtClass.voidType);
            baseTemplate.context = context;
            method.setBody(baseTemplate.render());
        }
 
        return ctClass.toBytecode();
    }
 
}