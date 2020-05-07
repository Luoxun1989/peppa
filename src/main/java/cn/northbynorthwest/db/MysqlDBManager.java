package cn.northbynorthwest.db;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * “Go Further进无止境” <br>
 * 〈实现mysql数据库基本数据操作〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
public class MysqlDBManager {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
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
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    LoadPropertiesFileUtil.getStringValue(Constant.JDBC_URL),
                    LoadPropertiesFileUtil.getStringValue(Constant.JDBC_USER),
                    LoadPropertiesFileUtil.getStringValue(Constant.JDBC_PASSWORD));
        } catch (SQLException e) {
            logger.error("获取mysql数据库连接失败！");
        }
        return conn;
    }

    /*
     * @Description:插入一条数据
     * @Date: 2020/5/7 9:54
     * @param: [conn, sql]
     * @return: void
     **/
    public void insert(Connection conn, String sql, Object[] obj) {
        //TODO
        PreparedStatement ps = null;
        try {
            //将sql语句提交到数据库进行预编译
            ps = conn.prepareStatement(sql);
            int i = ps.executeUpdate();
            if (i > 0) {
                logger.info("添加成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    /*
     * @Description:批量插入数据
     * @Date: 2020/5/7 10:24
     * @param: [conn, sql]
     * @return: void
     **/
    public void insertBatch(Connection conn, String sql, Object[] obj) {
    //TODO
    }

    /*
     * @Description:数据查询操作 结果回传
     * @Date: 2020/5/7 10:23
     * @param: [conn, sql, handler, obj]
     * @return: java.lang.Object
     **/
    public Object query(Connection conn, String sql, ResultSetHandler handler, Object[] obj) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //创建PreparedStatement对象
            ps = conn.prepareStatement(sql);
            //为查询语句设置参数
            setParameter(ps, obj);
            //获得ResultSet结果集
            rs = ps.executeQuery();
            //返回对象
            return handler.callback(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("数据库操作异常", e);
        } finally {
            //关闭连接
            close(conn, ps, rs);
            logger.debug("释放资源成功");
        }
        return null;
    }

    /*
     * @Description:参数设置
     * @Date: 2020/5/7 10:22
     * @param: [ps, obj]
     * @return: void
     **/
    private void setParameter(PreparedStatement ps, Object[] obj) throws SQLException {
        if (obj != null && obj.length > 0) {
            //循环设置参数
            for (int i = 0; i < obj.length; i++) {
                ps.setObject(i + 1, obj[i]);
            }
        }
    }

    /*
     * @Description:数据更新操作
     * @Date: 2020/5/7 10:23
     * @param: [conn, sql]
     * @return: void
     **/
    public void update(Connection conn, String sql, Object[] obj) {
        PreparedStatement ps = null;
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            setParameter(ps, obj);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                logger.info("更新成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public void delete(Connection conn, String sql, Object[] obj) {
        update(conn, sql, obj);
    }

    /*
     * @Description:关闭数据库连接，注意关闭的顺序,最后打开的最先关闭
     * @Date: 2020/5/7 10:31
     * @param: [conn, pstmt, rs]
     * @return: void
     **/
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
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
}
