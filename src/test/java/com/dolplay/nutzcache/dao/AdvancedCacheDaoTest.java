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
import org.nutz.lang.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzcache.Order;
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
	public void testZAddStringIntDoubleString() throws Exception {
		cacheDao.zAdd("test:listCba", 300, 1, "111");
		cacheDao.zAdd("test:listCba", 300, 2, "222");
		cacheDao.zAdd("test:listCba", 300, 3, "333");
		cacheDao.zAdd("test:listCba", 300, 6, "444");
		cacheDao.zAdd("test:listCba", 300, 7, "555");
		assertTrue(300L >= jedis.ttl("test:listCba"));
		Set<String> actual = jedis.zrange("test:listCba", 0, -1);
		Set<String> expected = new HashSet<String>();
		expected.add("111");
		expected.add("222");
		expected.add("333");
		expected.add("444");
		expected.add("555");
		assertEquals(expected, actual);

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
	public void testZQueryByRankStringLongLongOrder() throws Exception {
		jedis.zadd("test:listMmm", 1245, "111");
		jedis.zadd("test:listMmm", 2245, "222");
		jedis.zadd("test:listMmm", 3165, "333");
		jedis.zadd("test:listMmm", 3215, "444");
		jedis.zadd("test:listMmm", 3216, "555");
		jedis.zadd("test:listMmm", 3217, "666");
		jedis.zadd("test:listMmm", 3311, "777");
		jedis.zadd("test:listMmm", 4012, "888");
		List<String> list = cacheDao.zQueryByRank("test:listMmm", 2, 5, Order.Desc);
		assertEquals(Lang.list("666", "555", "444", "333"), list);
	}

	@Test
	public void testZQueryByRankStringLongLong() throws Exception {
		jedis.zadd("test:listNnn", 1245, "111");
		jedis.zadd("test:listNnn", 2245, "222");
		jedis.zadd("test:listNnn", 3165, "333");
		jedis.zadd("test:listNnn", 3215, "444");
		jedis.zadd("test:listNnn", 3216, "555");
		jedis.zadd("test:listNnn", 3217, "666");
		jedis.zadd("test:listNnn", 3311, "777");
		jedis.zadd("test:listNnn", 4012, "888");
		List<String> list = cacheDao.zQueryByRank("test:listNnn", 3, 5);
		assertEquals(Lang.list("444", "555", "666"), list);
	}

	@Test
	public void testZQueryByScoreStringDoubleDoubleOrder() throws Exception {
		jedis.zadd("test:listPpp", 1245, "111");
		jedis.zadd("test:listPpp", 2245, "222");
		jedis.zadd("test:listPpp", 3165, "333");
		jedis.zadd("test:listPpp", 3215, "444");
		jedis.zadd("test:listPpp", 3216, "555");
		jedis.zadd("test:listPpp", 3217, "666");
		jedis.zadd("test:listPpp", 3311, "777");
		jedis.zadd("test:listPpp", 4012, "888");
		List<String> list = cacheDao.zQueryByScore("test:listPpp", 3000, 3999, Order.Desc);
		assertEquals(Lang.list("777", "666", "555", "444", "333"), list);
	}

	@Test
	public void testZQueryByScoreStringDoubleDouble() throws Exception {
		jedis.zadd("test:listQqq", 1245, "111");
		jedis.zadd("test:listQqq", 2245, "222");
		jedis.zadd("test:listQqq", 3165, "333");
		jedis.zadd("test:listQqq", 3215, "444");
		jedis.zadd("test:listQqq", 3216, "555");
		jedis.zadd("test:listQqq", 3217, "666");
		jedis.zadd("test:listQqq", 3311, "777");
		jedis.zadd("test:listQqq", 4012, "888");
		List<String> list = cacheDao.zQueryByScore("test:listQqq", 3000, 3999);
		assertEquals(Lang.list("333", "444", "555", "666", "777"), list);
	}

	@Test
	public void testZQueryAllStringOrder() throws Exception {
		jedis.zadd("test:list1", 1245, "111");
		jedis.zadd("test:list1", 2245, "222");
		jedis.zadd("test:list1", 3165, "333");
		jedis.zadd("test:list1", 3215, "444");
		jedis.zadd("test:list1", 3216, "555");
		jedis.zadd("test:list1", 3217, "666");
		jedis.zadd("test:list1", 3311, "777");
		jedis.zadd("test:list1", 4012, "888");
		List<String> list = cacheDao.zQueryAll("test:list1", Order.Desc);
		assertEquals(Lang.list("888", "777", "666", "555", "444", "333", "222", "111"), list);
	}

	@Test
	public void testZQueryAllString() throws Exception {
		jedis.zadd("test:list2", 1245, "111");
		jedis.zadd("test:list2", 2245, "222");
		jedis.zadd("test:list2", 3165, "333");
		jedis.zadd("test:list2", 3215, "444");
		jedis.zadd("test:list2", 3216, "555");
		jedis.zadd("test:list2", 3217, "666");
		jedis.zadd("test:list2", 3311, "777");
		jedis.zadd("test:list2", 4012, "888");
		List<String> list = cacheDao.zQueryAll("test:list2");
		assertEquals(Lang.list("111", "222", "333", "444", "555", "666", "777", "888"), list);
	}

	@Test
	public void testZDel() throws Exception {
		jedis.zadd("test:list3", 1245, "111");
		jedis.zadd("test:list3", 2245, "222");
		jedis.zadd("test:list3", 3165, "333");
		jedis.zadd("test:list3", 3215, "444");
		jedis.zadd("test:list3", 3216, "555");
		jedis.zadd("test:list3", 3217, "666");
		jedis.zadd("test:list3", 3311, "777");
		jedis.zadd("test:list3", 4012, "888");
		cacheDao.zDel("test:list3", "111", "222");
		assertTrue(6 == jedis.zcount("test:list3", 0, 10000));
	}

	@Test
	public void testZDelByRank() throws Exception {
		jedis.zadd("test:list4", 1245, "111");
		jedis.zadd("test:list4", 2245, "222");
		jedis.zadd("test:list4", 3165, "333");
		jedis.zadd("test:list4", 3215, "444");
		jedis.zadd("test:list4", 3216, "555");
		jedis.zadd("test:list4", 3217, "666");
		jedis.zadd("test:list4", 3311, "777");
		jedis.zadd("test:list4", 4012, "888");
		cacheDao.zDelByRank("test:list4", 3, 5);
		assertTrue(5 == jedis.zcount("test:list4", 0, 10000));
	}

	@Test
	public void testZDelByScore() throws Exception {
		jedis.zadd("test:list5", 1245, "111");
		jedis.zadd("test:list5", 2245, "222");
		jedis.zadd("test:list5", 3165, "333");
		jedis.zadd("test:list5", 3215, "444");
		jedis.zadd("test:list5", 3216, "555");
		jedis.zadd("test:list5", 3217, "666");
		jedis.zadd("test:list5", 3311, "777");
		jedis.zadd("test:list5", 4012, "888");
		cacheDao.zDelByScore("test:list5", 3000, 3999);
		assertTrue(3 == jedis.zcount("test:list5", 0, 10000));
	}

}
