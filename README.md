nutz-cache
==========
一个nutz的缓存插件——你的nutz应用可以通过它来方便的用起redis实现的缓存。

Description
---------

* 提供了缓存预先读取及缓存自动设值的方法拦截器
* 提供了用于手动操作缓存的CacheDao
* 支持字符串类型缓存和有序集类型缓存

Usage
---------
1. 在你的nutz应用所配置的ioc加载器中增加一个配置文件“nutzcacheioc.js”，例如：

    @IocBy(type=JsonIocProvider.class, args={"/conf/core.js", "/conf/pet.js", "nutzcacheioc.js"})
    public class MainModule {
      ...

2. 在应用的source forlder下增加一个配置文件cache.properties，内容如下：

    #普通类型缓存有效时间(秒)
    STANDARD_CACHE_TIMEOUT=600
    #有序集缓存有效时间(秒)
    LIST_CACHE_TIMEOUT=3600
    
    pool-maxActive=1024
    pool-maxIdle=200
    pool-maxWait=1000
    pool-testOnBorrow=true
    pool-testOnReturn=true
    
    redis-host=127.0.0.1
    redis-port=6379
    redis-timeout=2000
    #redis-password=
    redis-databaseNumber=0

3. 在一个函数上使用缓存方法拦截器，并通过Cache注解指明缓存名。例如：

    @IocBean(args = { "refer:dao" })
    public class UserService extends IdEntityService<User> {
    	public UserService(Dao dao) {
    		super(dao);
    	}
    
    	/**
    	 * 查询参考用户
    	 */
    	@Aop(InterceptorName.CACHEINTERCEPTOR)
    	@Cache(cacheKeyPrefix = "myapptest:cache:ReferenceUser")
    	public User viewReferenceUser() {
    		return fetch(1);
    	}
    ...

4.启动redis服务器，运行上述函数的test case两次，即可观察到缓存其作用了。
