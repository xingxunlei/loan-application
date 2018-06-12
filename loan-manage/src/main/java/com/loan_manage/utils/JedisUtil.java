package com.loan_manage.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
    private final static Logger logger =   Logger.getLogger(JedisUtil.class);
    private static JedisPool pool;

    private JedisUtil() {
    }

    static {
        InputStream is = null;
        try {
            Properties constant = new Properties();
            is = JedisUtil.class.getClassLoader().getResourceAsStream("redis.properties");
            if (is != null) {
                constant.load(is);
            }
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxIdle(Integer.valueOf(constant.getProperty("redis.maxIdle", "100")));
            poolConfig.setMaxTotal(Integer.valueOf(constant.getProperty("redis.maxTotal", "100")));
            poolConfig.setTimeBetweenEvictionRunsMillis(-1);
            poolConfig.setTestOnBorrow(true);
            String host = constant.getProperty("redis.host");
            int port = Integer.valueOf(constant.getProperty("redis.port"));
            int timeout = 24 * 60 * 60;
            if(Detect.notEmpty(constant.getProperty("redis.timeout"))){
                timeout = Integer.valueOf(constant.getProperty("redis.timeout"));
            }
            String pwd = constant.getProperty("redis.pass");
            int db = Integer.valueOf(constant.getProperty("redis.db", "0"));
            if (pwd == null || pwd.isEmpty()) {
                pool = new JedisPool(poolConfig, host, port, timeout);
            } else {
                pool = new JedisPool(poolConfig, host, port, timeout, pwd, db);
            }
        } catch (IOException e) {
            logger.error("读取redis配置文件出错", e);
        } catch (Exception e) {
            logger.error("JedisUtil init error", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("关闭redis配置文件流出错", e);
                }
            }
        }
    }

    public static JedisPool getJedisPool() {
        return pool;
    }

    public static void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("set error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * 往KEY关联的SET集合增加成员
     *
     * @param key   Redis里面实际的KEY
     * @param value 要增加的成员
     */
    public static void sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("set cache error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * 判断KEY关联的SET集合是否存在对应的成员
     *
     * @param key   Redis里面实际的KEY
     * @param value 要查找的成员
     */
    public static boolean sismember(String key, String value) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = pool.getResource();
            flag = jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("set cache error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return flag;
    }

    public static long srem(String key, String value) {
        Jedis jedis = null;
        long flag = 0;
        try {
            jedis = pool.getResource();
            flag = jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("set cache error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return flag;
    }

    public static void set(String key, String value, int expireTimeInSec) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, expireTimeInSec, value);
        } catch (Exception e) {
            logger.error("set ex error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static boolean setObj(String key, Serializable obj) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, obj2String(obj));
            return true;
        } catch (Exception e) {
            logger.error("setObj error.", e);
            pool.returnBrokenResource(jedis);
            return false;
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static boolean setObj(String key, Serializable obj, int seconds) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, seconds, obj2String(obj));
            return true;
        } catch (Exception e) {
            logger.error("setObj ex error.", e);
            pool.returnBrokenResource(jedis);
            return false;
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            logger.error("getkey error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return value;
    }

    public static Object getObj(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String val = jedis.get(key);
            if (val == null) {
                return null;
            } else {
                return str2Object(val);
            }
        } catch (Exception e) {
            logger.error("getobj error.", e);
            pool.returnBrokenResource(jedis);
            return null;
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static String obj2String(Serializable obj) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(obj);
        return out.toString("ISO-8859-1");
    }

    public static Object str2Object(String str) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        return ois.readObject();
    }

    @SuppressWarnings("unchecked")
	public static <T extends Serializable> void push(String name, T... ts) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            for (T t : ts)
                jedis.lpush(name, JedisUtil.obj2String(t));
        } catch (Exception e) {
            logger.error("push error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static long lpush(String name, String json) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(name, json);
        } catch (Exception e) {
            logger.error("push error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return -1;
    }

    public static <T extends Serializable> void push(String name, Collection<T> collection) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            for (T t : collection)
                jedis.lpush(name, JedisUtil.obj2String(t));
        } catch (Exception e) {
            logger.error("push collection error.", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> pop(String name, int size) {
        if (size < 1) {
            return null;
        }

        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = pool.getResource();
            if (jedis.exists(name)) {
                String value = jedis.rpop(name);
                for (int i = 1; i <= size && value != null; i++) {
                    list.add((T) JedisUtil.str2Object(value));
                    value = jedis.rpop(name);
                }
            }
        } catch (Exception e) {
            logger.error("pop ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> lrange(String name, int start, int end) {
        Jedis jedis = null;
        List<T> list = new ArrayList<T>();
        try {
            jedis = pool.getResource();
            if (jedis.exists(name)) {
                List<String> strList = jedis.lrange(name, start, end);
                int size = strList == null ? 0 : strList.size();
                for (int i = 0; i < size; i++) {
                    list.add((T) JedisUtil.str2Object(strList.get(i)));
                }
            }
        } catch (Exception e) {
            logger.error("lrange ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }

        return list;
    }

    public static long length(String name) {
        Jedis jedis = null;
        long length = 0;
        try {
            jedis = pool.getResource();
            length = jedis.llen(name);
        } catch (Exception e) {
            logger.error("pop ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }

        return length;
    }

    public static void lrem(String name, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.lrem(name, count, "");
        } catch (Exception e) {
            logger.error("pop ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static void del(String name) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(name);
        } catch (Exception e) {
            logger.error("pop ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public static long zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.error("zadd ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }

    public static long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            logger.error("zadd ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }

    public static long zremrangeByRank(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            logger.error("zremrangeByRank ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }


    public static Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("zrang ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    public static long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("zcard ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }

    public static long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("hset ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }

    public static String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("hget ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    public static JSONArray hgetAll(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Map<String,String> map = jedis.hgetAll(key);
            JSONArray array = new JSONArray();
            for(Map.Entry entry: map.entrySet()) {
                String str = entry.getValue().toString();
                JSONObject object = JSON.parseObject(str);
                array.add(object);
            }
            return array;
        } catch (Exception e) {
            logger.error("hgetAll ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    public static Long hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key, field);
        } catch (Exception e) {
            logger.error("hget ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    /**
     * 设置key的过期时间
     * @param key key
     * @param second 过期时间，单位：秒
     */
    public static void setEcpire(String key,int second){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.expire(key,second);
        } catch (Exception e) {
            logger.error("hget ", e);
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }

    }
}
