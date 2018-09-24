package com.taotao.common.service;

/**
 * 该接口用于com.taotao.manage.service.RedisService中set()、get()、delete()等方法中相同代码的抽取。
 */
public interface Function <T, E> {
	/**
	 * 
	 * @param e
	 * @return T，根据具体方法的返回值而定，如：set()方法的返回值是String，那么它也就是String。
	 */
	public T callback(E e);
}
