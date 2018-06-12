package com.loan_utils.radis;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.JedisClusterCRC16;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by chenchao on 2017/11/14.
 */
public class ScriptCaller {
    private final static Logger logger = LoggerFactory.getLogger(ScriptCaller.class);
    private static final ConcurrentMap<String, String> SHA_CACHE = new ConcurrentHashMap<>();

    private String script;

    private ScriptCaller(String script) {
        this.script = script;
    }

    public static ScriptCaller getInstance(String script) {
        return new ScriptCaller(script);
    }

    public Object call(JedisCluster connection, List<String> keys, List<String> argv) {
        try {
            if (keys == null || keys.isEmpty()) {
                return null;
            }
            String key = keys.get(0);
            if (key == null || script == null) {
                return null;
            }

            String sha = SHA_CACHE.get(script);
            if (Strings.isNullOrEmpty(sha) || !connection.scriptExists(key, sha)) {
                sha = connection.scriptLoad(script, key);
                SHA_CACHE.put(this.script, sha);
            }
            return connection.evalsha(sha, keys, argv);
        } catch (Exception e) {
            logger.error("Lua脚本失败" + script);
            logger.error("执行Redis Lua脚本失败", e);
            return null;
        }
    }
}
