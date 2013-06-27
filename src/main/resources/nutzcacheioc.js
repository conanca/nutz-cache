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

	si1:{
		type : 'redis.clients.jedis.JedisShardInfo',
		args : [ 
		        	{ java : "$cacheProp.get('redis1-host','127.0.0.1')" },
		        	{ java : "$cacheProp.getInt('redis1-port',6379)" },
					{ java : "$cacheProp.getInt('redis1-timeout',2000)" },
					{ java : "$cacheProp.get('redis1-name','redis1')" }	
		         ],
		fields : {
			password : { java : "$cacheProp.get('redis1-password',null)" }
		}
	},

	jedisPool : {
		type : 'redis.clients.jedis.ShardedJedisPool',
		events : {
			depose : 'destroy'
		},
		args : [ 
		    {
				refer : 'jedisPoolConfig'
			},
			[
			 {refer:'si1'}
			 ]
		]
	},

	// 配置了 cacheDao 实例
	cacheDao: {
		type : "com.dolplay.nutzcache.dao.RedisCacheDao",
		args : [	 {refer : 'jedisPool'}]
	},

	// 字符串型缓存预先读取的方法拦截器配置
	cacheInterceptor: {
		type : "com.dolplay.nutzcache.interceptor.CacheInterceptor",
		fields : {
			cacheDao : {refer : 'advancedCacheDao'},
			cacheProp : {refer : 'cacheProp'}
		}
	},

	// 配置了 advancedCacheDao 实例
	advancedCacheDao: {
		type : "com.dolplay.nutzcache.dao.RedisAdvancedCacheDao",
		args : [{refer : 'jedisPool'}]
	},

	// 有序集合型缓存预先读取的方法拦截器配置
	advancedCacheInterceptor: {
		type : "com.dolplay.nutzcache.interceptor.AdvancedCacheInterceptor",
		fields : {
			cacheDao : {refer : 'advancedCacheDao'},
			cacheProp : {refer : 'cacheProp'}
		}
	}
};