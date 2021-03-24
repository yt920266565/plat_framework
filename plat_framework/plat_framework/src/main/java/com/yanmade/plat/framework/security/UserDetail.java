package com.yanmade.plat.framework.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.yanmade.plat.framework.entity.SmUser;

@Component
public class UserDetail implements UserDetails, Serializable{
    
    private static final long serialVersionUID = 1L;

    private String username;
    
    private String password;
    
    private List<? extends GrantedAuthority> authorties;
    
    public UserDetail() {
    }
    
    // 写一个能直接使用user创建jwtUser的构造器
    public UserDetail(SmUser user, List<Integer> roleIdSet) {
        username = user.getUsername();
        password = user.getPassword();
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        for (Integer roleId : roleIdSet) {
            list.add(new SimpleGrantedAuthority("" + roleId));
        }

        authorties = list;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorties;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
    
    //账号是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    //账号是否未锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    //账号凭证是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
