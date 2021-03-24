package com.yanmade.plat.framework.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.util.ApiResponseUtil;

/**
 * 用户没有权限的处理
 * 
 * @author lh
 *
 */
@Component
public class UserAccessDeniedHandler implements AccessDeniedHandler{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException arg2)
            throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter()
                .write(JSON.toJSONString(ApiResponseUtil.failure(HttpStatus.FORBIDDEN, null, ErrMsgEnum.FORBIDDEN)));
    }
    
}
