package com.hiwei.test.agent.proxy;
 

 
import com.hiwei.test.agent.call.CallJdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
 
public class ProxyPreparedStatement extends ProxyStatement implements PreparedStatement {
    private PreparedStatement preparedStatement;
    private ArrayList<Object> parameters;
 
    ProxyPreparedStatement(PreparedStatement preparedStatement, CallJdbc context) {
        super(preparedStatement, context);
        try {
            parameters = new ArrayList<>();
            int parameterCount = preparedStatement.getParameterMetaData().getParameterCount();
            for (int i = 0; i < parameterCount; i++) {
                parameters.add(null);
            }
            context.context.put("parameters", parameters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.preparedStatement = preparedStatement;
    }
 
    private String getSql() {
        String sql = preparedStatement.toString();
        if (sql.contains(":")) {
            sql = sql.substring(sql.indexOf(":") + 2);
        }
        return sql;
    }
 
    @Override
    public ResultSet executeQuery() throws SQLException {
        queryBefore(getSql());
        ResultSet resultSet = preparedStatement.executeQuery();
        queryEnd(resultSet);
        return resultSet;
    }
 
    @Override
    public int executeUpdate() throws SQLException {
        executeUpdateBefore(getSql());
        int row = preparedStatement.executeUpdate();
        executeUpdateEnd(row);
        return row;
    }
 
    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameters.set(parameterIndex - 1, null);
        preparedStatement.setNull(parameterIndex, sqlType);
    }
 
    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBoolean(parameterIndex, x);
    }
 
    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setByte(parameterIndex, x);
    }
 
    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setShort(parameterIndex, x);
    }
 
    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setInt(parameterIndex, x);
    }
 
    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setLong(parameterIndex, x);
    }
 
    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setFloat(parameterIndex, x);
    }
 
    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setDouble(parameterIndex, x);
    }
 
    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBigDecimal(parameterIndex, x);
    }
 
    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setString(parameterIndex, x);
    }
 
    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBytes(parameterIndex, x);
    }
 
    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setDate(parameterIndex, x);
    }
 
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setTime(parameterIndex, x);
    }
 
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setTimestamp(parameterIndex, x);
    }
 
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setAsciiStream(parameterIndex, x, length);
    }
 
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setUnicodeStream(parameterIndex, x, length);
    }
 
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBinaryStream(parameterIndex, x, length);
    }
 
    @Override
    public void clearParameters() throws SQLException {
        parameters.clear();
        preparedStatement.clearParameters();
    }
 
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }
 
    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setObject(parameterIndex, x);
    }
 
    @Override
    public boolean execute() throws SQLException {
        executeBefore(getSql());
        boolean result = preparedStatement.execute();
        executeEnd(result);
        return result;
    }
 
    @Override
    public void addBatch() throws SQLException {
        preparedStatement.addBatch();
    }
 
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }
 
    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setRef(parameterIndex, x);
    }
 
    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBlob(parameterIndex, x);
    }
 
    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setClob(parameterIndex, x);
    }
 
    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setArray(parameterIndex, x);
    }
 
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return preparedStatement.getMetaData();
    }
 
    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setDate(parameterIndex, x, cal);
    }
 
    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setTime(parameterIndex, x, cal);
    }
 
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setTimestamp(parameterIndex, x, cal);
    }
 
    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        parameters.set(parameterIndex - 1, null);
        preparedStatement.setNull(parameterIndex, sqlType, typeName);
    }
 
    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setURL(parameterIndex, x);
    }
 
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return preparedStatement.getParameterMetaData();
    }
 
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setRowId(parameterIndex, x);
    }
 
    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        parameters.set(parameterIndex - 1, value);
        preparedStatement.setNString(parameterIndex, value);
    }
 
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        parameters.set(parameterIndex - 1, value);
        preparedStatement.setNCharacterStream(parameterIndex, value, length);
    }
 
    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        parameters.set(parameterIndex - 1, value);
        preparedStatement.setNClob(parameterIndex, value);
    }
 
    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setClob(parameterIndex, reader, length);
    }
 
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        parameters.set(parameterIndex - 1, inputStream);
        preparedStatement.setBlob(parameterIndex, inputStream, length);
    }
 
    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setNClob(parameterIndex, reader, length);
    }
 
    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        parameters.set(parameterIndex - 1, xmlObject);
        preparedStatement.setSQLXML(parameterIndex, xmlObject);
    }
 
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }
 
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setAsciiStream(parameterIndex, x, length);
    }
 
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBinaryStream(parameterIndex, x, length);
    }
 
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }
 
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setAsciiStream(parameterIndex, x);
    }
 
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        parameters.set(parameterIndex - 1, x);
        preparedStatement.setBinaryStream(parameterIndex, x);
    }
 
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setCharacterStream(parameterIndex, reader);
    }
 
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        parameters.set(parameterIndex - 1, value);
        preparedStatement.setNCharacterStream(parameterIndex, value);
    }
 
    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setClob(parameterIndex, reader);
    }
 
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        parameters.set(parameterIndex - 1, inputStream);
        preparedStatement.setBlob(parameterIndex, inputStream);
    }
 
    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        parameters.set(parameterIndex - 1, reader);
        preparedStatement.setNClob(parameterIndex, reader);
    }
 
}