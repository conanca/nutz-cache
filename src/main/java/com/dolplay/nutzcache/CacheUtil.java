package com.dolplay.nutzcache;

import org.nutz.ioc.impl.PropertiesProxy;

import com.dolplay.nutzcache.dao.AdvancedCacheDao;

public class CacheUtil {
	public static long remStrEternalKeySet(PropertiesProxy cacheProp, AdvancedCacheDao cacheDao, String... key)
			throws Exception {
		return cacheDao
				.sRem(cacheProp
						.get("cahce-stringEternalCacheKeySetName", CacheConfig.STRING_ETERNAL_CACHE_KEY_SET_NAME), key);
	}

	public static long remZsetEternalKeySet(PropertiesProxy cacheProp, AdvancedCacheDao cacheDao, String... key)
			throws Exception {
		return cacheDao.sRem(
				cacheProp.get("cache-zsetEternalCacheKeySetName", CacheConfig.ZSET_ETERNAL_CACHE_KEY_SET_NAME), key);
	}
}
