package com.dolplay.nutzcache;

import org.nutz.ioc.impl.PropertiesProxy;

import com.dolplay.nutzcache.dao.AdvancedCacheDao;

public class CacheUtil {
	public static long remStrEternalKeySet(PropertiesProxy cacheProp, AdvancedCacheDao cacheDao, String... key)
			throws Exception {
		return cacheDao.sRem(
				cacheProp.get("StringEternalCacheKeySetName", CacheConfig.String_Eternal_Cache_KeySet_Name), key);
	}

	public static long remZsetEternalKeySet(PropertiesProxy cacheProp, AdvancedCacheDao cacheDao, String... key)
			throws Exception {
		return cacheDao.sRem(cacheProp.get("ZsetEternalCacheKeySetName", CacheConfig.Zset_Eternal_Cache_KeySet_Name),
				key);
	}
}
