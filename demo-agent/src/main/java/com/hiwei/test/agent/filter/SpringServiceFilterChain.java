package com.hiwei.test.agent.filter;

import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.BaseCall;
import com.hiwei.test.agent.call.CallService;
import com.hiwei.test.agent.call.CallSpan;
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

public class SpringServiceFilterChain extends AbstractFilterChain {

    private static final Logger LOGGER = LogManager.getLogger(SpringServiceFilterChain.class);
    /**
     * 实例
     */
    public static final SpringServiceFilterChain INSTANCE = new SpringServiceFilterChain();
    private static final Set<String> SERVICE_ANNOTATION = new HashSet<String>(){{
        add("org.springframework.stereotype.Service");
        add("org.springframework.stereotype.Component");
    }};

    @Override
    public boolean isTargetClass(String className, CtClass ctClass) {
        try {
            final Object[] annotations = ctClass.getAnnotations();
            return Stream.of(annotations)
                    .filter(a -> a instanceof Annotation)
                    .map(a -> ((Annotation) a).annotationType().getName())
                    .anyMatch(SERVICE_ANNOTATION::contains);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    @Override
    public void before(BaseCall context) {
        CallSpan.Span span = CallContext.createEntrySpan(null);
        context.span = span.toString();
        context.trace = CallContext.getTrace();
    }

    @Override
    public void finale(BaseCall context) {
        super.finale(context);
    }

    @Override
    public byte[] processingAgentClass(ClassLoader loader, CtClass ctClass, String className) throws Exception {
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for (CtMethod method : methods) {
            if (!processed(method)) {
                continue;
            }

            String methodName = method.getName();

            BaseCall context = new CallService();
            context.type = "SERVICE";
            context.className = className;
            context.methodName = methodName;
            context.context.put("CallType", "com.hiwei.test.agent.call.CallService");
            context.context.put("instance", "com.hiwei.test.agent.filter.SpringServiceFilterChain.INSTANCE");
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