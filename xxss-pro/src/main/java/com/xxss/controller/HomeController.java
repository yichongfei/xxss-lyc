package com.xxss.controller;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxss.aws.s3.AmazonS3Object;
import com.xxss.config.S3Config;
import com.xxss.dao.AccountService;
import com.xxss.dao.CardService;
import com.xxss.dao.VideoService;
import com.xxss.entity.Account;
import com.xxss.entity.Card;
import com.xxss.entity.Result;
import com.xxss.entity.Video;
import com.xxss.util.IndexCache;

/**
 * 
 * @ClassName: HomeController
 * @Description: TODO
 * @author lovefamily
 * @date 2018年7月2日 下午3:31:17
 *
 */
@Controller
public class HomeController {

	@Autowired
	private VideoService videoService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;
	//每播放一次记录IP
	private static ConcurrentHashMap<String, Integer> playTimes = new ConcurrentHashMap<String, Integer>();
	
	//推广IP的记录
	
	private static ConcurrentHashMap<String,Integer> tuiguangTimes =new ConcurrentHashMap<String, Integer>();
	
	public static long dayEnd = getEndTime();

	@RequestMapping("/")
	public String index(Model model, HttpServletRequest request) {
		return "index";
	}
	
	/**
	 * 获取最新电影的图片
	 * 
	 * @return
	 */
	@RequestMapping("/video/getNewVideoPic")
	@ResponseBody
	public List<Video> getNewVideoPic() {

		if (IndexCache.newVideoPicList.size() != 0) {
			return IndexCache.newVideoPicList;

		}

		Sort sort = new Sort(Direction.DESC, "uploadTime");
		int page = 0, size = 5;
		Pageable pageable = new PageRequest(page, size, sort);
		List<Video> list = videoService.findAll(pageable).getContent();
		return list;
	}

	/**
	 * 获取最多播放的VIDEO
	 * 
	 * @return
	 */
	@RequestMapping("/video/getMostViewedPic")
	@ResponseBody
	public List<Video> getMostViewedPic() {
		if (IndexCache.MostViewedPicList.size() != 0) {
			return IndexCache.MostViewedPicList;

		}

		Sort sort = new Sort(Direction.DESC, "playTimes");
		int page = 0, size = 8;
		Pageable pageable = new PageRequest(page, size, sort);
		List<Video> list = videoService.findAll(pageable).getContent();
		return list;
	}

	/**
	 * 获取最新的国产自拍video
	 * 
	 * @return
	 */
	@RequestMapping("/video/getChinaViewPic")
	@ResponseBody
	public List<Video> getChinaVideoPic() {

		if (IndexCache.ChinaViewPicList.size() != 0) {
			return IndexCache.ChinaViewPicList;
		}

		Sort sort = new Sort(Direction.DESC, "uploadTime");
		int page = 0, size = 8;
		Pageable pageable = new PageRequest(page, size, sort);
		List<Video> list = videoService.findBycategory(pageable, "china");
		return list;

	}

	/**
	 * 获取最新的欧美video
	 * 
	 * @return
	 */
	@RequestMapping("/video/getAmericaViewPic")
	@ResponseBody
	public List<Video> getAmericaViewPic() {
		if (IndexCache.AmericaViewPicList.size() != 0) {
			return IndexCache.AmericaViewPicList;
		}

		Sort sort = new Sort(Direction.DESC, "uploadTime");
		int page = 0, size = 8;
		Pageable pageable = new PageRequest(page, size, sort);
		List<Video> list = videoService.findBycategory(pageable, "america");
		return list;

	}

	/**
	 * 跳转到观看视频页面
	 * 
	 * @return
	 */
	@RequestMapping("/goVideoPlay")
	public String goVideoPlay(HttpServletRequest request, Model model,
			@RequestParam(value = "id", required = false, defaultValue = "") String id) {

		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		if (account != null && account.getVipDeadline() > System.currentTimeMillis()) {
			return "videoPlay";// 会员观看次数不限制
		}

		if (System.currentTimeMillis() > dayEnd) {
			resetPlayTimes();// 每天重置播放次数记录数据
		}

		String addr = getIpAddr(request);
		if (playTimes.containsKey(addr)) {
			if (playTimes.get(addr) > 10) {
				return "videoPlayNon";
			}

			Integer times = playTimes.get(addr) + 1;
			playTimes.put(addr, times);
		} else {
			playTimes.put(addr, 1);
		}
		model.addAttribute("id", id);
		return "videoPlay";

	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}

