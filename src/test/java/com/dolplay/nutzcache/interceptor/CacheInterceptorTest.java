package com.dolplay.nutzcache.interceptor;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dolplay.nutzcache.assets.CacheKeyPrefix;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.assets.service.UserService;
import com.dolplay.nutzcache.assets.utils.IocProvider;

public class CacheInterceptorTest {
	private static Logger logger = LoggerFactory.getLogger(CacheInterceptorTest.class);
	private static JedisPool pool;
	private static Jedis jedis;
	private static UserService userService;

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
		userService = ioc.get(UserService.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pool.returnResource(jedis);
	}

	@Test
	public void testSimple() {
		User user1 = userService.viewReferenceUser();
		logger.debug("第一次查询结果：" + JSON.toJSONString(user1));
		assertTrue(jedis.exists(CacheKeyPrefix.TEST_CACHE_REFERENCEUSER));
		User user2 = userService.viewReferenceUser();
		logger.debug("第二次查询结果：" + JSON.toJSONString(user2));
		assertEquals(user1, user2);
	}

	@Test
	public void testCacheKeySuffix() {
		User user1 = userService.view(3);
		logger.debug("第一次查询结果：" + JSON.toJSONString(user1));
		assertTrue(jedis.exists(CacheKeyPrefix.TEST_CACHE_USER + ":" + 3));
		User user2 = userService.view(3);
		logger.debug("第二次查询结果：" + JSON.toJSONString(user2));
		assertEquals(user1, user2);
	}

	@Test
	public void testCacheKeySuffix2() {
		Pager pager = new Pager();
		pager.setPageNumber(1);
		pager.setPageSize(5);
		pager.setRecordCount(0);
		List<User> userList1 = userService.listInPage(pager);
		logger.debug("第一次查询结果：" + JSON.toJSONString(userList1));
		String key = CacheKeyPrefix.TEST_CACHE_ALLUSERS_INPAGE + ":"
				+ JSON.toJSONString(pager, SerializerFeature.UseSingleQuotes);
		logger.debug(key);
		assertTrue(jedis.exists(key));
		List<User> userList2 = userService.listInPage(pager);
		logger.debug("第二次查询结果：" + JSON.toJSONString(userList2));
		assertEquals(userList1, userList2);
	}

	@Test
	public void testTimeOut() {
		int count1 = userService.countUser();
		long ttl = jedis.ttl(CacheKeyPrefix.TEST_CACHE_COUNTUSER);
		assertTrue(ttl <= 600 && ttl > 590);
		int count2 = userService.countUser();
		assertEquals(count1, count2);
	}

	@Test
	public void testReturnSet() {
		Set<Integer> userIds1 = userService.userIds();
		logger.debug("第一次查询结果：" + JSON.toJSONString(userIds1));
		Set<Integer> userIds2 = userService.userIds();
		logger.debug("第二次查询结果：" + JSON.toJSONString(userIds2));
		assertEquals(userIds1, userIds2);
	}
}
