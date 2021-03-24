package com.yanmade.plat.framework.util;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    private JwtTokenUtil() {
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    
    public static final String SUBJECT = "yanmade.com";

    public static final long EXPIRITION = 1000 * 24 * 60 * 60 * 7l;

    public static final String APPSECRET_KEY = "congge_secret";

    public static String createToken(Map<String, Object> map) {
        return Jwts
                .builder()
                .setSubject(SUBJECT)
                .setClaims(map)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRITION))
                .signWith(SignatureAlgorithm.HS256, APPSECRET_KEY).compact();
    }
    
    public static Claims checkJWT(String token) {
        try {
            return Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取用户名
     * @param token
     * @return
     */
    public static String getUsername(String token){
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        return claims.get("username").toString();
    }
    
    /**
     * 获取用户角色
     * @param token
     * @return
     */
    public static String getUserRole(String token){
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        return claims.get("role").toString();
    }
    
    public static Date getExpirationTime(String token) {
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

    /**
     * 是否过期
     * @param token
     * @return
     */
    public static boolean isExpiration(String token){
        Date expirationTime = getExpirationTime(token);
        return expirationTime.before(new Date());
    }
    
}
