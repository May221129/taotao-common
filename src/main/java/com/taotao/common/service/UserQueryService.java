package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taotao.sso.query.api.service.QueryUserByTokenService;
import com.taotao.sso.query.bean.User;

/**
 * 该类中自动注入QueryUserByTokenService接口，完成远程服务的调用。
 */
@Service
public class UserQueryService {
	
	@Autowired(required=false)
	private QueryUserByTokenService queryUserByTokenService;
	
	/**
	 * 根据token查询用户信息。
	 * 这是使用了dubbo完成远程服务调用：C端或Cart系统调用了taotao-sso-query-service的服务。
	 */
	public User queryUserByToken(String token){
		return this.queryUserByTokenService.queryUserByToken(token);
	}
	
}
