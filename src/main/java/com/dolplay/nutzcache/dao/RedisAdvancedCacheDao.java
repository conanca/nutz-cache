package com.dolplay.nutzcache.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.impl.PropertiesProxy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzcache.CacheConfig;
import com.dolplay.nutzcache.Order;

/**
 * Redis实现的高级缓存DAO
 * @author conanca
 *
 */
public class RedisAdvancedCacheDao extends RedisCacheDao implements AdvancedCacheDao {

	public RedisAdvancedCacheDao(PropertiesProxy config, JedisPool jedisPool) {
		super(config, jedisPool);
	}

	public void zAdd(String cacheKey, int seconds, double score, String item) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zadd(cacheKey, score, item);
			boolean isNew = !jedis.exists(cacheKey);
			if (isNew && seconds > 0) {
				jedis.expire(cacheKey, seconds);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void zAdd(String cacheKey, double score, String item) throws Exception {
		int timeout = config.getInt("LIST_CACHE_TIMEOUT", CacheConfig.DEFAULT_LIST_CACHE_TIMEOUT);
		zAdd(cacheKey, timeout, score, item);
	}

	public List<String> zQueryByRank(String cacheKey, long startIndex, long endIndex, Order order) throws Exception {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> valueSet = null;
			if (order.equals(Order.Asc)) {
				valueSet = jedis.zrange(cacheKey, startIndex, endIndex);
			} else {
				valueSet = jedis.zrevrange(cacheKey, startIndex, endIndex);
			}
			valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return valueList;
	}

	public List<String> zQueryByRank(String cacheKey, long startIndex, long endIndex) throws Exception {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> valueSet = null;
			valueSet = jedis.zrange(cacheKey, startIndex, endIndex);
			valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return valueList;
	}

	public List<String> zQueryByScore(String cacheKey, double minScore, double maxScore, Order order) throws Exception {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> valueSet = null;
			if (order.equals(Order.Asc)) {
				valueSet = jedis.zrangeByScore(cacheKey, minScore, maxScore);
			} else {
				valueSet = jedis.zrevrangeByScore(cacheKey, maxScore, minScore);
			}
			valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return valueList;
	}

	public List<String> zQueryByScore(String cacheKey, double minScore, double maxScore) throws Exception {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> valueSet = null;
			valueSet = jedis.zrangeByScore(cacheKey, minScore, maxScore);
			valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return valueList;
	}

	public List<String> zQueryAll(String cacheKey, Order order) throws Exception {
		return zQueryByRank(cacheKey, 0, -1, order);
	}

	public List<String> zQueryAll(String cacheKey) throws Exception {
		return zQueryByRank(cacheKey, 0, -1);
	}

	public void zDel(String cacheKey, String... items) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zrem(cacheKey, items);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void zDelByRank(String cacheKey, long startIndex, long endIndex) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zremrangeByRank(cacheKey, startIndex, endIndex);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	public void zDelByScore(String cacheKey, double minScore, double maxScore) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zremrangeByScore(cacheKey, minScore, maxScore);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}
}
