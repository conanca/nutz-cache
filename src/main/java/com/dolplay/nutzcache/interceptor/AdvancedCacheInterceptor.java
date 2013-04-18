package com.dolplay.nutzcache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nutz.aop.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.dao.AdvancedCacheDao;
import com.dolplay.nutzcache.type.CacheType;
import com.dolplay.nutzcache.type.Order;

/**
 * @author Conanca
 * 实现缓存预先读取及缓存自动设值的方法拦截器，支持字符串型缓存和有序集型缓存
 */
public class AdvancedCacheInterceptor extends CacheInterceptor {
	private static Logger logger = LoggerFactory.getLogger(AdvancedCacheInterceptor.class);

	private AdvancedCacheDao cacheDao;

	public AdvancedCacheDao cacheDao() {
		return cacheDao;
	}

	protected void cacheReturn(String cacheKey, InterceptorChain chain, Method method, Cache cacheAn) throws Throwable {
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
				logger.debug("Get a value from this cache");
				return;
			} else {
				logger.debug("Can't get any value from this cache");
			}
			// 执行方法
			chain.doChain();
			// 获取方法返回值并增加相应缓存
			List<?> returnObj = (List<?>) chain.getReturn();
			if (returnObj != null && returnObj.size() > 0) {
				try {
					setCache(cacheKey, returnObj, cacheAn);
					logger.debug("Set a new value for this cache");
				} catch (Exception e) {
					logger.error("Set cache error", e);
				}
			} else {
				logger.warn("No value to set for this cache");
			}
		} else {
			logger.error("The method annotation : CacheType Error!", new RuntimeException(
					"The method annotation : CacheType Error"));
		}
	}

	private void setCache(String cacheKey, List<?> returnObj, Cache cacheAn) throws Exception {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<?> items = new ArrayList(returnObj);
		// 如果需要倒序存放入缓存中，则将顺序倒转
		if (cacheAn.reverse()) {
			Collections.reverse(items);
		}
		// 按items的顺序依次插入相应的缓存中
		long now = System.currentTimeMillis();
		int cacheTimeout = createCacheTimeout(cacheAn, cacheProp(), CacheType.zset);
		for (Object item : items) {
			// 如果缓存超时时间设置的有效，则新增缓存时设置该超时时间，否则设置配置文件中所配置的超时时间
			cacheDao().zAdd(cacheKey, cacheTimeout, now++, item);
		}
	}
}
