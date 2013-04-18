package com.dolplay.nutzcache.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.dolplay.nutzcache.type.Order;

/**
 * Redis实现的高级缓存DAO
 * @author conanca
 *
 */
public class RedisAdvancedCacheDao extends RedisCacheDao implements AdvancedCacheDao {

	public RedisAdvancedCacheDao(JedisPool jedisPool) {
		super(jedisPool);
	}

	public void zAdd(String cacheKey, int seconds, double score, Object item) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			boolean isNew = !jedis.exists(cacheKey);
			String cacheValue = null;
			if (CharSequence.class.isAssignableFrom(item.getClass())) {
				cacheValue = item.toString();
			} else {
				cacheValue = JSON.toJSONString(item);
			}
			jedis.zadd(cacheKey, score, cacheValue);
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

	public void zAdd(String cacheKey, double score, Object item) throws Exception {
		zAdd(cacheKey, -1, score, item);
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
			valueList = new ArrayList<String>(valueSet);
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
		return zQueryByRank(cacheKey, startIndex, endIndex, Order.Asc);
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
			valueList = new ArrayList<String>(valueSet);
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
		return zQueryByScore(cacheKey, minScore, maxScore, Order.Asc);
	}

	public List<String> zQueryAll(String cacheKey, Order order) throws Exception {
		return zQueryByRank(cacheKey, 0, -1, order);
	}

	public List<String> zQueryAll(String cacheKey) throws Exception {
		return zQueryByRank(cacheKey, 0, -1);
	}

	public <T> List<T> zQueryByRank(String cacheKey, long startIndex, long endIndex, Order order, Class<T> itemType)
			throws Exception {
		return strList2tList(zQueryByRank(cacheKey, startIndex, endIndex, order), itemType);
	}

	public <T> List<T> zQueryByRank(String cacheKey, long startIndex, long endIndex, Class<T> itemType)
			throws Exception {
		return strList2tList(zQueryByRank(cacheKey, startIndex, endIndex), itemType);
	}

	public <T> List<T> zQueryByScore(String cacheKey, double minScore, double maxScore, Order order, Class<T> itemType)
			throws Exception {
		return strList2tList(zQueryByScore(cacheKey, minScore, maxScore, order), itemType);
	}

	public <T> List<T> zQueryByScore(String cacheKey, double minScore, double maxScore, Class<T> itemType)
			throws Exception {
		return strList2tList(zQueryByScore(cacheKey, minScore, maxScore), itemType);
	}

	public <T> List<T> zQueryAll(String cacheKey, Order order, Class<T> itemType) throws Exception {
		return strList2tList(zQueryAll(cacheKey, order), itemType);
	}

	public <T> List<T> zQueryAll(String cacheKey, Class<T> itemType) throws Exception {
		return strList2tList(zQueryAll(cacheKey), itemType);
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

	@SuppressWarnings("unchecked")
	public <T> List<T> strList2tList(List<String> list, Class<T> itemType) {
		List<T> newList = new ArrayList<T>();
		if (CharSequence.class.isAssignableFrom(itemType)) {
			return (List<T>) list;
		}
		for (String item : list) {
			newList.add(JSON.parseObject(item, itemType));
		}
		return newList;
	}
}
