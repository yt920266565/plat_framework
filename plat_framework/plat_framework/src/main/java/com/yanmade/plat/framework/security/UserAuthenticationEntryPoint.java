package com.yanmade.plat.framework.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.util.ApiResponseUtil;

/**
 * 用户未登录的处理
 * 
 * @author lh
 *
 */
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException auth)
            throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(
                JSON.toJSONString(ApiResponseUtil.failure(HttpStatus.UNAUTHORIZED, null, ErrMsgEnum.UNAUTHORIZED)));
    }
    
}
