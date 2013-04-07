package com.dolplay.nutzcache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dolplay.nutzcache.CacheConfig;
import com.dolplay.nutzcache.type.CacheType;

/**
 * @author Conanca
 * 指明要进行缓存操作的方法的注解
 * 注:方法还需要声明@Aop("cacheInterceptor")注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface Cache {

	/**
	 * 指明预读取/设置哪个缓存
	 * @return
	 */
	public String cacheKeyPrefix() default "";

	/**
	 * 指明缓存类型。根据类型不同，可缓存字符串(任何对象均可被缓存，都将被转为JSON格式的字符串)值和有序集合值
	 * @return
	 */
	public CacheType cacheType() default CacheType.String;

	/**
	 * 指明缓存超时时间(秒)，超过这个时间该缓存将被删除。如果超时时间小于等于0，则为永久缓存。
	 * 缺省则使用配置文件中所配置的超时时间
	 * @return
	 */
	public int cacheTimeout() default CacheConfig.INVALID_TIMEOUT;

	/**
	 * 指明CacheType为List类型时，往缓存中存放有序集时，是否倒转原有顺序。
	 * 缺省不倒转
	 * @return
	 */
	public boolean reverse() default false;

}