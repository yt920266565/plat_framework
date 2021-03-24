package com.yanmade.plat.framework.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;

@Service
public interface RoleService {

    List<SmRole> getRoles(int id);
    
    List<HashMap<String, Object>> getRoleList(Map<String, Object> input);
    
    int getRoleCnt();

    SmRole getRole(int id);

    boolean insert(SmRole role);

    boolean update(SmRole role);

    boolean delete(int id);

    boolean relationUsers(List<SmUser> users, int roleId);

}
