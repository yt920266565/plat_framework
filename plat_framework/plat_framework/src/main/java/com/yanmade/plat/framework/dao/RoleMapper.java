package com.yanmade.plat.framework.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmRole;

@Component
public interface RoleMapper {

	List<SmRole> getRoles(int roleId);

	@Select("select sr.*,GROUP_CONCAT(distinct(su.realname)) as users,GROUP_CONCAT(distinct(sf.description)) as func"
			+ ",GROUP_CONCAT(distinct(sd.name)) as datas,GROUP_CONCAT(distinct(sf.id)) as funcId,"
			+ "GROUP_CONCAT(distinct(sd.id)) as deptId from sm_role sr "
			+ "left join sm_role_function srf on sr.id = srf.roleid left join sm_function sf on srf.functionid = sf.id "
			+ "left join sm_role_department srd on sr.id = srd.roleid left join sm_department sd on srd.departmentid = sd.id "
			+ "left join sm_user_role sur on sr.id = sur.roleid left join sm_user su on su.id = sur.userid "
			+ "group by sr.id limit #{page},#{limit}")
	List<HashMap<String, Object>> getRoleList(Map<String, Object> input);

	@Select("select count(*) from sm_role")
	int getRoleCnt();

	@Select("select * from sm_role where id = #{id}")
	SmRole getRole(int id);

	@Insert("insert into sm_role (id, name, description, createtime) values(#{id}, #{name}, #{description}, #{createTime})")
	boolean insert(SmRole role);

	@Update("update sm_role set name = #{name}, description = #{description}, createtime = #{createTime} where id = #{id}")
	boolean update(SmRole role);

	@Delete("delete from sm_role where id = #{id}")
	boolean delete(int id);

	@Delete("delete from sm_user_role where roleid = #{roleid}")
	boolean deleteUserRole(int roleid);

}
