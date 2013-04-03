package com.dolplay.nutzcache.interceptor;

import static org.junit.Assert.*;

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

import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.assets.service.UserAdvancedService;
import com.dolplay.nutzcache.assets.utils.IocProvider;

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
	public void test() {

		List<String> ids = userService.listIdByGender("male");
		logger.debug("第一次查询用户结果：" + Json.toJson(ids));
		List<String> ids2 = userService.listIdByGender("male");
		logger.debug("第二次查询用户结果：" + Json.toJson(ids));
		assertEquals(ids, ids2);
	}

}
