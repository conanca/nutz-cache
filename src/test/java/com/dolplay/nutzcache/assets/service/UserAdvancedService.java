package com.dolplay.nutzcache.assets.service;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzcache.CacheType;
import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.annotation.CacheKeySuffix;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;

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
	@Aop("advancedCacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_ALLUSERS_IDLIST, cacheType = CacheType.List)
	public List<String> listIdByGender(@CacheKeySuffix String gender) {
		List<User> userList = query(Cnd.where("gender", "=", gender).desc("birthday"), null);
		List<String> idList = new ArrayList<String>();
		for (User u : userList) {
			idList.add(String.valueOf(u.getId()));
		}
		return idList;
	}

}
