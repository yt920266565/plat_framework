package com.yanmade.plat.framework.dao;

import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmRole;

@Component
public interface RoleDepartmentMapper {

    @Insert({ "<script>", "insert into sm_role_department (roleId, departmentId) values",
            "<foreach collection = 'role.departments' item='item' index='index' separator=','>",
            "(#{role.id}, #{item.id})", "</foreach>", "</script>" })
    public boolean insert(@Param("role") SmRole role);

    @Delete("delete from sm_role_department where roleId = #{roleId}")
    public boolean deleteByRoleId(int roleId);

    @Select("select d.departmentId from sm_user_role a "
            + "join sm_role_function b on a.roleId = b.roleId "
            + "join sm_function c on b.functionId = c.id "
            + "join sm_role_department d on b.roleId = d.roleId "
            + "where a.userId = #{userId} and c.name = #{functionName}")
    Set<Integer> getDepartmentIds(int userId, String functionName);
}
