package com.yanmade.plat.framework.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmRole;

@Component
public interface RoleFunctionMapper {

    @Insert({ "<script>", "insert into sm_role_function (roleId, functionId) values",
            "<foreach collection = 'role.functions' item='item' index='index' separator=','>",
            "(#{role.id}, #{item.id})",
            "</foreach>", "</script>" })
    boolean insert(@Param("role") SmRole role);

    @Delete("delete from sm_role_function where roleId = #{roleId}")
    boolean deleteByRoleId(int roleId);

    @Select("select roleId from sm_role_function where functionId = #{functionId}")
    List<Integer> getRoleIdByFunctionId(int functionId);

}
