var ioc = {

	// 系统参数
	cacheProp : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		args : [false],
		fields : {
			paths : [ "cache.properties" ]
		}
	},
	
	jedisPoolConfig : {
		type : 'redis.clients.jedis.JedisPoolConfig',
		fields : {
			maxActive : { java : "$cacheProp.getInt('pool-maxActive',1024)" },
			maxIdle : { java : "$cacheProp.getInt('pool-maxIdle',200)" },
			maxWait : { java : "$cacheProp.getInt('pool-maxWait',1000)" },
			testOnBorrow : { java : "$cacheProp.get('pool-testOnBorrow',true)" },
			testOnReturn : { java : "$cacheProp.get('pool-testOnReturn',true)" }
		}
	},
	
	jedisPool : {
		type : 'redis.clients.jedis.JedisPool',
		events : {
			depose : 'destroy'
		},
		args : [ 
		    {
				refer : 'jedisPoolConfig'
			},
			{ java : "$cacheProp.get('redis-host','127.0.0.1')" },						// host
			{ java : "$cacheProp.getInt('redis-port',6379)" },						// port
			{ java : "$cacheProp.getInt('redis-timeout',2000)" },					// timeout
			{ java : "$cacheProp.get('redis-password',null)" },				// password
			{ java : "$cacheProp.getInt('redis-databaseNumber',0)" }	// database number
		]
	},
	
	// 配置了cacheDao示例
	cacheDao: {
		type : "com.dolplay.nutzcache.dao.RedisCacheDao",
		args : [	 {refer : 'cacheProp'},{refer : 'jedisPool'}]
	},
	
	// 字符串型缓存预先读取的方法拦截器配置
	cacheInterceptor: {
		type : "com.dolplay.nutzcache.interceptor.CacheInterceptor",
		fields : {
			cacheDao : {refer : 'cacheDao'}
		}
	},
	
	// 配置了advancedCacheDao示例
	advancedCacheDao: {
		type : "com.dolplay.nutzcache.dao.RedisAdvancedCacheDao",
		args : [	 {refer : 'cacheProp'},{refer : 'jedisPool'}]
	},
	
	// 有序集合型缓存预先读取的方法拦截器配置
	advancedCacheInterceptor: {
		type : "com.dolplay.nutzcache.interceptor.AdvancedCacheInterceptor",
		fields : {
			cacheDao : {refer : 'advancedCacheDao'}
		}
	}
};