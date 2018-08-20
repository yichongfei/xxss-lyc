package com.xxss.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * XXSS网站账号实体类
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "account")
public class Account {
	@Id
	private String id;
	
	private String email;// 邮箱

	private String password;// 密码

	private boolean vip;// 是不是VIP

	private long vipDeadline;// vip截止日期

	
	private String referrer;
	
	private String tuiguangURL;
	
	private int tuiguangTimes;
	
	
	public Account() {
		
	}
	
	public Account(String email,String password) {
		this.id = UUID.randomUUID().toString();
		this.email = email;
		this.password = password;
		this.vip = false;
		this.tuiguangURL =creatTuiguangUrl(id)+"欢迎观看形形色色华人社区,最精彩,等你来";
	}
	public Account(String email,String password,String referrer) {
		this.id = UUID.randomUUID().toString();
		this.email = email;
		this.password = password;
		this.vip = false;
		this.referrer=referrer;
		this.tuiguangURL =creatTuiguangUrl(id)+"欢迎观看形形色色华人社区,最精彩,等你来";
	}
	

	/**
	 * 判断是否过期
	 * 
	 * @return
	 */
	public boolean vipIsAvailable() {
		if (this.vipDeadline - System.currentTimeMillis() > 0) {
			return true;
		}
		return false;
	}

	
	/**
	 * 创建推广链接
	 * @param id
	 * @return
	 */
	public  String creatTuiguangUrl(String id) {
		String tuiguangUrl = "www.xxss2018.com/account/tuiguang?id="+id;
		return tuiguangUrl;
	}
	
	
	/**
	 * 访问有效,推广次数加1;
	 */
	public void updateTuiguangTimes() {
		this.setTuiguangTimes(this.tuiguangTimes+1);
	}
	
	
	
	/**
	 * 用户升级VIP
	 */
	public void updateVip(Card card) {
		if (this.vipDeadline < System.currentTimeMillis()) {
			this.vipDeadline = System.currentTimeMillis() + card.getMonthsMillisecond();
		}else {
			this.vipDeadline = this.vipDeadline + card.getMonthsMillisecond();
		}
		this.vip = true;
	}
	
	
	public void checkTuiguangTimes() {
		if(this.tuiguangTimes ==10) {
			Card card = new Card();
			card.setMonths(1);
			this.updateVip(card);
		}
		if(this.tuiguangTimes ==30) {
			Card card = new Card();
			card.setMonths(2);
			this.updateVip(card);
		}
		if(this.tuiguangTimes ==60) {
			Card card = new Card();
			card.setMonths(3);
			this.updateVip(card);
		}
		if(this.tuiguangTimes ==100) {
			Card card = new Card();
			card.setMonths(6);
			this.updateVip(card);
		}
		
	}
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public long getVipDeadline() {
		return vipDeadline;
	}

	public void setVipDeadline(long vipDeadline) {
		this.vipDeadline = vipDeadline;
	}

	public int getTuiguangTimes() {
		return tuiguangTimes;
	}

	public void setTuiguangTimes(int tuiguangTimes) {
		this.tuiguangTimes = tuiguangTimes;
	}

	public String getTuiguangURL() {
		return tuiguangURL;
	}

	public void setTuiguangURL(String tuiguangURL) {
		this.tuiguangURL = tuiguangURL;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	
	
	
}
