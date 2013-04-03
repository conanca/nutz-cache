package com.dolplay.nutzcache.dao;

import java.util.Set;

/**
 * 缓存DAO，用于操作缓存
 * @author conanca
 *
 */
public interface CacheDao {
	/**
	 * 指定缓存名往缓存中增加一个值，如果存在将更新该缓存。注：缓存超时时间由配置文件配置
	 * @param cacheKey
	 * @param cacheValue
	 * @throws Exception
	 */
	public void set(String cacheKey, Object cacheValue) throws Exception;

	/**
	 * 指定缓存名往缓存中增加一个值，如果存在将更新该缓存。可指定缓存超时时间(秒)，如果超时时间小于等于0，则为永久缓存
	 * @param cacheKey
	 * @param timeout
	 * @param cacheValue
	 */
	public void set(String cacheKey, int timeout, Object cacheValue) throws Exception;

	/**
	 * 根据缓存名从缓存中获取一个JSON格式的值
	 * @param cacheKey
	 * @return
	 */
	public String get(String cacheKey) throws Exception;

	/**
	 * 根据缓存名和对象类型从缓存中获取一个对象
	 * @param cacheKey
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public <T> T get(String cacheKey, Class<T> type) throws Exception;

	/**
	 * 删除一个或多个缓存。
	 * @param cacheKeys
	 * @return
	 */
	public long remove(String... cacheKeys) throws Exception;

	/**
	 * 为给定缓存设置超时时间(秒)，当缓存 过期时(超时时间为 0 )，它会被自动删除
	 * @param cacheKey
	 * @param seconds
	 * @return
	 */
	public boolean expire(String cacheKey, int seconds) throws Exception;

	/**
	 * 判断给定 key 是否存在
	 * @param cacheKey
	 * @return
	 */
	public boolean exists(String cacheKey) throws Exception;

	/**
	 * 查找所有符合给定模式 pattern 的 key
	 * 
	 * KEYS * 匹配数据库中所有 key 。
	 * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
	 * KEYS h*llo 匹配 hllo 和 heeeeello 等。
	 * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
	 * 特殊符号用 \ 隔
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keySet(String pattern) throws Exception;
}
