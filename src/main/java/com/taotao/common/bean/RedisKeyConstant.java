package com.taotao.common.bean;

/**
 * 该类专门用于存放redis的key
 * Redis中key的命名规则：项目名_模块名_业务名
 */
public class RedisKeyConstant {
	
	//B端的Redis-key:
	public static final String API_CONTENT_KEY = "TAOTAO-MANEGE_CONTENT_API-CONTENT";
	public static final String API_ITEM_CAT_GET_KEY = "TAOTAO-MANEGE_ITEM-CAT_API-ITEM-CAT-GET";//这里key的命名规则：项目名_模块名_业务名
	
	//C端的Redis-key:
	public static final String ITEM_KEY = "TAOTAO-WEB_ITEM_ITEM";
	public static final String ITEM_DESC_KEY = "TAOTAO-WEB_ITEM_ITEM-DESC";
	public static final String ITEM_PARAM_ITEM_KEY = "TAOTAO-WEB_ITEM_ITEM-PARAM-ITEM";
	
	//SSO端存放到Redis中的token：
	/**
	 * 将token存放到Redis中：
	 * 1.为了方便统计Redis中有多少token；
	 * 2.也出于安全考虑（详情见：String com.taotao.sso.service.UserService.doLogin(String username, String password) throws Exception）
	 */
	public static String getToken(String src){
		return "TOKEN_" + src;
	}
	/**
	 * 将userId作为key，value是token的key。
	 * 一旦用户在30分钟内进行重复登录，就可以根据userId的value——标记token的key值，来删除token。
	 * 一个Redis中有无效的很多token，挤爆Redis的内存。
	 */
	public static String getUserId(Long userId){
		return userId.toString();
	}
}
