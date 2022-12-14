package com.crazymaker.springcloud.common.jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class JdbcUtil
{

    public static void select(Connection conn , String name) throws ClassNotFoundException, SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(name);
            pstmt.setString(1, name);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                System.out.printf("id: %s, name: %s, balance: %s\n",
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getLong("balance"));
            }
            resultSet.close();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            conn.close();
        }
    }

    public Map<String,Object> resultToList(ResultSet set) throws SQLException {
        Map<String,Object> map = new HashMap<String,Object>();

        ResultSetMetaData rsmd =set.getMetaData();
        int count = rsmd.getColumnCount();

        //先生成几个list对象
        @SuppressWarnings("unchecked")
        List<String> headLists = new ArrayList<>();

        for (int i=0;i<count;i++) {
            headLists.add(rsmd.getColumnName(i+1));
        }


        /**
         * 这里是获取的一条一条
         */
        while(set.next()){
            for(int i=0 ;i<headLists.size();i++){
                String name = headLists.get(i);
               map.put(name,set.getString(headLists.get(i)));
            }
        }




        return map;
    }
}
