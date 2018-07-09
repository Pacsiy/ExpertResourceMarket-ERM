/*
The MIT License (MIT)

Copyright (c) 2014 Manni Wood

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package edu.buaa.server.dataLayer.typehandlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.json.JSONArray;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Manni Wood
 */
@MappedTypes(JSONArray.class)
public class JSONTypeHandler extends BaseTypeHandler<JSONArray> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    JSONArray parameter, JdbcType jdbcType) throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        try {
            jsonObject.setValue(parameter.toString());
        } catch (Exception e) {
            jsonObject.setValue(null);
        }
        ps.setObject(i, jsonObject);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        try {
            return new JSONArray(rs.getString(columnName));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        try {
            return new JSONArray(rs.getString(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        try {
            return new JSONArray(cs.getString(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }
}