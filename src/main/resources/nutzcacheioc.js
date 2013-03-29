var ioc = {

	// 系统参数
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		args : [false],
		fields : {
			paths : [ "nutzcacheconf.properties" ]
		}
	},
	
	jedisPoolConfig : {
		type : 'redis.clients.jedis.JedisPoolConfig',
		fields : {
			maxActive : { java : "$config.getInt('pool-maxActive')" },
			maxIdle : { java : "$config.getInt('pool-maxIdle')" },
			maxWait : { java : "$config.getInt('pool-maxWait')" },
			testOnBorrow : { java : "$config.get('pool-testOnBorrow')" },
			testOnReturn : { java : "$config.get('pool-testOnReturn')" }
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
			{ java : "$config.get('redis-host')" },						// host
			{ java : "$config.getInt('redis-port')" },						// port
			{ java : "$config.getInt('redis-timeout')" },					// timeout
			{ java : "$config.get('redis-password',null)" },				// password
			{ java : "$config.getInt('redis-databaseNumber')" }	// database number
		]
	},
	
	// 配置了cacheDao示例
	cacheDao: {
		type : "com.dolplay.nutzcache.dao.RedisCacheDao",
		args : [	 {refer : 'config'},{refer : 'jedisPool'}]
	},
	
	// 缓存预先读取的方法拦截器配置
	cacheInterceptor: {
		type : "com.dolplay.nutzcache.interceptor.CacheInterceptor",
		fields : {
			cacheDao : {refer : 'cacheDao'}
		}
	},
	
	// 配置了advancedCacheDao示例
	advancedCacheDao: {
		type : "com.dolplay.nutzcache.dao.RedisAdvancedCacheDao",
		args : [	 {refer : 'config'},{refer : 'jedisPool'}]
	},
	
	// 有序集合型缓存预先读取的方法拦截器配置
	advancedCacheInterceptor: {
		type : "com.dolplay.nutzcache.interceptor.AdvancedCacheInterceptor",
		fields : {
			cacheDao : {refer : 'advancedCacheDao'}
		}
	}
};