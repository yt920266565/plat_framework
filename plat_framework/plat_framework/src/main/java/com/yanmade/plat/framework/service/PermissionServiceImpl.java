package com.yanmade.plat.framework.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.yanmade.plat.framework.dao.FunctionMapper;
import com.yanmade.plat.framework.entity.SmFunction;
import com.yanmade.plat.framework.util.RedisUtil;

@Component
@Validated
public class PermissionServiceImpl implements PermissionService {

    private static final String PERMISSION_ID_KEY = "permission_id_key";

    @Autowired
    private FunctionMapper mapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean insert(SmFunction permission) {
        int id = redisUtil.increment(PERMISSION_ID_KEY);
        permission.setId(id);
        return mapper.insert(permission);
    }

    @Override
    public SmFunction getSmPermission(int permissionId) {
        return mapper.getFunction(permissionId);
    }

    @Override
    public List<SmFunction> getPermissions(int roleId) {
        if (roleId <= 0) {
            return mapper.getFunctions();
        }

        return getPermissions(roleId);
    }

    @Override
    public List<SmFunction> getPermissionsByRoleId(int roleId) {
        return mapper.getFunctionsByRoleId(roleId);
    }

}
