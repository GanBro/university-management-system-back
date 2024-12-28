package com.wubo.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wubo.api.entity.Meta;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 类型转换器
 */
public class MetaTypeHandler extends BaseTypeHandler<Meta> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Meta parameter, JdbcType jdbcType) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        String json = null;
        try {
            if (Objects.nonNull(parameter)) {
                json = om.writeValueAsString(parameter);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ps.setString(i, json);
    }

    @Override
    public Meta getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        ObjectMapper om = new ObjectMapper();
        Meta meta = null;
        try {
            if (Objects.nonNull(json)) {
                meta = om.readValue(json, Meta.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return meta;
    }

    @Override
    public Meta getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        ObjectMapper om = new ObjectMapper();
        Meta meta = null;
        try {
            if (Objects.nonNull(json)) {
                meta = om.readValue(json, Meta.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return meta;
    }

    @Override
    public Meta getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        ObjectMapper om = new ObjectMapper();
        Meta meta = null;
        try {
            if (Objects.nonNull(json)) {
                meta = om.readValue(json, Meta.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return meta;
    }
}
