package com.dolplay.nutzcache.dao;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.dolplay.nutzcache.assets.domain.User;
import com.dolplay.nutzcache.assets.utils.IocProvider;

public class AdvancedCacheDaoTest {
	private static Logger logger = LoggerFactory.getLogger(AdvancedCacheDaoTest.class);
	private static AdvancedCacheDao cacheDao;
	private static JedisPool pool;
	private static Jedis jedis;

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

		// 初始化cacheDao
		cacheDao = ioc.get(AdvancedCacheDao.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pool.returnResource(jedis);
	}

	@Test
	public void testZAddStringIntDoubleString() {

	}

	@Test
	public void testZAddStringDoubleString() throws Exception {
		cacheDao.zAdd("test:listAbc", 1, "111");
		cacheDao.zAdd("test:listAbc", 2, "222");
		cacheDao.zAdd("test:listAbc", 3, "333");
		cacheDao.zAdd("test:listAbc", 6, "444");
		cacheDao.zAdd("test:listAbc", 7, "555");
		Set<String> actual = jedis.zrange("test:listAbc", 0, -1);
		Set<String> expected = new HashSet<String>();
		expected.add("111");
		expected.add("222");
		expected.add("333");
		expected.add("444");
		expected.add("555");
		assertEquals(expected, actual);
	}

	@Test
	public void testZQueryByRankStringLongLongOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testZQueryByRankStringLongLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testZQueryByScoreStringDoubleDoubleOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testZQueryByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZQueryAllStringOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testZQueryAllString() {
		fail("Not yet implemented");
	}

	@Test
	public void testZDel() {
		fail("Not yet implemented");
	}

	@Test
	public void testZDelByRank() {
		fail("Not yet implemented");
	}

	@Test
	public void testZDelByScore() {
		fail("Not yet implemented");
	}

}
