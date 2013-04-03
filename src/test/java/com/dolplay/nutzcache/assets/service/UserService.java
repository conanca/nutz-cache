package com.dolplay.nutzcache.assets.service;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.annotation.CacheKeySuffix;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;

/**
 * @author Conanca
 * 用户增删改查等操作的Service类
 * 演示使用缓存的一个示例
 */
@IocBean(args = { "refer:dao" })
public class UserService extends IdEntityService<User> {

	public UserService(Dao dao) {
		super(dao);
	}

	@Aop("cacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_REFERENCEUSER)
	public User viewReferenceUser() {
		return fetch(1);
	}

	@Aop("cacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_USER)
	public User view(@CacheKeySuffix int id) {
		return fetch(id);
	}

	@Aop("advancedCacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_ALLUSERS_INPAGE)
	public List<User> listInPage(@CacheKeySuffix Pager pager) {
		return query(null, pager);
	}

	@Aop("cacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_COUNTUSER, cacheTimeout = 600)
	public int countUser() {
		return count();
	}

	/**
	 * 插入一个用户
	 * @param user
	 * @return
	 */
	public void insert(User user) {
		dao().insert(user);
	}
}
