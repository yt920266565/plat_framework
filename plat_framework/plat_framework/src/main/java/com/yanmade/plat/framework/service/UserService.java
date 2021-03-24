package com.yanmade.plat.framework.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;

import org.springframework.stereotype.Service;

import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;

@Service
public interface UserService {
    
    public List<SmUser> getUsers(SmUser smUser, Integer roleId);

    public boolean insert(SmUser user);

    public boolean modifyPassword(HttpServletRequest request, int id, String oldPassword, String newPassword);

    public boolean update(SmUser user);

    public boolean delete(int userId);
    
    public boolean batDelete(Map<String, Object> input);

    public Map<String, Object> getFunctionsByUserId(
            @Min(value = 0, message = "INVALID_PARAMETER") int userId);

    List<SmRole> getRolesByUserId(int userId);

    public boolean insertUsers(List<SmUser> users);
}
