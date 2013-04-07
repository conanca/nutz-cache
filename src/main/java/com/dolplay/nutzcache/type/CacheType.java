package com.dolplay.nutzcache.type;

public enum CacheType {

	/**
	 * 字符串(任何对象均可被缓存，都将被转为JSON格式的字符串)值
	 */
	String,

	/**
	 * 有序集合值，请参考Redis的数据结构 : sorted set
	 */
	List

}
