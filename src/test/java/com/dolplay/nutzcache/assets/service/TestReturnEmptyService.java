package com.dolplay.nutzcache.assets.service;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.interceptor.InterceptorName;
import com.dolplay.nutzcache.type.CacheType;

@IocBean(args = { "refer:dao" })
public class TestReturnEmptyService extends IdEntityService<User> {

	public TestReturnEmptyService(Dao dao) {
		super(dao);
	}

	@Aop(InterceptorName.CACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = "xobj", cacheTimeout = -1)
	public User viewXobj() {
		System.out.println("try to fetch date to get a list...");
		System.out.println("ops,no date!");
		return null;
	}

	@Aop(InterceptorName.ADVANCEDCACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = "xlist", cacheType = CacheType.zset, cacheTimeout = -1)
	public List<String> xlist() {
		System.out.println("try to fetch date to get a list...");
		System.out.println("ops,no date!");
		return new ArrayList<String>();
	}
}
