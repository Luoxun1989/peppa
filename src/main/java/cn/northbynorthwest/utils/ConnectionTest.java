package cn.northbynorthwest.utils;

import cn.northbynorthwest.db.CustomDBConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * “Go Further进无止境” <br>
 * 〈数据库连接池测试〉
 *
 * @author Luoxun
 * @create 2020/5/6
 * @since 1.0.0
 */
public class ConnectionTest {
    public static void main(String[] args) {
        druidTest();
        customTest();
    }
    public static void druidTest(){
        long start = System.currentTimeMillis();
        // 循环测试100次数据库连接
        for (int i = 0; i < 100; i++) {
            Connection conn = DruidDBCPoolUtil.getConnection(); // 从连接库中获取一个可用的连接
            Statement stmt ;
            try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("");
                while (rs.next()) {
                    String name = rs.getString("name");
                }
                DruidDBCPoolUtil.close(conn,stmt,rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("经过100次的循环调用，使用连接池花费的时间:"+ (System.currentTimeMillis() - start) + "ms");
    }
    public static void customTest(){
        long start = System.currentTimeMillis();
        CustomDBConnectionPool connPool = CustomDBCPoolUtil.getInstance();
        // 循环测试100次数据库连接
        for (int i = 0; i < 100; i++) {
            try {
                Connection conn = connPool.getConnection(); // 从连接库中获取一个可用的连接
                Statement stmt ;
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("");
                while (rs.next()) {
                    String name = rs.getString("name");
                }
                connPool.close(conn,stmt,rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("经过100次的循环调用，使用连接池花费的时间:"+ (System.currentTimeMillis() - start) + "ms");
    }
}
