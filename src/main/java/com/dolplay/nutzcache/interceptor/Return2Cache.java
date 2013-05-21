package com.dolplay.nutzcache.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolplay.nutzcache.dao.AdvancedCacheDao;

/**
 * 没办法克隆方法返回值给这个线程，暂废弃该类
 * @author conanca
 *
 */
public class Return2Cache extends Thread {
	private static Logger logger = LoggerFactory.getLogger(Return2Cache.class);

	private AdvancedCacheDao cacheDao;
	private String cacheKey;
	private int cacheTimeout;
	private Object returnObj;
	private boolean isEternalCacheKeySetValid;
	private String stringEternalCacheKeySetName;

	public Return2Cache(AdvancedCacheDao cacheDao, String cacheKey, int cacheTimeout, Object returnObj,
			boolean isEternalCacheKeySetValid, String stringEternalCacheKeySetName) {
		super();
		this.cacheDao = cacheDao;
		this.cacheKey = cacheKey;
		this.cacheTimeout = cacheTimeout;
		this.returnObj = returnObj;
		this.isEternalCacheKeySetValid = isEternalCacheKeySetValid;
		this.stringEternalCacheKeySetName = stringEternalCacheKeySetName;
	}

	public void run() {
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
}
