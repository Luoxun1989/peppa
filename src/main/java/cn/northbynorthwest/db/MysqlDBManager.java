package cn.northbynorthwest.db;

import cn.northbynorthwest.constants.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * “Go Further进无止境” <br>
 * 〈实现数据库连接池〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
public class MysqlDBManager {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.err.println("加载mysql数据库驱动失败！");
        }
    }
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 获取Connection
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Constant.mysqlUrl,Constant.mysqlUser, Constant.mysqlPassword);
        } catch (SQLException e) {
            logger.error("获取mysql数据库连接失败！");
        }
        return conn;
    }
}
