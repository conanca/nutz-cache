package com.dolplay.nutzcache.test.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

import com.dolplay.nutzcache.CStrings;
import com.dolplay.nutzcache.CacheKeyPrefix;
import com.dolplay.nutzcache.annotation.Cache;
import com.dolplay.nutzcache.annotation.CacheKeySuffix;
import com.dolplay.nutzcache.dao.CacheDao;
import com.dolplay.nutzcache.service.CacheIdEntityService;
import com.dolplay.nutzcache.test.domain.User;

/**
 * @author Conanca
 * 用户增删改查等操作的Service类
 * 演示使用缓存的一个示例
 */
@IocBean(args = { "refer:dao", "refer:cacheDao" })
public class UserService extends CacheIdEntityService<User> {

	public UserService(Dao dao, CacheDao cacheDao) {
		super(dao, cacheDao);
	}

	public Map<Long, String> map() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<User> users = query(null, null);
		for (User user : users) {
			map.put(user.getId(), user.getName());
		}
		return map;
	}

	/**
	 * 查询全部用户返回一个列表
	 * 指定了缓存名为cache:system:allusers(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_ALLUSERS)
	public List<User> list() {
		return query(null, null);
	}

	/**
	 * 查询指定id的用户
	 * 指定了缓存名的前缀为cache:system:user,后缀为用户id(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @param id
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_USER)
	public User view(@CacheKeySuffix int id) {
		return fetch(id);
	}

	/**
	 * 插入一个用户
	 * @param user
	 * @return
	 */
	public void insert(User user) {
		dao().insert(user);
	}

	/**
	 * 更新一个用户,并手动更新相应缓存cache:system:user:[id]
	 * @param id
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	public void update(int id, User user) throws Exception {
		dao().update(user);
		// 立即更新缓存
		cacheDao().set(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_USER, id), user);
	}

	/**
	 * 删除一个用户,并手动删除相应缓存cache:system:user:[id]
	 * @param id
	 * @throws Exception 
	 */
	public void remove(int id) throws Exception {
		delete(id);
		// 立即删除缓存
		cacheDao().remove(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_USER, id));
	}

	/**
	 * 手动删除全部用户列表缓存
	 * @throws Exception 
	 */
	public void delCacheForTest() throws Exception {
		cacheDao().remove(CacheKeyPrefix.SYSTEM_ALLUSERS);
	}
}
