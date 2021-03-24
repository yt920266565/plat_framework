package com.yanmade.plat.framework.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmDepartment;

@Component
public interface DepartmentMapper {

    @Select("select * from sm_department")
    List<SmDepartment> getDepartments();
    
    @Select("select distinct(sd.id),sd.name,sd.pid from sm_user su join sm_user_role sur on su.id = sur.userid "
    		+ "join sm_department sd on su.deptid = sd.id")
    List<SmDepartment> getURDepartments();

    @Insert("<script> " + "replace into sm_department (id, name, description, pid) values "
            + "<foreach collection = 'departments' item='item' index='index' separator=','> "
            + "(#{item.id}, #{item.name}, #{item.description}, #{item.pid}) " + "</foreach> " + "</script>")
    boolean insertBatch(@Param("departments") List<SmDepartment> departmentList);
    

}
