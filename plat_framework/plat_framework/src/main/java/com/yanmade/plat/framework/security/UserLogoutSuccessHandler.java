package com.yanmade.plat.framework.security;

import com.alibaba.fastjson.JSON;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.util.ApiResponseUtil;
import com.yanmade.plat.framework.util.JwtTokenUtil;
import com.yanmade.plat.framework.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 用户注销处理
 * 
 * @author lh
 *
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler{

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // 删除redis缓存的token
        String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        if (Objects.isNull(tokenHeader)) {
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(
                    JSON.toJSONString(ApiResponseUtil.failure(HttpStatus.UNAUTHORIZED, null, ErrMsgEnum.UNAUTHORIZED)));
            return;
        }

        String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        String username = JwtTokenUtil.getUsername(token);

        redisUtil.delete(username + ":" + token);

        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSON.toJSONString(ApiResponseUtil.success(null)));
    }
    
}
