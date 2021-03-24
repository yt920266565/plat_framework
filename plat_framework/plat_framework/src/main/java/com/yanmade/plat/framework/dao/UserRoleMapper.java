package com.yanmade.plat.framework.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmUser;

@Component
public interface UserRoleMapper {

    @Insert({ "<script>",
            "insert into sm_user_role (userId, roleId) values",
            "<foreach collection = 'user.roles' item='item' index='index' separator=','>",
            "(#{user.id}, #{item.id})",
            "</foreach>",
            "</script>" })
    boolean insert(@Param("user") SmUser user);

    @Insert({ "<script>", "replace into sm_user_role (userId, roleId) values",
            "<foreach collection = 'users' item='item' index='index' separator=','>", "(#{item.id}, #{roleId})",
            "</foreach>", "</script>" })
    boolean insertBatch(@Param("users") List<SmUser> users, @Param("roleId") int roleId);

    @Delete("delete from sm_user_role where userId = #{userId}")
    boolean deleteByUserId(int userId);

    @Delete("delete from sm_user_role where roleId = #{roleId}")
    boolean deleteByRoleId(int roleId);

    @Select("select userId from sm_user_role where roleId = #{roleId}")
    List<Integer> getUserIdsByRoleId(int roleId);
    
    @Delete("<script> "+
    		"delete from sm_user_role where userid in "+
    		"<foreach item=\"item\" index=\"index\" collection=\"userid\"  open=\"(\" separator=\",\" close=\")\"> "+ 
    		"#{item} "+
    	    "</foreach> "+
    	    "</script> ")
    boolean batDelete(Map<String, Object> input);
}
