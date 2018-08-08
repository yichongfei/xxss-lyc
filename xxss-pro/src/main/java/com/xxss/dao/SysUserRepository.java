package com.xxss.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xxss.entity.SysUser;

/**
 * 
 * @ClassName: SysUserRepository
 * @Description: TODO
 * @author lovefamily
 * @date 2018年6月13日 下午7:21:12
 *
 */
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
	SysUser findByUsername(String username);
}
