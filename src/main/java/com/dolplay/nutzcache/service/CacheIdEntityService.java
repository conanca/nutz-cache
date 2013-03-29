package com.dolplay.nutzcache.service;

import org.nutz.dao.Dao;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzcache.dao.CacheDao;

/**
 * 提供缓存操作的Service基类
 * @author conanca
 *
 * @param <T>
 */
public class CacheIdEntityService<T> extends IdEntityService<T> implements CacheService {
	private CacheDao cacheDao;

	public CacheIdEntityService(Dao dao, CacheDao cacheDao) {
		super(dao);
		this.cacheDao = cacheDao;
	}

	public CacheDao cacheDao() {
		return cacheDao;
	}

}
