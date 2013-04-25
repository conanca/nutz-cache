package com.dolplay.nutzcache.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.alibaba.fastjson.JSON;

/**
 * Redis实现的缓存DAO
 * @author conanca
 *
 */
public class RedisCacheDao implements CacheDao {
	protected ShardedJedisPool jedisPool;

	public RedisCacheDao(ShardedJedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public void set(String cacheKey, Object cacheValue) throws Exception {
		set(cacheKey, -1, cacheValue);
	}

	public void set(String cacheKey, int timeout, Object cacheValue) throws Exception {
		ShardedJedis jedis = null;
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
		ShardedJedis jedis = null;
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
		ShardedJedis jedis = null;
		long count = 0;
		try {
			jedis = jedisPool.getResource();
			for (String cacheKey : cacheKeys) {
				count += jedis.del(cacheKey);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return count;
	}

	public boolean expire(String cacheKey, int seconds) throws Exception {
		ShardedJedis jedis = null;
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
		ShardedJedis jedis = null;
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
		ShardedJedis jedis = null;
		Set<String> keySet = null;
		try {
			jedis = jedisPool.getResource();
			Collection<Jedis> jedisColl = jedis.getAllShards();
			keySet = new HashSet<String>();
			for (Jedis aJedis : jedisColl) {
				keySet.addAll(aJedis.keys(pattern));
			}
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
		ShardedJedis jedis = null;
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
