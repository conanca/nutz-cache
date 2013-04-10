package com.dolplay.nutzcache.assets.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.annotation.CacheKeySuffix;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.interceptor.InterceptorName;
import com.dolplay.nutzcache.type.CacheType;

@IocBean(args = { "refer:dao" })
public class UserAdvancedService extends IdEntityService<User> {

	public UserAdvancedService(Dao dao) {
		super(dao);
	}

	/**
	 * 根据指定性别查询用户Id列表
	 * 缓存类型为有序集合
	 * @param gender
	 * @return
	 */
	@Aop(InterceptorName.ADVANCEDCACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_ALLUSERS_IDLIST, cacheType = CacheType.zset)
	public List<Integer> listIdByGender(@CacheKeySuffix String gender) {
		List<User> userList = query(Cnd.where("gender", "=", gender).desc("birthday"), null);
		List<Integer> idList = new ArrayList<Integer>();
		for (User u : userList) {
			idList.add(u.getId().intValue());
		}
		return idList;
	}

	@Aop(InterceptorName.ADVANCEDCACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_ALLUSERS_LIST, cacheType = CacheType.zset)
	public List<User> listByGender(@CacheKeySuffix String gender) {
		List<User> userList = query(Cnd.where("gender", "=", gender).desc("birthday"), null);
		return userList;
	}

	@Aop(InterceptorName.ADVANCEDCACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_NEWUSERS_IDLIST, cacheType = CacheType.zset, reverse = true)
	public List<String> listNewUsers() throws ParseException {
		List<User> userList = query(Cnd.where("birthday", ">", new SimpleDateFormat("yyyy-MM-dd").parse("2008-01-01"))
				.desc("id"), null);
		List<String> idList = new ArrayList<String>();
		for (User u : userList) {
			idList.add(String.valueOf(u.getId()));
		}
		return idList;
	}
}
