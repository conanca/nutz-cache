package com.dolplay.nutzcache.lang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dolplay.nutzcache.CacheConfig;

/**
 * 缓存相关的字符串操作的帮助类
 * @author conanca
 *
 */
public class CStrings {

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheKeyPrefix
	 * @param cacheKeySuffixs
	 * @return
	 */
	public static String cacheKey(String cacheKeyPrefix, String... cacheKeySuffixs) {
		StringBuilder sb = new StringBuilder(cacheKeyPrefix);
		if (cacheKeySuffixs != null && cacheKeySuffixs.length > 0) {
			for (String cacheKeySuffix : cacheKeySuffixs) {
				// 如果缓存名称后缀为null，则拼接一个空字符串
				if (cacheKeySuffix == null) {
					cacheKeySuffix = "";
				}
				sb.append(CacheConfig.CACHEKEY_DELIMITER);
				sb.append(cacheKeySuffix);
			}
		}
		return sb.toString();
	}

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheKeyPrefix
	 * @param cacheKeySuffixs
	 * @return
	 */
	public static String cacheKey(String cacheKeyPrefix, int... cacheKeySuffixs) {
		StringBuilder sb = new StringBuilder(cacheKeyPrefix);
		if (cacheKeySuffixs != null && cacheKeySuffixs.length > 0) {
			for (int cacheKeySuffix : cacheKeySuffixs) {
				sb.append(CacheConfig.CACHEKEY_DELIMITER);
				sb.append(cacheKeySuffix);
			}
		}
		return sb.toString();
	}

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheKeyPrefix
	 * @param cacheKeySuffixs
	 * @return
	 */
	public static String cacheKey(String cacheKeyPrefix, long... cacheKeySuffixs) {
		StringBuilder sb = new StringBuilder(cacheKeyPrefix);
		if (cacheKeySuffixs != null && cacheKeySuffixs.length > 0) {
			for (long cacheKeySuffix : cacheKeySuffixs) {
				sb.append(CacheConfig.CACHEKEY_DELIMITER);
				sb.append(cacheKeySuffix);
			}
		}
		return sb.toString();
	}

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheKeyPrefix
	 * @param cacheKeySuffixs
	 * @return
	 */
	public static String commonCacheKey(String cacheKeyPrefix, Object... cacheKeySuffixs) {
		StringBuilder sb = new StringBuilder(cacheKeyPrefix);
		if (cacheKeySuffixs != null && cacheKeySuffixs.length > 0) {
			for (Object cacheKeySuffix : cacheKeySuffixs) {
				// 如果缓存名称后缀为null，则拼接一个空字符串
				if (cacheKeySuffix == null) {
					cacheKeySuffix = "";
				} else if (CharSequence.class.isAssignableFrom(cacheKeySuffix.getClass())) {
					cacheKeySuffix = cacheKeySuffix.toString();
				} else {
					cacheKeySuffix = JSON.toJSONString(cacheKeySuffix, SerializerFeature.UseSingleQuotes);
				}
				sb.append(CacheConfig.CACHEKEY_DELIMITER);
				sb.append(cacheKeySuffix);
			}
		}
		return sb.toString();
	}
}
