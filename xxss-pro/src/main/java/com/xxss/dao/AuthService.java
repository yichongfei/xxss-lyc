package com.xxss.dao;

import com.xxss.entity.SysUser;

/**
 * 
 * @ClassName: AuthService
 * @Description: TODO
 * @author lovefamily
 * @date 2018年6月13日 下午7:21:23
 *
 */
public interface AuthService {
	SysUser register(SysUser userToAdd);

	String login(String username, String password);

	String refresh(String oldToken);
}
