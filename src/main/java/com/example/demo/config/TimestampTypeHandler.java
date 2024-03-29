package com.example.demo.config;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampTypeHandler implements TypeHandler<Timestamp> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Timestamp parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, parameter);
    }

    @Override
    public Timestamp getResult(ResultSet rs, String columnName) throws SQLException {
        // 将时间格式转换为时间戳
        String timeString = rs.getString(columnName);
        // 假设时间格式为 "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(timeString);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Timestamp getResult(ResultSet rs, int columnIndex) throws SQLException {
        // 与上面的方法类似，根据列索引获取结果并处理
        return null;
    }

    @Override
    public Timestamp getResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 与上面的方法类似，根据列索引获取结果并处理
        return null;
    }
}