package com.xxss.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @ClassName: SysRole
 * @Description: TODO
 * @author lovefamily
 * @date 2018年7月2日 下午3:34:37
 *
 */
@Entity
@Table(name = "role")
public class SysRole {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String comment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
