package com.dolplay.nutzcache;

public class CacheConfig {

	/**
	 * 缓存名前缀和(一个或多个)后缀拼接时用的间隔符号 
	 */
	public static final String CACHEKEY_DELIMITER = ":";

	/**
	 * 默认的字符串型缓存的超时时间，单位为秒
	 */
	public static final int DEFAULT_STRING_CACHE_TIMEOUT = 600;

	/**
	 * 默认的有序集合类型缓存的超时时间，单位为秒
	 */
	public static final int DEFAULT_ZSET_CACHE_TIMEOUT = 3600;

	/**
	 * 用于指明Cache注解中cacheTimeout的特定标志值，表示使用配置文件所配置的超时时间
	 */
	public static final int INVALID_TIMEOUT = -9527;

}
