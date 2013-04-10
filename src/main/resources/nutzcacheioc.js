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
			maxActive : { java : "$cacheProp.getInt('pool-maxActive')" },
			maxIdle : { java : "$cacheProp.getInt('pool-maxIdle')" },
			maxWait : { java : "$cacheProp.getInt('pool-maxWait')" },
			testOnBorrow : { java : "$cacheProp.get('pool-testOnBorrow')" },
			testOnReturn : { java : "$cacheProp.get('pool-testOnReturn')" }
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
			{ java : "$cacheProp.get('redis-host')" },						// host
			{ java : "$cacheProp.getInt('redis-port')" },						// port
			{ java : "$cacheProp.getInt('redis-timeout')" },					// timeout
			{ java : "$cacheProp.get('redis-password',null)" },				// password
			{ java : "$cacheProp.getInt('redis-databaseNumber')" }	// database number
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