	private static void resetPlayTimes() {
		dayEnd = getEndTime();
		playTimes.clear();
	}

	/**
	 * 获取要播放的PRE-URL
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/video/getVideo")
	@ResponseBody
	public Video getVideoUrl(String id) {
		Video video = videoService.findById(id);
		String preSignedURL = AmazonS3Object.getPreSignedURL(S3Config.VIDEOBUCKET, video.getMp4Key());
		video.setPreUrl(preSignedURL);
		return video;
	}
	
	/**
	 * 获取要播放的PRE-URL
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/video/getPre")
	@ResponseBody
	public String getVideoPreUrl(String id) {
		Video video = videoService.findById(id);
		String preSignedURL = AmazonS3Object.getPreSignedURL(S3Config.VIDEOBUCKET, video.getMp4Key());
		return preSignedURL;
	}

	
	

	@RequestMapping("/video/updataPlayTimes")
	@ResponseBody
	public void updataPlayTimes(String id) {
		Video video = videoService.findById(id);
		if(video !=null) {
			video.updateIncreasePlayTimes();
			videoService.saveAndFlush(video);
		}
	}

	@RequestMapping("/listVideo/{category}/{page}")
	public String listVideo(@PathVariable(value = "category") String category,
			@PathVariable(value = "page") Integer page, Model model) {
		if (category == null) {
			model.addAttribute("name", "china");
		} else {
			model.addAttribute("name", category);
		}

		Sort sort = new Sort(Direction.DESC, "uploadTime");
		Pageable pageable = new PageRequest(page, 16, sort);
		List<Video> list = videoService.findBycategory(pageable, category);
		model.addAttribute("videos", list);
		model.addAttribute("count", videoService.getCountByCategory(category));
		model.addAttribute("curpage", page);
		return "gallery";
	}

	@RequestMapping("/listNewVideo/{page}")
	public String listNewVideo(@PathVariable(value = "page") Integer page, Model model) {
		Sort sort = new Sort(Direction.DESC, "uploadTime");
		Pageable pageable = new PageRequest(page, 16, sort);
		Page<Video> list = videoService.findAll(pageable);
		model.addAttribute("videos", list);
		model.addAttribute("countpage", list.getTotalPages());
		model.addAttribute("curpage", page);
		return "listNewVideo";
	}

	@RequestMapping("/listHotVideo/{page}")
	public String listHotVideo(@PathVariable(value = "page") Integer page, Model model) {
		Sort sort = new Sort(Direction.DESC, "playTimes");
		Pageable pageable = new PageRequest(page, 16, sort);
		Page<Video> list = videoService.findAll(pageable);
		model.addAttribute("videos", list);
		model.addAttribute("countpage", list.getTotalPages());
		model.addAttribute("curpage", page);
		return "listHotVideo";
	}

	/**
	 * 跳转到注册界面
	 * 
	 * @return
	 */
	@RequestMapping("/register")
	public String signUp() {
		return "pages-sign-up";
	}

