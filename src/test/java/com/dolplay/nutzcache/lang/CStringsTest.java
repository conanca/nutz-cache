package com.dolplay.nutzcache.lang;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;

import com.dolplay.nutzcache.assets.domain.User;

public class CStringsTest {

	@Test
	public void testCacheKeyStringStringArray() {
		String cacheKey = CStrings.cacheKey("cache:test1", "a1", "xx", "110");
		System.out.println(cacheKey);
		assertEquals("cache:test1:a1:xx:110", cacheKey);
	}

	@Test
	public void testCacheKeyStringIntArray() {
		String cacheKey = CStrings.cacheKey("cache:test1", 455, 33, 231, 11);
		System.out.println(cacheKey);
		assertEquals("cache:test1:455:33:231:11", cacheKey);
	}

	@Test
	public void testCommonCacheKey() {
		List<Integer> l = Lang.array2list(Lang.array(1, 3, 55, 6));
		User u = new User();
		u.setId(1L);
		u.setName("jack");
		String cacheKey = CStrings.commonCacheKey("cache:test1", l, u, "110x1", 13);
		System.out.println(cacheKey);
		assertEquals("cache:test1:[1,3,55,6]:{'id':1,'name':'jack'}:110x1:13", cacheKey);
	}

}
