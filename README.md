nutz-cache
==========
一个nutz的缓存插件——你的nutz应用可以通过它来方便的用起redis实现的缓存。

介绍
----

* 提供了缓存预先读取及缓存自动设值的方法拦截器
* 提供了用于手动操作缓存的CacheDao
* 支持字符串类型缓存和有序集类型缓存

简单用法
--------
1. 在你的nutz应用项目引入nutz-cache.0.1.x.jar，然后在你所配置的ioc加载器中增加一个配置文件“nutzcacheioc.js”，例如：

    @IocBy(type=JsonIocProvider.class, args={"/conf/core.js", "/conf/pet.js", "nutzcacheioc.js"})  
    public class MainModule {  
    	...

2. 在你的项目的source forlder下增加一个配置文件cache.properties，内容参考该文件：

    [cache.properties](https://github.com/conanca/nutz-cache/blob/master/src/test/resources/cache.properties)

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

4.启动redis服务器，运行上述函数的test case两次，即可观察到缓存起作用了。

进阶
----
请参考如下示例：  
[UserService.java](https://github.com/conanca/nutz-cache/blob/master/src/test/java/com/dolplay/nutzcache/assets/service/UserService.java)  
[UserAdvancedService.java](https://github.com/conanca/nutz-cache/blob/master/src/test/java/com/dolplay/nutzcache/assets/service/UserAdvancedService.java)
