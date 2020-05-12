package cn.northbynorthwest.db;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/5/12
 * @since 1.0.0
 */
public class JedisPoolManager {
    /**
     * 双锁机制，安全且在多线程情况下能保持高性能
     */
    private static volatile JedisPool jedisPool = null;
    private static final int TIMEOUT = 2000;
    private JedisPoolManager() {
    }

    public static JedisPool getRedisPool() {
        if (null == jedisPool) {
            synchronized (JedisPoolManager.class) {
                if (null == jedisPool) {
                    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
                    poolConfig.setMaxTotal(LoadPropertiesFileUtil.getIntValue(Constant.JEDISPOOL_MAXACTIVE));
                    poolConfig.setMaxIdle(LoadPropertiesFileUtil.getIntValue(Constant.JEDISPOOL_MAXIDLE));
                    poolConfig.setMaxWaitMillis(LoadPropertiesFileUtil.getIntValue(Constant.JEDISPOOL_MAXWAIT));
                    poolConfig.setTestOnBorrow(LoadPropertiesFileUtil.getBooleanValue(Constant.JEDISPOOL_TESTONBORROW));
                    poolConfig.setTestOnReturn(LoadPropertiesFileUtil.getBooleanValue(Constant.JEDISPOOL_TESTONRETURN));
                    jedisPool = new JedisPool(poolConfig,
                            LoadPropertiesFileUtil.getStringValue(Constant.JEDISPOOL_HOST),
                            LoadPropertiesFileUtil.getIntValue(Constant.JEDISPOOL_PORT),
                            TIMEOUT);
                }
            }
        }
        return jedisPool;
    }

}
