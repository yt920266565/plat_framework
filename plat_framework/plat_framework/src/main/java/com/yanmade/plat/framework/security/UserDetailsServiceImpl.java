package com.yanmade.plat.framework.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.entity.SmUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    
    @Autowired
    private UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        
        SmUser user = mapper.getUserByName(username);
        if (user == null) {
            return null;
        }

        List<Integer> roleIdList = mapper.getRoleIdsByUserId(user.getId());
        return new UserDetail(user, roleIdList);
    }
        
}
