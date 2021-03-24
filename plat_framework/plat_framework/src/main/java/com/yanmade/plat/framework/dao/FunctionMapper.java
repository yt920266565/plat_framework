package com.yanmade.plat.framework.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmFunction;

@Component
public interface FunctionMapper {

    @Insert("insert into sm_function (id, name, description, url, method, pid) "
            + "values (#{id}, #{name}, #{description}, #{url}, #{method}, #{pid})")
    boolean insert(SmFunction permission);

    @Select("select * from sm_function where id = #{functionId}")
    SmFunction getFunction(int functionId);

    @Select("select roleId from sm_function as a join sm_role_function as b on a.id = b.functionId"
            + " where a.url = #{url} and a.method = #{method}")
    List<Integer> getRoleIdByUrl(String url, String method);
    
    @Select("select * from sm_function")
    List<SmFunction> getFunctions();
    
    @Select("select name from sm_function")
    List<String> getFunctionName();

    @Select("select * from sm_function as a, sm_role_function as b where a.id = b.functionId and b.roleId = #{roleId}")
    List<SmFunction> getFunctionsByRoleId(int roleId);
}
