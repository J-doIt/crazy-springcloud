package com.crazymaker.springcloud.j2cache.testcase;

//import com.crazymaker.l2cache.config.J2CacheAutoConfiguration;
//import com.crazymaker.l2cache.config.J2CacheSpringCacheAutoConfiguration;
//import com.crazymaker.l2cache.manager.CacheChannel;
//import com.crazymaker.l2cache.manager.CacheObject;
import com.crazymaker.springcloud.j2cache.bean.TestBean;
import com.crazymaker.springcloud.j2cache.service.TestCacheService;
import com.crazymaker.springcloud.standard.config.CustomedRedisAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
		TestCacheService.class,
//		J2CacheAutoConfiguration.class,
//		J2CacheSpringCacheAutoConfiguration.class,
		CustomedRedisAutoConfiguration.class },
		properties = {
				"j2cache.config-location=classpath:j2cache-test.properties",
				"spring.cache.type=GENERIC",
				"j2cache.open-spring-cache=true", 
				"j2cache.j2CacheConfig.serialization=json",
				"j2cache.redis-client=jedis", 
				"j2cache.cache-clean-mode=active", 
				"j2cache.allow-null-values=true",
				"j2cache.l2-cache-open=true"
				})
public class CacheServiceTests {

	@Autowired
	private TestCacheService testService;

//	@Autowired
//	private CacheChannel cacheChannel;

	@Test
	public void testCache() throws IOException {
		testService.reset();
		testService.evict();
		testService.getNum();
		testService.getNum();
//		testService.getNum();
//		testService.getNum();
//		Integer n = testService.getNum();
//		Assert.isTrue(n == 1, "缓存未生效！");
		for (int i = 1; i < 200; i++) {
			new Thread(() -> {
				for (int j = 1; j < 1000; j++) {
					Integer n = testService.getNum();
					try {
						Random r = new Random();
						Thread.sleep(r.nextInt(100));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					if(n == null) {
//						System.out.println(Thread.currentThread().getId() + ":出现缓存为null:" + j);	
//					}
				}
			}).start();
		}
		System.in.read();

	}

	@Test
	public void clearCache() {
		testService.reset();
		testService.getNum();
		testService.reset();
		testService.evict();
		Integer a = testService.getNum();
		Assert.isTrue(a == 1, "清除缓存未生效！");
	}

	@Test
	public void beanCache() {
		testService.reset();
		testService.evict();
		testService.testBean();
		TestBean b = testService.testBean();
		Integer a = b.getNum();
		Assert.isTrue(a == 1, "对象缓存未生效！");
	}

//	@Test
//	public void test() {
//		cacheChannel.set("test", "123", "321");
//		CacheObject a = cacheChannel.get("test", "123");
//		Assert.isTrue(a.getValue().equals("321"), "失败！");
//	}
//
//	@Test
//	public void test1() {
////		cacheChannel.set("test", "123", "321");
//		CacheObject a = cacheChannel.get("test", "1233");
//		Assert.isTrue(a.getValue().equals("321"), "失败！");
//	}
}
