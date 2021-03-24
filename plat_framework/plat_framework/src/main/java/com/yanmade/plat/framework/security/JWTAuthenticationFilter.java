package com.yanmade.plat.framework.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.util.ApiResponseUtil;
import com.yanmade.plat.framework.util.JsonUtil;
import com.yanmade.plat.framework.util.JwtTokenUtil;
import com.yanmade.plat.framework.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    private AuthenticationManager authenticationManager;
    
    private RedisUtil redisUtil;

    @Autowired
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, RedisUtil redisUtil) {
        this.authenticationManager = authenticationManager;
        this.redisUtil = redisUtil;
        super.setFilterProcessesUrl("/token");
        super.setPostOnly(true);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = null;
        try {
            json = JsonUtil.getRequestJsonObject(request);
        } catch (IOException e) {
            json = new JSONObject();
        }

        String username = json.getString("username");
        String password = json.getString("password");
        
        String credentials = username + " " + password;
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, credentials, new ArrayList<>()));
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        
        UserDetail user = (UserDetail) authResult.getPrincipal();

        StringBuilder role = new StringBuilder();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        for (GrantedAuthority authority : authorities){
            role.append(authority.getAuthority() + ",");
        }
        if (role.length() > 0) {
            role = role.deleteCharAt(role.length() - 1); // 删除最后一个分隔符
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", role);
        
        String token = JwtTokenUtil.createToken(claims);

        // 令牌存入redis, 设置30分钟过期时间
        redisUtil.set(user.getUsername() + ":" + token, token);
        redisUtil.expire(user.getUsername() + ":" + token, 30, TimeUnit.MINUTES);

        // 返回创建成功的token
        // 但是这里创建的token只是单纯的token
        // 按照jwt的规定，最后请求的时候应该是 `Bearer token`
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        String tokenStr = JwtTokenUtil.TOKEN_PREFIX + token;
        response.setHeader("token", tokenStr);
        response.getWriter().write(JSON.toJSONString(ApiResponseUtil.success(token)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(
                JSON.toJSONString(ApiResponseUtil.failure(HttpStatus.UNAUTHORIZED, null, ErrMsgEnum.LOGIN_FAILD)));
    }

}
