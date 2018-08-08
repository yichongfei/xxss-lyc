package com.xxss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xxss.dao.MenuRepository;
import com.xxss.entity.Menu;

/**
 * 
 * @ClassName: MenuController
 * @Description: TODO
 * @author lovefamily
 * @date 2018年7月2日 下午2:25:21
 *
 */
@RestController
public class MenuController {
	@Autowired
	private MenuRepository menuRepository;

	@RequestMapping(value = "rest/menu/add", method = RequestMethod.POST)
	public void createMenu(@RequestBody Menu menu) {
		menuRepository.save(menu);
	}

	@RequestMapping(value = "rest/menu/{id}", method = RequestMethod.DELETE)
	public void modifyCity(@PathVariable("id") Long id) {
		menuRepository.delete(id);
	}

	@RequestMapping(value = "rest/menu/list", method = RequestMethod.GET)
	public List<Menu> findAllMenu() {
		return (List<Menu>) menuRepository.findAll();
	}
}
