package com.yanmade.plat.framework.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface AccessRecordMapper {
	
	@Insert("insert into publish.access_record(staffCode,staffName,url,accessTime,menuName) "
			+ "values(#{staffCode},#{staffName},#{url},NOW(),#{menuName})")
	void insert(Map<String, Object> map);
	
	@Select("select * from  publish.access_record where url = #{url} and accessTime between #{startDate} and #{endDate} limit #{page},#{limit}")
	List<Map<String, Object>> getAccessRecords(Map<String, Object> map);
	
	@Select("select count(*) from  publish.access_record where url = #{url} and accessTime between #{startDate} and #{endDate}")
	int getAccessRecordCnt(Map<String, Object> map);
	
	@Select("select name from view_menu_name_url where url = #{url}")
	Map<String, Object> getNameByUrl(Object url);
	
	@Select("select realname from sm_user where username = #{staffCode}")
	Map<String, Object> getNameByCode(String staffCode);

}
