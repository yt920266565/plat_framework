package com.yanmade.plat.framework.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yanmade.plat.framework.dao.AccessRecordMapper;

@Service
public class AccessRecordService {

	@Autowired
	AccessRecordMapper mapper;
	
	@Value("${access.record}")
	private boolean accessRecord;

	public void insert(Map<String, Object> map) {
		if(!accessRecord) {
			return;
		}
		
		String staffCode = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (staffCode.equals("anonymousUser")) {
			return;
		}
		String staffName = (String) mapper.getNameByCode(staffCode).get("realname");
		map.put("staffCode", staffCode);
		map.put("staffName", staffName);

		Map<String, Object> menuMap = mapper.getNameByUrl(map.get("url"));
		if (menuMap == null) {
			map.put("menuName", "打开的页面未命名");
			mapper.insert(map);
			return;
		}
		map.put("menuName", menuMap.get("name"));
		mapper.insert(map);
	}

	public List<Map<String, Object>> getAccessRecords(Map<String, Object> map) {
		pageMap(map);
		timeMap(map);
		return mapper.getAccessRecords(map);
	}

	public int getAccessRecordCnt(Map<String, Object> map) {
		timeMap(map);
		return mapper.getAccessRecordCnt(map);
	}

	private void pageMap(Map<String, Object> map) {
		int page = Integer.parseInt(map.get("page").toString());
		int limit = Integer.parseInt(map.get("limit").toString());
		page = (page - 1) * limit;
		map.put("page", page);
		map.put("limit", limit);
	}
	
	private void timeMap(Map<String, Object> map) {
		LocalDateTime endDate = LocalDateTime.now();
		LocalDateTime startDate = endDate.plusDays(-7);
		String startKey = "startDate";
		map.put("endDate", endDate);
		map.put(startKey, startDate);
		if(map.get("time") == null) {
			return;
		}
		
		String time = map.get("time").toString();
		if(time.equals("oneMonth")) {
			map.put(startKey, endDate.plusMonths(-1));
			return;
		}
		
		if(time.equals("threeMonth")) {
			map.put(startKey, endDate.plusMonths(-3));
			return;
		}
		
		if(time.equals("halfYear")) {
			map.put(startKey, endDate.plusMonths(-6));
			return;
		}
		
		if(time.equals("oneYear")) {
			map.put(startKey, endDate.plusYears(-1));
		}
		
	}
	
}
