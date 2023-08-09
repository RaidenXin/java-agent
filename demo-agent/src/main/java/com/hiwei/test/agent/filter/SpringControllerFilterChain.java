package com.hiwei.test.agent.filter;
 

import com.hiwei.test.agent.AgentApplication;
import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.BaseCall;
import com.hiwei.test.agent.call.CallSpan;
import com.hiwei.test.agent.call.CallWeb;
import com.hiwei.test.agent.template.BaseTemplate;
import com.hiwei.test.agent.template.TemplateFactory;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.hiwei.test.agent.common.Constant.RETURN_TYPE_NAME;

public class SpringControllerFilterChain extends AbstractFilterChain {

    /**
     * 实例
     */
    public static final SpringControllerFilterChain INSTANCE = new SpringControllerFilterChain();
    
    // 如果类上包含这两个注解中的任意一个则进行插桩
    private static final Set<String> CONTROLLER_ANNOTATIONS = new HashSet<String>() {{
        add("org.springframework.web.bind.annotation.RestController");
        add("org.springframework.stereotype.Controller");
    }};
 
    // 如果方法上包含 6 个注解中的任意一个则进行插桩
    private static final Set<String> MAPPING_ANNOTATIONS = new HashSet<String>() {{
        add("org.springframework.web.bind.annotation.RequestMapping");
        add("org.springframework.web.bind.annotation.GetMapping");
        add("org.springframework.web.bind.annotation.PostMapping");
        add("org.springframework.web.bind.annotation.PutMapping");
        add("org.springframework.web.bind.annotation.DeleteMapping");
        add("org.springframework.web.bind.annotation.PatchMapping");
    }};

    private static final Logger LOGGER = LogManager.getLogger(AgentApplication.class);

 
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
                return Stream.of(ctClass.getAnnotations())
                        .filter(a -> a instanceof Annotation)
                        .anyMatch(a -> CONTROLLER_ANNOTATIONS.contains(((Annotation) a).annotationType().getName()));
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
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

            // 必须包含指定的注解 只要包含一个 Mapping 注解. 那么这个方法就是我们需要处理的方法
            boolean status = Stream.of(ctClass.getAnnotations())
                    .filter(a -> a instanceof Annotation)
                    .anyMatch(a -> MAPPING_ANNOTATIONS.contains(((Annotation) a).annotationType().getName()));;
 
            // 如果不包含指定的注解, 那么就不处理这个方法
            if (!status) {
                continue;
            }
 
            String methodName = method.getName();
 
            BaseCall context = new CallWeb();
            context.type = "CONTROLLER";
            context.className = className;
            context.methodName = methodName;
            context.context.put("CallType", "com.hiwei.test.agent.call.CallWeb");
            context.context.put("instance", "com.hiwei.test.agent.filter.SpringControllerFilterChain.INSTANCE");
            context.context.put("names", renderParamNames(method));
            context.context.put(RETURN_TYPE_NAME, method.getReturnType().getName());
 
            ctClass.addMethod(CtNewMethod.copy(method, methodName + "$agent", ctClass, null));
            BaseTemplate baseTemplate = TemplateFactory.getTemplate(method.getReturnType() != CtClass.voidType);
            baseTemplate.context = context;
            final String templateValue = baseTemplate.render();
            LOGGER.info(templateValue);
            method.setBody(templateValue);
        }
 
        return ctClass.toBytecode();
    }
 
}