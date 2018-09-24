package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * RedisService不需要继承BaseService，因为RedisService和数据库没有关系。
 * 因为导入了Spring Data Redis依赖，里面包含了操作Redis的各种工具，所以这里就不用自己实现的了。
 * Spring Data Redis还可以用于消息的发布和订阅，这一功能用于一旦B端的商品详情或商品类目发生了更新，就会发布消息通知到C端，C端的redis缓存的同步。
 * 详情见：https://blog.csdn.net/jslcylcy/article/details/78201812
 */
@Service
public class MyRedisService {
	
	//required = false：从spring容器中查找redis的bean，如果找的到就注入，找不到就忽略：
	@Autowired(required = false)
	private ShardedJedisPool shardedJedisPool;
	
	/**
	 * 抽取出set、get等方法中相同的代码，而不一样的代码则由function.callback()方法来实现。
	 * function.callback()方法的实现是在set、get等方法中创建的Function接口的匿名内部类中。
	 */
	private <T> T execute(Function<T, ShardedJedis> function) {
		ShardedJedis shardedJedis = null;
		try {
			// 从连接池中获取到jedis分片对象
			shardedJedis = shardedJedisPool.getResource();
			return function.callback(shardedJedis);
		} finally {
			if (null != shardedJedis) {
				// 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
				shardedJedis.close();
			}
		}
	}
	
	/**
	 * 执行set操作。
	 * 这是改进后的set()方法，是将set()、get()中通用的代码抽取出来后的set()方法。
	 */
	public String set(final String key, final String value) {
		return execute(new Function<String, ShardedJedis>() {
			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value);
			}
		});
	}
	
	/**
	 * 执行set操作
	 */
//	public String set(String key, String value) {
//		ShardedJedis shardedJedis = null;
//		try {
//			// 从连接池中获取到jedis分片对象
//			shardedJedis = shardedJedisPool.getResource();
//			return shardedJedis.set(key, value);
//		} finally {
//			if (null != shardedJedis) {
//				// 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
//				shardedJedis.close();
//			}
//		}
//	}
	
	/**
	 * 执行set操作。
	 * 这是改进后的set()方法，是将set()、get()中通用的代码抽取出来后的set()方法。
	 */
	public String get(final String key){
		return this.execute(new Function<String, ShardedJedis>() {
			@Override
			public String callback(ShardedJedis e) {
				return e.get(key);
			}
		});
	}
	
	/**
	 * 执行get操作
	 */
//	public String get(String key) {
//		ShardedJedis shardedJedis = null;
//		try {
//			// 从连接池中获取到jedis分片对象
//			shardedJedis = shardedJedisPool.getResource();
//			return shardedJedis.get(key);
//		} finally {
//			if (null != shardedJedis) {
//				// 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
//				shardedJedis.close();
//			}
//		}
//	}
	
	/**
	 * 执行delete操作
	 */
	public Long del(final String key){
		return this.execute(new Function<Long, ShardedJedis>() {
			@Override
			public Long callback(ShardedJedis e) {
				return e.del(key);
			}
		});
	}
	
	/**
	 * 设置rides中数据的生存时间
	 * Integer seconds:时间：秒。
	 */
	public Long expire(final String key, final Integer seconds){
		return this.execute(new Function<Long, ShardedJedis>() {
			@Override
			public Long callback(ShardedJedis e) {
				return e.expire(key, seconds);
			}
		});
	}
	
	/**
	 * 将数据set到redis的时候，同时设置该数据的生存时间，单位为秒。
	 */
	public String set(final String key, final String value, final Integer seconds){
		return this.execute(new Function<String, ShardedJedis>() {
			@Override
			public String callback(ShardedJedis e) {
				String result = e.set(key, value);
				e.expire(key, seconds);
				return result;
			}
		});
	}
}
