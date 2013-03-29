package com.dolplay.nutzcache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Conanca
 * 指明作为缓存名后缀的形参的注解
 * 缓存方法拦截器将会把所有这样的形参的值转成字符串，按照顺序从左往右拼接起来作为缓存名的后缀
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Documented
public @interface CacheKeySuffix {
}
