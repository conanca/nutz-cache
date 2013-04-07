package com.dolplay.nutzcache.type;

public enum CacheType {

	/**
	 * 普通类型的缓存
	 */
	Common,

	/**
	 * 有序集类型的缓存，请参考Redis的数据结构 : sorted set
	 */
	Sorted

}
