package com.yanmade.plat.framework.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yanmade.plat.framework.dao.RoleDepartmentMapper;
import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.dao.UserRoleMapper;
import com.yanmade.plat.framework.util.RedisUtil;
import com.yanmade.plat.framework.util.RemoteUtil;

@Service
public class CacheService {

    private static final String ADMIN_KEY = "admin";

    private static final String PREFIX_KEY = "USER:";

    @Autowired
    private UserMapper mapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RoleDepartmentMapper roleDepartmentMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RemoteUtil remoteUtil;

    /**
     * 刷新指定用户的功能权限
     * 
     * @param userId
     */
    public void refreshUserFunctionsCache(int userId) {
        String userFunctionKey = getUserFunctionsKey(userId);
        List<String> nameList = mapper.getFunctionsByUserId(userId);
        if (!nameList.isEmpty()) {
            redisUtil.delete(userFunctionKey);
            redisUtil.sSet(userFunctionKey, nameList.toArray());
        }
    }

    private String getUserFunctionsKey(int userId) {
        return PREFIX_KEY + userId + ":FUNCTION";
    }

    /**
     * 缓存指定用户的所有功能的数据权限
     * 
     * @param userId
     */
    public void refreshUserFunctionDepartmentsCache(int userId) {
        String userFunctionKey = getUserFunctionsKey(userId);
        Set<Object> functionNames = redisUtil.sGet(userFunctionKey);
        for (Object name : functionNames) {
            String functionName = (String) name;
            Set<Integer> departmentIds = roleDepartmentMapper.getDepartmentIds(userId, functionName);

            String key = getFunctionDepartmentsKey(userId, functionName); // 先删除再插入
            redisUtil.delete(key);
            redisUtil.set(key, StringUtils.join(departmentIds, ","));
        }
    }

    /**
     * 删除用户缓存
     * 
     * @param userId
     */
    public void deleteCache(int userId) {
        // 删除用户是否是管理员缓存
        String userIsAdminKey = getUserIsAdminCacheKey(userId);
        redisUtil.delete(userIsAdminKey);

        String userFunctionsKey = getUserFunctionsKey(userId);
        Set<Object> set = redisUtil.sGet(userFunctionsKey);
        for (Object name : set) {
            String key = getFunctionDepartmentsKey(userId, (String) name);
            redisUtil.delete(key);
        }
        redisUtil.delete(userFunctionsKey);
    }
    
    /**
     * 批量删除用户缓存
     * 
     * @param userId
     */
    public void deleteCache(Map<String, Object> input) {
    	ArrayList<Integer> list = (ArrayList<Integer>) input.get("userid");
    	for(int i=0;i<list.size();i++) {
    		int userId = list.get(i);
    		// 删除用户是否是管理员缓存
    		String userIsAdminKey = getUserIsAdminCacheKey(userId);
    		redisUtil.delete(userIsAdminKey);
    		
    		String userFunctionsKey = getUserFunctionsKey(userId);
    		Set<Object> set = redisUtil.sGet(userFunctionsKey);
    		for (Object name : set) {
    			String key = getFunctionDepartmentsKey(userId, (String) name);
    			redisUtil.delete(key);
    		}
    		redisUtil.delete(userFunctionsKey);
    	}
    }

    private String getFunctionDepartmentsKey(int userId, String functionName) {
        return PREFIX_KEY + userId + ":FUNCTION:" + functionName;
    }
    
    /**
     * 刷新角色对应所有用户的缓存
     * 
     * @param roleId
     */
    public void refreshRoleUsersCache(int roleId) {
        List<Integer> userIds = userRoleMapper.getUserIdsByRoleId(roleId);
        for (int userId : userIds) {
            refreshUserFunctionsCache(userId);
            refreshUserFunctionDepartmentsCache(userId);
        }
    }

    /**
     * 刷新 用户是否是管理员 缓存
     * 
     * @param userId
     */
    public void refreshUserIsAdminCache(int userId) {
        String key = getUserIsAdminCacheKey(userId);
        boolean isAdmin = mapper.isAdmin(userId) > 0;

        Map<Object, Object> map = new HashMap<>();
        map.put(ADMIN_KEY, isAdmin);

        redisUtil.delete(key);
        redisUtil.hashSet(key, map);
    }

    public String getUserIsAdminCacheKey(int userId) {
        return PREFIX_KEY + userId;
    }

    /**
     * 判断用户是否是管理员 缓存
     * 
     * @param userId
     * @return
     */
    public boolean isAdminCache(int userId) {
        String key = PREFIX_KEY + userId;
        Object value = redisUtil.hashGet(key, ADMIN_KEY);
        if (value == null) {
            refreshUserIsAdminCache(userId);
            value = redisUtil.hashGet(key, ADMIN_KEY);
            return (boolean) value;
        }
        return (boolean) value;
    }

    /**
     * 获取指定用户的功能权限
     * 
     * @param userId
     * @return
     */
    public Set<Object> getUserFunctionsCache(int userId) {
        String userFunctionKey = getUserFunctionsKey(userId);
        return redisUtil.sGet(userFunctionKey);
    }

    /**
     * 获取用户指定功能的数据权限
     * 
     * @param userId
     * @param functionName
     * @return
     */
    public String getUserFunctionDepartmentsCache(int userId, String functionName) {
        String key = getFunctionDepartmentsKey(userId, functionName);
        return (String) redisUtil.get(key);
    }

}
