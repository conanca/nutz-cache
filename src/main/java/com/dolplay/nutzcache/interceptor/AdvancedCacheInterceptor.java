package com.dolplay.nutzcache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolplay.nutzcache.CacheConfig;
import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.type.CacheType;
import com.dolplay.nutzcache.type.Order;

/**
 * @author Conanca
 * 实现缓存预先读取及缓存自动设值的方法拦截器，支持字符串型缓存和有序集型缓存
 */
public class AdvancedCacheInterceptor extends CacheInterceptor {
	private static Logger logger = LoggerFactory.getLogger(AdvancedCacheInterceptor.class);

	@SuppressWarnings("rawtypes")
	protected void cacheReturn(String cacheKey, InterceptorChain chain, Method method, Cache cacheAn) throws Throwable {
		boolean isEternalCacheKeySetValid = isEternalCacheKeySetValid(cacheProp(), CacheType.zset);
		int cacheTimeout = createCacheTimeout(cacheAn, cacheProp(), CacheType.zset);
		String zsetEternalCacheKeySetName = cacheProp().get("ZsetEternalCacheKeySetName",
				CacheConfig.ZSET_ETERNAL_CACHE_KEY_SET_NAME);
		// 获取缓存类型，根据缓存类型不同分别对缓存有不同的操作方式
		CacheType cacheType = cacheAn.cacheType();
		if (cacheType.equals(CacheType.string)) {
			super.cacheReturn(cacheKey, chain, method, cacheAn);
		} else if (cacheType.equals(CacheType.zset)) {
			// 获取该方法欲读取的缓存的 VALUE
			List<?> cacheValue = null;
			Class<?> returnListItemType = (Class<?>) ((ParameterizedType) method.getGenericReturnType())
					.getActualTypeArguments()[0];
			try {
				if (cacheAn.reverse()) {
					cacheValue = cacheDao().zQueryAll(cacheKey, Order.Desc, returnListItemType);
				} else {
					cacheValue = cacheDao().zQueryAll(cacheKey, Order.Asc, returnListItemType);
				}
			} catch (Exception e) {
				logger.error("Read Cache error", e);
			}
			// 若缓存值不为空，则该方法直接返回缓存里相应的值
			if (cacheValue != null && cacheValue.size() > 0) {
				chain.setReturnValue(cacheValue);
				logger.debug("Get a value from this cache:" + cacheKey);
				return;
			} else {
				logger.debug("Can't get any value from this cache:" + cacheKey);
				if (isEternalCacheKeySetValid && cacheTimeout < 0) {
					try {
						if (cacheDao().sIsMember(zsetEternalCacheKeySetName, cacheKey)) {
							logger.debug(cacheKey + " is in " + zsetEternalCacheKeySetName
									+ ",will return empty list right now");
							chain.setReturnValue(new ArrayList());
							return;
						}
					} catch (Exception e) {
						logger.error("Read Cache error", e);
					}
				}
			}
			// 执行方法
			chain.doChain();
			// 获取方法返回值并增加相应缓存
			@SuppressWarnings("unchecked")
			List<?> cacheObj = new ArrayList((List<?>) chain.getReturn());
			AdvancedReturn2Cache ar2c = new AdvancedReturn2Cache(cacheDao(), cacheKey, cacheTimeout, cacheObj,
					cacheAn.reverse(), isEternalCacheKeySetValid, zsetEternalCacheKeySetName);
			ar2c.start();
		} else {
			logger.error("The method annotation : CacheType Error!", new RuntimeException(
					"The method annotation : CacheType Error"));
		}
	}
}
