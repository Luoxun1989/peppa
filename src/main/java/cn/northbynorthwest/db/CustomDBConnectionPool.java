package cn.northbynorthwest.db;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * “Go Further进无止境” <br>
 * 〈自定义数据库连接池〉
 *
 * @author Luoxun
 * @create 2020/5/6
 * @since 1.0.0
 */
@Getter
@Setter
public class CustomDBConnectionPool {
    private String jdbcDriver = ""; // 数据库驱动
    private String dbUrl = ""; // 数据 URL
    private String dbUsername = ""; // 数据库用户名
    private String dbPassword = ""; // 数据库用户密码
    private String testTable = ""; // 测试连接是否可用的测试表名，默认没有测试表

    private int initialConnections = 10; // 连接池的初始大小
    private int incrementalConnections = 5;// 连接池自动增加的大小
    private int maxConnections = 50; // 连接池最大的大小
    private Vector connections = null; // 存放连接池中数据库连接的向量 , 初始时为 null


    public CustomDBConnectionPool(String jdbcDriver, String dbUrl, String dbUsername, String dbPassword) {
        this.jdbcDriver = jdbcDriver;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public synchronized void createPool() throws SQLException {
        if (this.maxConnections < 1) {
            System.err.println("max Connections < 1");
            return;
        }
        // 确保连接池没有创建
        // 如果连接池己经创建了，保存连接的向量 connections 不会为空
        if (connections != null) {
            return; // 如果己经创建，则返回
        }
        // 实例化 JDBC Driver 中指定的驱动类实例
        Driver driver = null;
        try {
            driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DriverManager.registerDriver(driver); // 注册 JDBC 驱动程序
        // 创建保存连接的向量 , 初始时有 0 个元素
        connections = new Vector();
//        connections = Collections.synchronizedList();
        // 根据 initialConnections 中设置的值，创建连接。
        createBatchConnections(this.initialConnections);
        // System.out.println(" 数据库连接池创建成功！ ");
    }

    private void createBatchConnections(int initialConnections) throws SQLException {
        for (int i = 0; i < initialConnections; i++) {
            if (this.connections.size() >= this.maxConnections) {
                break;
            }
            try {
                connections.addElement(new PooledConnection(createOneConnection()));
            } catch (SQLException e) {
                System.err.println(" 创建数据库连接失败！ " + e.getMessage());
                throw new SQLException();
            }
        }
        // System.out.println(" 数据库连接己创建 ......");
    }

    private Connection createOneConnection() throws SQLException {
        // 创建一个数据库连接
        Connection conn = DriverManager.getConnection(dbUrl, dbUsername,
                dbPassword);
        // 如果这是第一次创建数据库连接，即检查数据库，获得此数据库允许支持的最大客户连接数目
        // connections.size()==0 表示目前没有连接己被创建
        if (connections.size() == 0) {
            DatabaseMetaData metaData = conn.getMetaData();
            int driverMaxConnections = metaData.getMaxConnections();
            // 数据库返回的 driverMaxConnections 若为 0 ，表示此数据库没有最大连接限制，或数据库的最大连接限制不知道
            // driverMaxConnections 为返回的一个整数，表示此数据库允许客户连接的数目
            // 如果连接池中设置的最大连接数量大于数据库允许的连接数目 , 则置连接池的最大连接数目为数据库允许的最大数目
            if (driverMaxConnections > 0
                    && this.maxConnections > driverMaxConnections) {
                this.maxConnections = driverMaxConnections;
            }
        }
        return conn; // 返回创建的新的数据库连接
    }

    public synchronized Connection getConnection() throws SQLException {
        // 确保连接池己被创建
        if (connections == null) {
            return null; // 连接池还没创建，则返回 null
        }
        Connection conn = getFreeConnection(); // 获得一个可用的数据库连接
        // 如果目前没有可以使用的连接，即所有的连接都在使用中
        while (conn == null) {
            // 等一会再试
            // System.out.println("Wait");
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conn = getFreeConnection(); // 重新再试，直到获得可用的连接，如果
            // getFreeConnection() 返回的为 null
            // 则表明创建一批连接后也不可获得可用连接
        }
        return conn;// 返回获得的可用的连接
    }

    private Connection getFreeConnection() throws SQLException {
        // 从连接池中获得一个可用的数据库连接
        Connection conn = findFreeConnection();
        if (conn == null) {
            // 如果目前连接池中没有可用的连接
            // 创建一些连接
            createBatchConnections(this.incrementalConnections);
            // 重新从池中查找是否有可用连接
            conn = findFreeConnection();
            if (conn == null) {
                // 如果创建连接后仍获得不到可用的连接，则返回 null
                return null;
            }
        }
        return conn;

    }

    private Connection findFreeConnection() {
        Connection conn = null;
        PooledConnection pConn;
        // 获得连接池向量中所有的对象
        Enumeration enumerate = connections.elements();
        // 遍历所有的对象，看是否有可用的连接
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (!pConn.isBusy()) {
                // 如果此对象不忙，则获得它的数据库连接并把它设为忙
                conn = pConn.getConnection();
                pConn.setBusy(true);
                // 测试此连接是否可用
                if (!testConnection(conn)) {
                    // 如果此连接不可再用了，则创建一个新的连接，
                    // 并替换此不可用的连接对象，如果创建失败，返回 null
                    try {
                        conn = createOneConnection();
                    } catch (SQLException e) {
                        System.out.println(" 创建数据库连接失败！ " + e.getMessage());
                        return null;
                    }
                    pConn.setConnection(conn);
                }
                break; // 己经找到一个可用的连接，退出
            }
        }
        return conn;// 返回找到到的可用连接
    }

    private boolean testConnection(Connection conn) {
        try {
            // 判断测试表是否存在
            if (testTable.equals("")) {
                // 如果测试表为空，试着使用此连接的 setAutoCommit() 方法
                // 来判断连接否可用（此方法只在部分数据库可用，如果不可用 ,
                // 抛出异常）。注意：使用测试表的方法更可靠
                conn.setAutoCommit(true);
            } else {// 有测试表的时候使用测试表测试
                // check if this connection is valid
                Statement stmt = conn.createStatement();
                stmt.execute("select count(*) from " + testTable);
            }
        } catch (SQLException e) {
            // 上面抛出异常，此连接己不可用，关闭它，并返回 false;
            closeConnection(conn);
            return false;
        }
        // 连接可用，返回 true
        return true;
    }

    private void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(" 关闭数据库连接出错： " + e.getMessage());
        }
    }


    /**
     * 刷新连接池中所有的连接对象
     */

    public synchronized void refreshConnections() throws SQLException {
        // 确保连接池己创新存在
        if (connections == null) {
            System.err.println(" 连接池不存在，无法刷新 !");
            return;
        }
        PooledConnection pConn;
        Enumeration enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            // 获得一个连接对象
            pConn = (PooledConnection) enumerate.nextElement();
            // 如果对象忙则等 5 秒 ,5 秒后直接刷新
            if (pConn.isBusy()) {
                try {
                    wait(5000); // 等 5 秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 关闭此连接，用一个新的连接代替它。
            closeConnection(pConn.getConnection());
            pConn.setConnection(createOneConnection());
            pConn.setBusy(false);
        }
    }

    /**
     * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
     */

    public void returnConnection(Connection conn) {
        // 确保连接池存在，如果连接没有创建（不存在），直接返回
        if (connections == null) {
            System.err.println(" 连接池不存在，无法返回此连接到连接池中 !");
            return;
        }
        PooledConnection pConn = null;
        Enumeration enumerate = connections.elements();
        // 遍历连接池中的所有连接，找到这个要返回的连接对象
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            // 先找到连接池中的要返回的连接对象
            if (conn == pConn.getConnection()) {
                // 找到了 , 设置此连接为空闲状态
                pConn.setBusy(false);
                break;
            }
        }
    }

    /*
     * @Description:关闭单个数据库连接
     * @Date: 2020/5/6 18:22
     * @param: [conn, stmt, rs]
     * @return: void
     **/
    public void close(Connection connection, Statement statement, ResultSet resultSet) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    class PooledConnection {
        Connection connection = null;// 数据库连接
        boolean busy = false; // 此连接是否正在使用的标志，默认没有正在使用

        // 构造函数，根据一个 Connection 构告一个 PooledConnection 对象
        public PooledConnection(Connection connection) {
            this.connection = connection;
        }

        // 获得对象连接是否忙
        public boolean isBusy() {
            return busy;
        }

        // 设置对象的连接正在忙
        public void setBusy(boolean busy) {
            this.busy = busy;
        }

        public Connection getConnection() {
            return this.connection;
        }

        public void setConnection(Connection conn) {
            this.connection = conn;
        }
    }
}
