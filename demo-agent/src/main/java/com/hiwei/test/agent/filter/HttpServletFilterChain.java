package com.hiwei.test.agent.filter;

import com.alibaba.fastjson.JSON;
import com.hiwei.test.agent.call.CallContext;
import com.hiwei.test.agent.call.BaseCall;
import com.hiwei.test.agent.call.CallServlet;
import com.hiwei.test.agent.call.CallSpan;
import com.hiwei.test.agent.template.BaseTemplate;
import com.hiwei.test.agent.template.TemplateFactory;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.hiwei.test.agent.common.Constant.RETURN_TYPE_NAME;

/**
 * 针对 HttpServlet 的请求拦截
 *
 * @author tameti
 */
public class HttpServletFilterChain extends AbstractFilterChain {

    private static final Logger LOGGER = LogManager.getLogger(HttpServletFilterChain.class);

    /**
     * 暴露实例出去
     */
    public static final HttpServletFilterChain INSTANCE = new HttpServletFilterChain();

    /**
     * web 入口
     */
    public static String servletClassName = "javax.servlet.http.HttpServlet";
 
    /**
     * 获取请求头信息
     *
     * @param request
     * @return
     */
    private Map<String, String> getHeader(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        Map<String, String> map = new HashMap<>();
        while (headers.hasMoreElements()) {
            String h = headers.nextElement();
            String v = request.getHeader(h);
            map.put(h, v);
        }
        return map;
    }
 
    /**
     * 获取响应头信息
     *
     * @param response
     * @return
     */
    private Map<String, String> getHeader(HttpServletResponse response) {
        Collection<String> headers = response.getHeaderNames();
        Map<String, String> map = new HashMap<>();
        headers.forEach(e -> map.put(e, response.getHeader(e)));
        return map;
    }
 
    @Override
    public void before(BaseCall context) {
        Object[] paramValues = (Object[]) context.context.get("values");
        HttpServletRequest request = (HttpServletRequest) paramValues[0];
 
        // 获取 span 和 trace
        String spanValue = request.getHeader("span");
        String traceValue = request.getHeader("trace");
        CallSpan.Span span = CallContext.createEntrySpan(spanValue);
        CallContext.setTrace(traceValue);
 
 
        String session = request.getSession().getId();
        String address = request.getRemoteAddr();
        String url = request.getRequestURI();
 
        // 请求头信息
        Map<String, String> header = getHeader(request);
 
        // 请求的方法类型
        String method = request.getMethod();
        CallServlet servlet = (CallServlet) context;
        servlet.trace = CallContext.getTrace();
        servlet.span = span.toString();
        servlet.session = session;
        servlet.address = address;
        servlet.url = url;
        servlet.method = method;
        servlet.params = JSON.toJSONString(request.getParameterMap());
        servlet.header = JSON.toJSONString(header);
        servlet.cookies = JSON.toJSONString(request.getCookies());
        servlet.thread = Thread.currentThread().getName();
    }
 
 
    @Override
    public void finale(BaseCall context) {
        CallServlet servlet = (CallServlet) context;
        Object[] paramValues = (Object[]) context.context.get("values");
        HttpServletResponse response = (HttpServletResponse) paramValues[1];
        servlet.status = response.getStatus();
        super.finale(servlet);
    }
 
    @Override
    public boolean isTargetClass(String className, CtClass ctClass) {
        return servletClassName.equals(className);
    }
 
 
    @Override
    public byte[] processingAgentClass(ClassLoader loader, CtClass ctClass, String className) throws Exception {
        String methodName = "service";
        CtMethod service = ctClass.getMethod(methodName, "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V");

        CallServlet context = new CallServlet();
        context.type = "SERVLET";
        context.className = servletClassName;
        context.context.put("CallType", "com.hiwei.test.agent.call.CallServlet");
        context.methodName = methodName;
        context.context.put("instance", "com.hiwei.test.agent.filter.HttpServletFilterChain.INSTANCE");
        context.context.put("names", "new String[] {\"req\", \"resp\"}");
        context.context.put(RETURN_TYPE_NAME, service.getReturnType().getName());
        CtMethod service$agent = CtNewMethod.copy(service, context.methodName + "$agent", ctClass, null);
        ctClass.addMethod(service$agent);
 
        BaseTemplate template = TemplateFactory.getTemplate(service.getReturnType() != CtClass.voidType);
        template.context = context;
        String templateValue = template.render();
        service.setBody(templateValue);
        return ctClass.toBytecode();
    }
 
 
}