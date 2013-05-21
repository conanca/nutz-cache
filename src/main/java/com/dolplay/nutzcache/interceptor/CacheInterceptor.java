package com.dolplay.nutzcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dolplay.nutzcache.CacheConfig;
import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.annotation.CacheKeySuffix;
import com.dolplay.nutzcache.dao.AdvancedCacheDao;
import com.dolplay.nutzcache.lang.CStrings;
import com.dolplay.nutzcache.type.CacheType;

/**
 * @author Conanca
 *  实现缓存预先读取及缓存自动设值的方法拦截器，支持字符串型缓存
 */
public class CacheInterceptor implements MethodInterceptor {
	private static Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);

	private AdvancedCacheDao cacheDao;
	private PropertiesProxy cacheProp;

	public AdvancedCacheDao cacheDao() {
		return cacheDao;
	}

	public PropertiesProxy cacheProp() {
		return cacheProp;
	}

	public void filter(InterceptorChain chain) throws Throwable {
		// 取得被拦截的方法及其注解
		Method method = chain.getCallingMethod();
		Cache cacheAn = method.getAnnotation(Cache.class);
		Object[] args = chain.getArgs();
		// 获取cacheKey
		String cacheKey = createCacheKey(args, method, cacheAn);
		// 若cacheKey不为空，将对该缓存及该方法返回值做相应操作否则直接执行方法
		if (cacheKey != null) {
			cacheReturn(cacheKey, chain, method, cacheAn);
		} else {
			// 执行方法
			chain.doChain();
		}
	}

	/**
	 * 创建缓存名
	 * @param chain
	 * @return
	 */
	protected String createCacheKey(Object args[], Method method, Cache cacheAn) {
		String cacheKey = null;

		// 获取缓存KEY的前缀：cacheKeyPrefix
		String cacheKeyPrefix = cacheAn.cacheKeyPrefix();
		// 若cacheKeyPrefix不为空，将对该缓存做相应操作否则直接执行方法
		if (!Strings.isEmpty(cacheKeyPrefix)) {
			// 获取该方法欲读取的缓存的 KEY——将拼接方法注解中的cacheKeyPrefix及标有“CacheKeySuffix”注解的参数
			String[] cacheParaArr = new String[args.length];
			Annotation[][] ans = method.getParameterAnnotations();
			int k = 0;
			if (ans.length > 0) {
				for (int i = 0; i < ans.length; i++) {
					for (int j = 0; j < ans[i].length; j++) {
						if (ans[i][j].annotationType() == CacheKeySuffix.class) {
							if (args[i] == null) {
								cacheParaArr[k] = "";
							} else if (CharSequence.class.isAssignableFrom(args[i].getClass())) {
								cacheParaArr[k] = args[i].toString();
							} else {
								cacheParaArr[k] = JSON.toJSONString(args[i], SerializerFeature.UseSingleQuotes);
							}
							k++;
						}
					}
				}
			}
			cacheKey = CStrings.cacheKey(cacheKeyPrefix, cacheParaArr);
			logger.debug("Cache key : " + cacheKey);
		} else {
			logger.warn("cacheKeyPrefix is empty!");
		}

		return cacheKey;
	}

	/**
	 * 操作缓存和方法的返回值
	 * 先取缓存值，如果缓存值不为空，将其赋给方法的返回值；如果缓存值为空，将执行方法，并获取方法返回值，将该值再赋给缓存
	 * @param cacheKey
	 * @param chain
	 * @param method
	 * @param cacheAn
	 * @throws Throwable
	 */
	protected void cacheReturn(String cacheKey, InterceptorChain chain, Method method, Cache cacheAn) throws Throwable {
		boolean isEternalCacheKeySetValid = isEternalCacheKeySetValid(cacheProp(), CacheType.string);
		// 获取缓存超时时间
		int cacheTimeout = createCacheTimeout(cacheAn, cacheProp(), CacheType.string);
		String stringEternalCacheKeySetName = cacheProp().get("cahce-stringEternalCacheKeySetName",
				CacheConfig.STRING_ETERNAL_CACHE_KEY_SET_NAME);
		// 获取该方法欲读取的缓存的 VALUE
		String cacheValue = null;
		try {
			cacheValue = cacheDao().get(cacheKey);
		} catch (Exception e) {
			logger.error("Read Cache error", e);
		}
		// 若缓存值不为空，则该方法直接返回缓存里相应的值
		if (cacheValue != null) {
			Class<?> returnType = method.getReturnType();
			Object returnValue = null;
			if (returnType.isAssignableFrom(List.class)) {
				returnValue = JSON.parseArray(cacheValue,
						(Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
			} else {
				returnValue = JSON.parseObject(cacheValue, returnType);
			}
			chain.setReturnValue(returnValue);
			logger.debug("Get a value from this cache:" + cacheKey);
			return;
		} else {
			logger.debug("Can't get any value from this cache:" + cacheKey);
			if (isEternalCacheKeySetValid && cacheTimeout < 0) {
				try {
					if (cacheDao().sIsMember(stringEternalCacheKeySetName, cacheKey)) {
						logger.debug(cacheKey + " is in " + stringEternalCacheKeySetName
								+ ",will return null right now");
						chain.setReturnValue(null);
						return;
					}
				} catch (Exception e) {
					logger.error("Read Cache error", e);
				}
			}
		}
		// 执行方法
		chain.doChain();
		// 获取方法返回值
		Object returnObj = chain.getReturn();

		if (returnObj != null) {
			try {
				//如果缓存超时时间设置的有效，则新增缓存时设置该超时时间，否则设置配置文件中所配置的超时时间
				cacheDao.set(cacheKey, cacheTimeout, returnObj);
				logger.debug("Set a new value for this cache:" + cacheKey);
			} catch (Exception e) {
				logger.error("Set cache error", e);
			}
		} else {
			logger.warn("No value to set for this cache:" + cacheKey);
		}
		// 往StringEternalCacheKeySet添加相应的Key
		if (isEternalCacheKeySetValid && cacheTimeout < 0) {
			try {
				cacheDao.sAdd(stringEternalCacheKeySetName, cacheKey);
			} catch (Exception e) {
				logger.error("Set cache error", e);
			}
		}

	}

	protected static int createCacheTimeout(Cache cacheAn, PropertiesProxy cacheProp, CacheType type) {
		int cacheTimeout = cacheAn.cacheTimeout();
		if (cacheTimeout == CacheConfig.INVALID_TIMEOUT) {
			if (type.equals(CacheType.string)) {
				cacheTimeout = cacheProp.getInt("cache-defaultStringCacheTimeout",
						CacheConfig.DEFAULT_STRING_CACHE_TIMEOUT);
			} else if (type.equals(CacheType.zset)) {
				cacheTimeout = cacheProp
						.getInt("cache-defaultZsetCacheTimeout", CacheConfig.DEFAULT_ZSET_CACHE_TIMEOUT);
			} else {
				logger.warn("Unknown cache type" + type);
				cacheTimeout = -1;
			}
		}
		return cacheTimeout;
	}

	protected static boolean isEternalCacheKeySetValid(PropertiesProxy cacheProp, CacheType type) {
		boolean isValid = false;
		if (type.equals(CacheType.string)) {
			String strIsValid = cacheProp.get("cache-stringEternalCacheKeySetIsValid");
			isValid = Strings.isEmpty(strIsValid) ? CacheConfig.STRING_ETERNAL_CACHE_KEY_SET_IS_VALID : Boolean
					.valueOf(strIsValid);
		} else if (type.equals(CacheType.zset)) {
			String strIsValid = cacheProp.get("cache-zsetEternalCacheKeySetIsValid");
			isValid = Strings.isEmpty(strIsValid) ? CacheConfig.ZSET_ETERNAL_CACHE_KEY_SET_IS_VALID : Boolean
					.valueOf(strIsValid);
		} else {
			logger.warn("Unknown cache type" + type);
		}
		return isValid;
	}
}
