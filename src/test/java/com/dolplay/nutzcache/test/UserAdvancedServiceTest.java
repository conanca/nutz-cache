package com.dolplay.nutzcache.test;

import java.text.SimpleDateFormat;
import java.util.List;

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

import com.dolplay.nutzcache.test.domain.User;
import com.dolplay.nutzcache.test.service.UserAdvancedService;

public class UserAdvancedServiceTest {
	private static Logger logger = LoggerFactory.getLogger(UserAdvancedServiceTest.class);
	private UserAdvancedService userService;

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

		// 清空redis
		logger.info("清空redis...");
		JedisPool pool = ioc.get(JedisPool.class, "jedisPool");
		Jedis jedis = pool.getResource();
		jedis.flushDB();
		pool.returnResource(jedis);
	}

	@Test
	public void testCache() throws Exception {
		Ioc ioc = IocProvider.ioc();
		// 获取UserService示例
		userService = ioc.get(UserAdvancedService.class);

		logger.info("第一次执行view方法(获取一个user)...");
		userService.view(2);
		logger.info("第二次执行view方法(获取一个user)...");
		User user = userService.view(2);
		logger.info(Json.toJson(user));

		logger.info("第一次执行listInPage方法...");
		List<User> users = userService.listInPage(userService.dao().createPager(1, 5));
		logger.info("userlist个数：" + users.size());
		logger.info("第二次执行listInPage方法...");
		users = userService.listInPage(userService.dao().createPager(1, 5));
		logger.info("userlist个数：" + users.size());

		logger.info("第一次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());
		logger.info("第二次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());

		logger.info("插入新的user...");
		user = new User();
		user.setName("张三");
		user.setDescription("just for test");
		userService.insert(user);
		logger.info("第三次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());
		logger.info("清除ALLUSERS缓存");
		userService.delAllUsersCache();
		logger.info("第四次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());

		logger.info("获取一个user并更新字段...");
		user = userService.view(3);
		user.setDescription("testttttttttttttttttttttttt");
		userService.update(3, user);
		user = userService.view(3);
		logger.info("更新后查到的user：" + Json.toJson(user));

		logger.info("删除一个user...");
		userService.remove(3);
		logger.info("尝试查询这个user...");
		user = userService.view(3);
		logger.info("查到的user:" + Json.toJson(user));
	}

	@Test
	public void testAdvancedCache() throws Exception {
		Ioc ioc = IocProvider.ioc();
		// 获取UserService示例
		userService = ioc.get(UserAdvancedService.class);

		logger.info("第一次执行listIdByGender方法...");
		List<String> userIdList = userService.listIdByGender("female");
		logger.info("查到的userIdList:" + Json.toJson(userIdList));
		logger.info("第二次执行listIdByGender方法...");
		userIdList = userService.listIdByGender("female");
		logger.info("查到的userIdList:" + Json.toJson(userIdList));

		logger.info("第一次执行oldUserListId方法...");
		userIdList = userService.oldUserListId();
		logger.info("查到的userIdList:" + Json.toJson(userIdList));
		logger.info("第二次执行oldUserListId方法...");
		userIdList = userService.oldUserListId();
		logger.info("查到的userIdList:" + Json.toJson(userIdList));

		logger.info("添加一个用户");
		User user = new User();
		user.setName("张三");
		user.setGender("female");
		user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2003-01-03"));
		userService.insertAndUpdateCache(user);
		logger.info("第三次执行oldUserListId方法...");
		userIdList = userService.oldUserListId();
		logger.info("查到的userIdList:" + Json.toJson(userIdList));

		logger.info("删除一个用户");
		user = userService.fetch(10);
		userService.deleteAndUpdateCache(user);
		logger.info("第四次执行oldUserListId方法...");
		userIdList = userService.oldUserListId();
		logger.info("查到的userIdList:" + Json.toJson(userIdList));
	}
}
