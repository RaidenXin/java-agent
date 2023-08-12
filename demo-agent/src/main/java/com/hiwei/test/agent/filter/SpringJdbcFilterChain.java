package com.hiwei.test.agent.filter;
 
import com.hiwei.test.agent.proxy.ProxyConnection;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
 
public class SpringJdbcFilterChain implements FilterChain {
    private static final String[] driverClassNames = {
            "com.mysql.cj.jdbc.NonRegisteringDriver",
            "oracle.jdbc.driver.OracleDriver"
    };
 
    @Override
    public boolean isTargetClass(String className, CtClass ctClass) {
        for (String driverClassName : driverClassNames) {
            if (driverClassName.equals(className)) {
                return true;
            }
        }
        return false;
    }
 
    public static java.sql.Connection proxyConnection(java.sql.Connection connection) {
        return new ProxyConnection(connection);
    }
 
    @Override
    public byte[] processingAgentClass(ClassLoader loader, CtClass ctClass, String className) throws Exception {
        CtMethod connect = ctClass.getMethod("connect", "(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
        ctClass.addMethod(CtNewMethod.copy(connect, "connect$agent", ctClass, null));
 
        String sb = "{\n" +
                "    java.sql.Connection conn = connect$agent($$);\n"  +
                "    return com.hiwei.test.agent.filter.SpringJdbcFilterChain.proxyConnection(conn);\n" +
                "}\n";
        connect.setBody(sb);
        return ctClass.toBytecode();
    }
 
}