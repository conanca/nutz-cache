package com.dolplay.nutzcache.dao;

import java.util.Set;

import org.nutz.ioc.impl.PropertiesProxy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.dolplay.nutzcache.CacheConfig;

/**
 * Redis实现的缓存DAO
 * @author conanca
 *
 */
public class RedisCacheDao implements CacheDao {
	protected PropertiesProxy config;
	protected JedisPool jedisPool;

	public RedisCacheDao(PropertiesProxy config, JedisPool jedisPool) {
		this.config = config;
		this.jedisPool = jedisPool;
	}

	public void set(String cacheKey, Object cacheValue) throws Exception {
		int timeout = config.getInt("STANDARD_CACHE_TIMEOUT", CacheConfig.DEFAULT_STANDARD_CACHE_TIMEOUT);
		set(cacheKey, timeout, cacheValue);
	}

	public void set(String cacheKey, int timeout, Object cacheValue) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (timeout <= 0) {
				jedis.set(cacheKey, JSON.toJSONString(cacheValue));
			} else {
				jedis.setex(cacheKey, timeout, JSON.toJSONString(cacheValue));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public String get(String cacheKey) throws Exception {
		String valueJson = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			valueJson = jedis.get(cacheKey);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}

		}
		return valueJson;
	}

	public <T> T get(String cacheKey, Class<T> type) throws Exception {
		return JSON.parseObject(get(cacheKey), type);
	}

	public long remove(String... cacheKeys) throws Exception {
		Jedis jedis = null;
		Long count = null;
		try {
			jedis = jedisPool.getResource();
			count = jedis.del(cacheKeys);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return count == null ? 0 : count.longValue();
	}

	public boolean expire(String cacheKey, int seconds) throws Exception {
		Jedis jedis = null;
		long success = 0;
		try {
			jedis = jedisPool.getResource();
			success = jedis.expire(cacheKey, seconds);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return success == 1 ? true : false;
	}

	public boolean exists(String cacheKey) throws Exception {
		Jedis jedis = null;
		boolean isExist = false;
		try {
			jedis = jedisPool.getResource();
			isExist = jedis.exists(cacheKey);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return isExist;
	}

	public Set<String> keySet(String pattern) throws Exception {
		Jedis jedis = null;
		Set<String> keySet = null;
		try {
			jedis = jedisPool.getResource();
			keySet = jedis.keys(pattern);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return keySet;
	}

	public String keyType(String key) throws Exception {
		Jedis jedis = null;
		String keyType = null;
		try {
			jedis = jedisPool.getResource();
			keyType = jedis.type(key);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return keyType;
	}
}
