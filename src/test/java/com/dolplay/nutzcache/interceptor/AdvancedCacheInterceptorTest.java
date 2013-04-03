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
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzcache.Order;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.assets.service.UserAdvancedService;
import com.dolplay.nutzcache.assets.utils.IocProvider;
import com.dolplay.nutzcache.dao.AdvancedCacheDao;

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
		List<String> ids1 = userService.listIdByGender("male");
		logger.debug("第一次查询用户结果：" + Json.toJson(ids1));
		List<String> ids2 = userService.listIdByGender("male");
		logger.debug("第二次查询用户结果：" + Json.toJson(ids2));
		assertEquals(ids1, ids2);
		User user = new User();
		user.setName("testuser");
		user.setGender("male");
		user.setDescription("test");
		user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2003-01-03"));
		userService.dao().insert(user);
		jedis.del(CacheKeyPrefix.TEST_CACHE_ALLUSERS_IDLIST + ":male");
		List<String> ids3 = userService.listIdByGender("male");
		logger.debug("第三次查询用户结果：" + Json.toJson(ids3));
		List<String> ids4 = userService.listIdByGender("male");
		logger.debug("第四次查询用户结果：" + Json.toJson(ids4));
		assertEquals(ids3, ids4);
	}

	@Test
	public void testReverse() throws Exception {
		List<String> ids1 = userService.listNewUsers();
		logger.debug("第一次查询用户结果：" + Json.toJson(ids1));
		List<String> ids2 = userService.listNewUsers();
		logger.debug("第二次查询用户结果：" + Json.toJson(ids2));
		User user = new User();
		user.setName("newtestuser");
		user.setGender("male");
		user.setDescription("test");
		user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2013-04-01"));
		user = userService.dao().insert(user);
		AdvancedCacheDao cacheDao = IocProvider.ioc().get(AdvancedCacheDao.class, "advancedCacheDao");
		cacheDao.zAdd(CacheKeyPrefix.TEST_CACHE_NEWUSERS_IDLIST, System.currentTimeMillis(),
				String.valueOf(user.getId()));
		List<String> idsCache = cacheDao.zQueryAll(CacheKeyPrefix.TEST_CACHE_NEWUSERS_IDLIST, Order.Desc);
		logger.debug("从缓存中获取结果:" + Json.toJson(idsCache));
		jedis.del(CacheKeyPrefix.TEST_CACHE_NEWUSERS_IDLIST);
		List<String> ids3 = userService.listNewUsers();
		logger.debug("第三次查询用户结果：" + Json.toJson(ids3));
		assertEquals(idsCache, ids3);
	}
}
