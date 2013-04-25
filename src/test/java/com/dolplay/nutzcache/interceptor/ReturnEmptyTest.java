package com.dolplay.nutzcache.interceptor;

import java.util.Collection;
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
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.alibaba.fastjson.JSON;
import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.assets.service.TestReturnEmptyService;
import com.dolplay.nutzcache.assets.utils.IocProvider;

public class ReturnEmptyTest {
	private static Logger logger = LoggerFactory.getLogger(ReturnEmptyTest.class);
	private static ShardedJedisPool pool;
	private static ShardedJedis jedis;
	private static TestReturnEmptyService testService;

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
		pool = ioc.get(ShardedJedisPool.class, "jedisPool");
		jedis = pool.getResource();
		Collection<Jedis> jedisColl = jedis.getAllShards();
		for (Jedis jedis : jedisColl) {
			jedis.flushAll();
		}

		// 初始化UserService
		testService = ioc.get(TestReturnEmptyService.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pool.returnResource(jedis);
	}

	@Test
	public void testSimple() {
		User u1 = testService.viewXobj();
		logger.debug("第一次查询结果：" + JSON.toJSONString(u1));
		User u2 = testService.viewXobj();
		logger.debug("第二次查询结果：" + JSON.toJSONString(u2));
		User u3 = testService.viewXobj();
		logger.debug("第三次查询结果：" + JSON.toJSONString(u3));
	}

	@Test
	public void testSimple1() {
		List l1 = testService.xlist();
		logger.debug("第一次查询结果：" + JSON.toJSONString(l1));
		List l2 = testService.xlist();
		logger.debug("第二次查询结果：" + JSON.toJSONString(l2));
		List l3 = testService.xlist();
		logger.debug("第三次查询结果：" + JSON.toJSONString(l3));
	}

}
