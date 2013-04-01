package com.dolplay.nutzcache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dolplay.nutzcache.dao.AdvancedCacheDaoTest;
import com.dolplay.nutzcache.dao.CacheDaoTest;

@RunWith(Suite.class)
@SuiteClasses({ CacheDaoTest.class, AdvancedCacheDaoTest.class })
public class AllTests {

}
