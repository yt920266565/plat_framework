package com.yanmade.plat.framework.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmFunction;

@Component
public interface PermissionService {

    public boolean insert(SmFunction permission);

    public SmFunction getSmPermission(int permissionId);

    public List<SmFunction> getPermissions(int roleId);
    
    public List<SmFunction> getPermissionsByRoleId(int roleId);
}
