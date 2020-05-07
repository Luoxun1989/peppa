package cn.northbynorthwest.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * “Go Further进无止境” <br>
 * 〈使用druid数据库连接池〉
 *
 * @author Luoxun
 * @create 2020/5/6
 * @since 1.0.0
 */
public class DruidDBCPoolUtil {

    private static DruidDataSource dataSource = null;

    //1.初始化Druid连接池
    static {
        try {
            Properties properties = new Properties();
            //通过类加载器加载配置文件
            InputStream inputStream = DruidDBCPoolUtil.class.getClassLoader().getResourceAsStream("druid.properties");
            properties.load(inputStream);
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * @Description:获取连接
     * @Date: 2020/5/6 15:58
     * @param: []
     * @return: com.alibaba.druid.pool.DruidPooledConnection
     **/
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    public static void close(Connection conn) {
        close(conn, null, null);
    }
}

