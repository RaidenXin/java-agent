package com.hiwei.test.agent.filter;
 
import com.codetool.common.AnnotationHelper;
import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.BaseCall;
import com.hiwei.test.agent.call.CallSpan;
import com.hiwei.test.agent.template.BaseTemplate;
import com.hiwei.test.agent.template.TemplateFactory;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class SpringServiceFilterChain extends AbstractFilterChain {
    public static final SpringServiceFilterChain INSTANCE = new SpringServiceFilterChain();
    private static final String serviceAnnotation = "@org.springframework.stereotype.Service";
 
    @Override
    public boolean isTargetClass(String className, CtClass ctClass) {
        try {
            Object[] annotations = ctClass.getAnnotations();
            String annotationValue = AnnotationHelper.getAnnotationValue(annotations, serviceAnnotation);
            return annotationValue != null;
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
 
            BaseCall context = new BaseCall();
            context.type = "SERVICE";
            context.className = className;
            context.methodName = methodName;
            context.context.put("CallType", "org.example.call.pojo.CallService");
            context.context.put("instance", "org.example.filter.support.SpringServiceFilterChain.INSTANCE");
            context.context.put("names", renderParamNames(method));
 
 
            ctClass.addMethod(CtNewMethod.copy(method, methodName + "$agent", ctClass, null));
            BaseTemplate baseTemplate = TemplateFactory.getTemplate(method.getReturnType() != CtClass.voidType);
            baseTemplate.context = context;
            method.setBody(baseTemplate.render());
        }
        return ctClass.toBytecode();
    }
}