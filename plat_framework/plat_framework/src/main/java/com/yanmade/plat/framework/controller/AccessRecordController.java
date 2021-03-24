package com.yanmade.plat.framework.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanmade.plat.framework.service.AccessRecordService;

import java.util.List;
import java.util.Map;

/**
 * @author 0103379 首页controller
 */
@RestController
@RequestMapping("/accessRecord")
public class AccessRecordController {

	@Autowired
	AccessRecordService service;

	@GetMapping
	public Map<String, Object> getAccessRecords(@RequestParam Map<String, Object> map) {
		List<Map<String, Object>> list = service.getAccessRecords(map);
		int count = service.getAccessRecordCnt(map);
		map.clear();
		map.put("data", list);
		map.put("count", count);
		map.put("code", 0);
		return map;
	}

	@PostMapping
	public void insert(@RequestBody Map<String, Object> map) {
		service.insert(map);
	}

}
