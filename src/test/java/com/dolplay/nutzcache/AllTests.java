package com.dolplay.nutzcache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dolplay.nutzcache.dao.AdvancedCacheDaoTest;
import com.dolplay.nutzcache.dao.CacheDaoTest;
import com.dolplay.nutzcache.interceptor.AdvancedCacheInterceptorTest;
import com.dolplay.nutzcache.interceptor.CacheInterceptorTest;
import com.dolplay.nutzcache.interceptor.ReturnEmptyTest;
import com.dolplay.nutzcache.lang.CStringsTest;

@RunWith(Suite.class)
@SuiteClasses({ CacheDaoTest.class, AdvancedCacheDaoTest.class, CacheInterceptorTest.class,
		AdvancedCacheInterceptorTest.class, ReturnEmptyTest.class, CStringsTest.class })
public class AllTests {

}
