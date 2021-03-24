package com.yanmade.plat.framework.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;

@Component
public interface UserMapper {
    
    List<SmUser> getUsers(Map<String, Object> map);

    boolean updateUser(SmUser user);

    @Insert({ "<script>",
            "replace into sm_user (id, username, password, realname, email, phone, deptid, createtime, deptName) values",
            "<foreach collection = 'users' item='item' index='index' separator=','>",
            "(#{item.id}, #{item.username}, #{item.password}, #{item.realName}, #{item.email}, #{item.phone}, #{item.deptId}, #{item.createTime}, #{item.deptName})",
            "</foreach>", "</script>" })
    boolean insertBatch(@Param("users") List<SmUser> users);

    @Select("select * from sm_user where username = #{username}")
    SmUser getUserByName(String username);

    @Select("select * from sm_user where id = #{id}")
    SmUser getUserById(int userId);

    @Insert("insert into sm_user (id, username, password, realname, email, phone, deptid, deptName, createtime) \r\n"
            + "values (#{id}, #{username}, #{password}, #{realName}, #{email}, #{phone}, #{deptId}, #{deptName}, #{createTime}) ")
    boolean insert(SmUser user);
    
    @Delete("delete from sm_user where id = #{userId} ")
    boolean deleteUser(int userId);

    @Select("select roleId from sm_user_role where userid = #{userId}")
    List<Integer> getRoleIdsByUserId(int userId);

    @Select("select c.name from sm_user_role a join sm_role_function b on a.roleId = b.roleId "
            + "join sm_function c on b.functionId = c.id "
            + "where a.userId = #{userId}")
    List<String> getFunctionsByUserId(int userId);

    @Select("select * from sm_role a join sm_user_role b on a.id = b.roleId where b.userId = #{userId}")
    List<SmRole> getRolesByUserId(int userId);

    @Select("select count(*) from sm_user_role where userId = #{userId} and roleId = -1")
    int isAdmin(int userId);

    @Select("<script>"
    		+"select group_concat(sur.roleid) as roleid,group_concat(sr.name) as rolename,"
    		+ "su.id,su.username,su.realname,su.email,su.phone,su.deptName,sur.roleid from sm_user su "
    		+ "left join sm_user_role sur on su.id = sur.userid "
    		+ "left join sm_role sr on sr.id = sur.roleid "
    		+"<where> "
    		+"<if test='deptId != null and deptId != \"\"'>and deptId = #{deptId}</if> "
    		+"<if test='userName != null and userName != \"\"'>and su.userName like concat('%',#{userName},'%')</if> "
    		+"<if test='realName != null and realName != \"\"'>and su.realName like concat('%',#{realName},'%')</if> "
    		+"</where> "
    		+"group by su.id "
    		+"limit #{page},#{limit} "
    		+"</script>")
    List<HashMap<String, Object>> getUsersByDeptId(Map<String, Object> input);
    
    @Select("<script>"
    		+"select group_concat(sur.roleid) as roleid,group_concat(sr.name) as rolename,"
    		+ "su.id,su.username,su.realname,su.email,su.phone,su.deptName,sur.roleid from sm_user su "
    		+ "join sm_user_role sur on su.id = sur.userid "
    		+ "join sm_role sr on sr.id = sur.roleid "
    		+"<where> "
    		+"<if test='deptId != null and deptId != \"\"'>and deptId = #{deptId}</if> "
    		+"<if test='userName != null and userName != \"\"'>and su.userName like concat('%',#{userName},'%')</if> "
    		+"<if test='realName != null and realName != \"\"'>and su.realName like concat('%',#{realName},'%')</if> "
    		+"</where> "
    		+"group by su.id "
    		+"limit #{page},#{limit} "
    		+"</script>")
    List<HashMap<String, Object>> getURByDeptId(Map<String, Object> input);
    
    @Select("<script>"
    		+"select count(*) from sm_user"
    		+"<where>"
    		+"<if test='deptId != null and deptId != \"\"'>and deptId = #{deptId}</if>"
    		+"<if test='userName != null and userName != \"\"'>and userName like concat('%',#{userName},'%')</if> "
    		+"<if test='realName != null and realName != \"\"'>and realName like concat('%',#{realName},'%')</if> "
    		+"</where>"
    		+"</script>")
    int getUsersByDepCnt(Map<String, Object> input);
    
    @Select("<script>"
    		+"select count(distinct(su.id)) from sm_user su "
    		+ "join sm_user_role sur on su.id = sur.userid "
    		+ "join sm_role sr on sr.id = sur.roleid "
    		+"<where> "
    		+"<if test='deptId != null and deptId != \"\"'>and deptId = #{deptId}</if> "
    		+"<if test='userName != null and userName != \"\"'>and su.userName like concat('%',#{userName},'%')</if> "
    		+"<if test='realName != null and realName != \"\"'>and su.realName like concat('%',#{realName},'%')</if> "
    		+"</where> "
    		+"</script>")
    int getURByDepCnt(Map<String, Object> input);
    
    @Delete("delete from sm_user")
    boolean delAllUser();
    
    @Select("select GROUP_CONCAT(b.realname) as users from sm_user_role a "
    		+ "join sm_user b on a.userid = b.id where a.roleid = #{roleid}  group by a.roleid ")
    Map<String,Object> getUserByRole(int roleid);
    
}
