package cn.northbynorthwest.utils;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.db.CustomDBConnectionPool;

import java.sql.SQLException;

public class CustomDBCPoolUtil {
    private static CustomDBConnectionPool connPool = null;

    public static CustomDBConnectionPool getInstance() {
        synchronized (CustomDBCPoolUtil.class){
            if (null == connPool){
                connPool = new CustomDBConnectionPool(
                        LoadPropertiesFileUtil.getStringValue(Constant.JDBC_DRIVER),
                        LoadPropertiesFileUtil.getStringValue(Constant.JDBC_URL),
                        LoadPropertiesFileUtil.getStringValue(Constant.JDBC_USER),
                        LoadPropertiesFileUtil.getStringValue(Constant.JDBC_PASSWORD));
                try {
                    connPool.createPool();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return connPool;
    }

    private CustomDBCPoolUtil() {
    }
}
