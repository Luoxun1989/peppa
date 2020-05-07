package cn.northbynorthwest.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 此接口的实现将 ResultSet 转换为其他对象
 * T： 目标类型（类型参数），也就是 ResultSet 转换为的对象的类型
 */
public interface ResultSetHandler<T> {
    /**
     * 方法说明：将 ResultSet 转换为一个对象
     *
     * rs： 要转换的 ResultSet
     * T： 返回用 ResultSet 数据初始化的对象
     * 如果 ResultSet 包含0行，那么实现返回 null 也是合法的
     * 数据库访问出错将会抛出 SQLException 异常
     */
    <T> T callback(ResultSet rs) throws SQLException;
}
