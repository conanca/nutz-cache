package com.dolplay.nutzcache.type;

/**
 * 在缓存中存储值的类型，均属于redis的数据类型
 * @author conanca
 */
public enum CacheType {

	/**
	 * 字符串
	 */
	string,

	/**
	 * 有序集，请参考Redis的数据结构 : sorted set
	 */
	zset

}
