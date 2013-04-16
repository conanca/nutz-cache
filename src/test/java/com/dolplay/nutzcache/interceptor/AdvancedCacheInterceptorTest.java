package com.dolplay.nutzcache.interceptor;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.assets.service.UserAdvancedService;
import com.dolplay.nutzcache.assets.utils.IocProvider;
import com.dolplay.nutzcache.dao.AdvancedCacheDao;
import com.dolplay.nutzcache.type.Order;

public class AdvancedCacheInterceptorTest {
	private static Logger logger = LoggerFactory.getLogger(CacheInterceptorTest.class);
	private static JedisPool pool;
	private static Jedis jedis;
	private static UserAdvancedService userService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Ioc ioc = IocProvider.ioc();
		// 初始化数据库
		logger.info("初始化数据库...");
		Dao dao = ioc.get(Dao.class, "dao");
		dao.create(User.class, true);
		FileSqlManager fm = new FileSqlManager("init_system_h2.sql");
		List<Sql> sqlList = fm.createCombo(fm.keys());
		dao.execute(sqlList.toArray(new Sql[sqlList.size()]));

		// 初始化redis数据及连接
		logger.info("初始化redis数据及连接...");
		pool = ioc.get(JedisPool.class, "jedisPool");
		jedis = pool.getResource();
		jedis.flushDB();

		// 初始化UserService
		userService = ioc.get(UserAdvancedService.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pool.returnResource(jedis);
	}

	@Test
	public void testSimple() throws ParseException {
		List<Integer> ids1 = userService.listIdByGender("male");
		logger.debug("第一次查询用户结果：" + JSON.toJSONString(ids1));
		List<Integer> ids2 = userService.listIdByGender("male");
		logger.debug("第二次查询用户结果：" + JSON.toJSONString(ids2));
		assertEquals(ids1, ids2);
		User user = new User();
		user.setName("testuser");
		user.setGender("male");
		user.setDescription("test");
		user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2003-01-03"));
		userService.dao().insert(user);
		jedis.del(CacheKeyPrefix.TEST_CACHE_ALLUSERS_IDLIST + ":male");
		List<Integer> ids3 = userService.listIdByGender("male");
		logger.debug("第三次查询用户结果：" + JSON.toJSONString(ids3));
		List<Integer> ids4 = userService.listIdByGender("male");
		logger.debug("第四次查询用户结果：" + JSON.toJSONString(ids4));
		assertEquals(ids3, ids4);
	}

	@Test
	public void testObjList() {
		List<User> userList1 = userService.listByGender("male");
		logger.debug("第一次查询用户结果：" + JSON.toJSONString(userList1));
		List<User> userList2 = userService.listByGender("male");
		logger.debug("第二次查询用户结果：" + JSON.toJSONString(userList2));
		assertEquals(userList1, userList2);
	}

	@Test
	public void testReverse() throws Exception {
		List<String> names1 = userService.listNewUsers();
		logger.debug("第一次查询用户结果：" + JSON.toJSONString(names1));
		List<String> names2 = userService.listNewUsers();
		logger.debug("第二次查询用户结果：" + JSON.toJSONString(names2));
		User user = new User();
		user.setName("newtestuser");
		user.setGender("male");
		user.setDescription("test");
		user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2013-04-01"));
		user = userService.dao().insert(user);
		AdvancedCacheDao cacheDao = IocProvider.ioc().get(AdvancedCacheDao.class, "advancedCacheDao");
		cacheDao.zAdd(CacheKeyPrefix.TEST_CACHE_NEWUSERS_NAMELIST, System.currentTimeMillis(),
				String.valueOf(user.getName()));
		List<String> namesCache = cacheDao.zQueryAll(CacheKeyPrefix.TEST_CACHE_NEWUSERS_NAMELIST, Order.Desc);
		logger.debug("从缓存中获取结果:" + JSON.toJSONString(namesCache));
		jedis.del(CacheKeyPrefix.TEST_CACHE_NEWUSERS_NAMELIST);
		List<String> names3 = userService.listNewUsers();
		logger.debug("第三次查询用户结果：" + JSON.toJSONString(names3));
		assertEquals(namesCache, names3);
	}
}
