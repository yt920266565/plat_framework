package com.yanmade.plat.framework.security;

import com.alibaba.fastjson.JSONObject;
import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.entity.SmUser;
import com.yanmade.plat.framework.util.JwtTokenUtil;
import com.yanmade.plat.framework.util.RedisUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    
    private static final String TOKEN_URL = "http://192.168.0.10:81/api/CommonApi/ValidToken?token={1}";

    private RestTemplate restTemplate;

    private UserMapper mapper;

    private boolean isExternalToken;
    
    private RedisUtil redisUtil;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, boolean isExternalToken,
            RestTemplate restTemplate, UserMapper mapper, RedisUtil redisUtil) {
        super(authenticationManager);
        this.isExternalToken = isExternalToken;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.redisUtil = redisUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        // 如果请求头中没有Authorization信息则不解析token直接进过滤器
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        // 如果token为空则不解析token直接进过滤器
        String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        if (token == null || token.trim().equals("") || token.equals("null")) {
            chain.doFilter(request, response);
            return;
        }

        if (isExternalToken) {
            // 调用erp的接口验证token
            JSONObject result = restTemplate.postForObject(TOKEN_URL, null, JSONObject.class, token);
            if (result == null) {
                chain.doFilter(request, response);
                return;
            }
            int state = result.getIntValue("state");
            if (state != 1) {
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = createAuthentication(result);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // 判断token是否过期（7天）
            if (JwtTokenUtil.isExpiration(token)) {
                chain.doFilter(request, response);
                return;
            }

//            RedisUtil redisUtil = context.getBean(RedisUtil.class);
            String username = JwtTokenUtil.getUsername(token);
            String key = username + ":" + token;
            String redisToken = (String) redisUtil.get(key);

            // 前端请求的token和缓存中的token不一致直接返回
            if (Objects.isNull(redisToken) || !redisToken.equals(token)) {
                chain.doFilter(request, response);
                return;
            }

            // 通过验证则刷新redis里token的过期时间
            redisUtil.expire(username + ":" + token, 30, TimeUnit.MINUTES);

            // 如果请求头中有token，则进行解析，并且设置认证信息
            UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        super.doFilterInternal(request, response, chain);
    }

    /**
     * 解析响应参数生成authentication
     * 
     * @param postForObject
     * @return
     */
    private UsernamePasswordAuthenticationToken createAuthentication(JSONObject result) {
        JSONObject data = result.getJSONObject("data");
        if (data == null) {
            return null;
        }
        String userCode = data.getString("UserCode");

        // 解析出来的userId不对，所以使用userCode查出userId
        SmUser user = mapper.getUserByName(userCode);

        List<Integer> roleIds = mapper.getRoleIdsByUserId(user.getId());
       // log.info("根据userId:{}查询数据库, roleIds:{}",user.getId(),roleIds);
        
        
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        for (Integer role : roleIds) {
            list.add(new SimpleGrantedAuthority(role + ""));
        }

        return new UsernamePasswordAuthenticationToken(userCode, null, list);
    }
    
    // 这里从token中获取用户信息并新建一个token
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String username = JwtTokenUtil.getUsername(token);
        String roles = JwtTokenUtil.getUserRole(token); // (通过用户名去数据库取用户角色)
        if (username != null){
            String[] roleArray = roles.split(",");
            List<SimpleGrantedAuthority> list = new ArrayList<>();
            for (String role : roleArray) {
                list.add(new SimpleGrantedAuthority(role));
            }

            return new UsernamePasswordAuthenticationToken(username, null, list);
        }

        return null;
    }
    
}