	/**
	 * 跳转到登录界面
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	public String login() {
		return "pages-sign-in";
	}

	/**
	 * 注册账号
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	@RequestMapping("/zhuce")
	@ResponseBody
	public Result register(String email, String password) {
		Account findByemail = accountService.findByemail(email);
		Result result = new Result();
		if (findByemail == null) {
			accountService.save(new Account(email, password));
			result.setSuccess(true);
			result.setInformation("注册成功,欢迎来到形形色色");
			return result;
		} else {
			result.setSuccess(false);
			result.setInformation("账号已存在,请您重新注册");
			return result;
		}

	}

	/**
	 * 登录到XXSS
	 * 
	 * @param email
	 *            账号
	 * @param password
	 *            密码
	 * @return
	 */
	@RequestMapping("/loginXXSS")
	@ResponseBody
	public Result loginXXSS(String email, String password, HttpServletRequest request) {
		HttpSession session = request.getSession();
		Account account = accountService.findByemail(email);
		Result result = new Result();
		if (account == null) {
			result.setSuccess(false);
			result.setInformation("登录失败,账号不存在");
			return result;
		} else if (account.getPassword().equals(password)) {
			result.setSuccess(true);
			result.setInformation("登录成功,欢迎回来XXSS,祝您愉快");
			result.setObject(account);
			session.setAttribute("account", account);
			return result;
		} else if (!account.getPassword().equals(password)) {
			result.setSuccess(false);
			result.setInformation("登录失败,您的密码错误");
			return result;
		} else {
			return null;
		}

	}

	/**
	 * 退出登录
	 * 
	 * @param email
	 * @param request
	 * @return
	 */
	@RequestMapping("/exit")
	public String exit(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("account");
		Account account = new Account();
		account.setEmail("游客");
		session.setAttribute("account", account);
		return "index";

	}

	/**
	 * 跳转到充值页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/chongzhi")
	public String chongzhi(HttpServletRequest request) {

		return "chongzhi";

	}

	@RequestMapping("/chongzhivip")
	@ResponseBody
	public Result chongzhivip(String email, String key, String secret, HttpServletRequest request) {
		Result result = new Result();
		Account account = accountService.findByemail(email);
		if (account == null) {
			result.setSuccess(false);
			result.setInformation("账号不存在");
			return result;
		}

		if (!key.equals("") && !secret.equals("")) {
			Card card = cardService.findBykeyWords(key);
			if (card != null && card.getSecret().equals(secret) && card.isAvailable() == true) {
				account.updateVip(card);
				accountService.saveAndFlush(account);
				card.setAvailable(false);
				cardService.saveAndFlush(card);
				result.setSuccess(true);
				result.setObject(account);
				result.setInformation("充值VIP " + card.getMonths() + "个月成功");
				HttpSession session = request.getSession();
				session.setAttribute("account", account);// 更新session中账户信息
				return result;
			}
		}
		result.setSuccess(false);
		result.setInformation("充值失败,请查看卡密是否准确,如有疑问,请联系客服QQ");

		return result;

	}

	@RequestMapping("/information")
	public String information(HttpServletRequest request) {
		// 判断有没有登录,如果没有登录,则返回到登录界面
		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		if (account.getEmail().equals("游客")) {
			return "pages-sign-in";
		}

		return "informationnew";
	}

	@RequestMapping("/tuiguang")
	public String tuiguang(HttpServletRequest request) {
		// 判断有没有登录,如果没有登录,则返回到登录界面
		HttpSession session = request.getSession();
		Account account = (Account) session.getAttribute("account");
		if (account.getEmail().equals("游客")) {
			return "pages-sign-in-tuiguang";
		}

		return "informationnew";

	}

	private static long getStartTime() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTimeInMillis();
	}

	private static long getEndTime() {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTimeInMillis();
	}

	
	/**
	 * 验证推广链接是否有效
	 * @param id
	 */
	@RequestMapping("/account/tuiguang")
	public String checkTuiguangURL(String id,HttpServletRequest request) {
		String addr = getIpAddr(request);
		
		if(tuiguangTimes.containsKey(addr)) {
			return "index";
		}else {
			tuiguangTimes.put(addr, 1);
			HttpSession session = request.getSession();
			Account account = (Account) session.getAttribute("account");
			if (!account.getEmail().equals("游客")) {
				return "index";
			}
			
			
			account = accountService.findByid(id);
			if(account==null) {
				return "index";
			}else {
				account.updateTuiguangTimes();
				account.checkTuiguangTimes();
				accountService.saveAndFlush(account);
				return "index";
			}
		}
		
	}

	@RequestMapping("/tag")
	public String goTag() {
		return "tag";
	}
	
	
	@RequestMapping("/av-girls")
	public String goAvGirls() {
		return "av-girls";
	}
}
