package com.dolplay.nutzcache.assets.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.annotation.CacheKeySuffix;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.interceptor.InterceptorName;

@IocBean(args = { "refer:dao" })
public class UserService extends IdEntityService<User> {

	public UserService(Dao dao) {
		super(dao);
	}

	/**
	 * 查询参考用户
	 * @return
	 */
	@Aop(InterceptorName.CACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_REFERENCEUSER)
	public User viewReferenceUser() {
		return fetch(1);
	}

	/**
	 * 查询参考用户的名称
	 * @return
	 */
	@Aop(InterceptorName.CACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_REFERENCEUSERNAME)
	public String viewReferenceUserName() {
		return fetch(1).getName();
	}

	/**
	 * 根据id查询用户
	 * @param id
	 * @return
	 */
	@Aop(InterceptorName.CACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_USER)
	public User view(@CacheKeySuffix int id) {
		return fetch(id);
	}

	/**
	 * 分页查询全部用户列表
	 * @param pager
	 * @return
	 */
	@Aop("advancedCacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_ALLUSERS_INPAGE)
	public List<User> listInPage(@CacheKeySuffix Pager pager) {
		return query(null, pager);
	}

	/**
	 * 统计用户数
	 * @return
	 */
	@Aop(InterceptorName.CACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_COUNTUSER, cacheTimeout = 600)
	public int countUser() {
		return count();
	}

	/**
	 * 查询全部用户id列表
	 * @return
	 */
	@Aop(InterceptorName.CACHEINTERCEPTOR)
	@Cache(cacheKeyPrefix = CacheKeyPrefix.TEST_CACHE_USERIDS)
	public Set<Integer> userIds() {
		Sql sql = Sqls.create("SELECT ID FROM SYSTEM_USER");
		sql.setCallback(Sqls.callback.ints());
		dao().execute(sql);
		int[] idsArray = (int[]) sql.getResult();
		Set<Integer> idSet = new HashSet<Integer>();
		for (int id : idsArray) {
			idSet.add(id);
		}
		return idSet;
	}

	public void insert(User user) {
		dao().insert(user);
	}
}
