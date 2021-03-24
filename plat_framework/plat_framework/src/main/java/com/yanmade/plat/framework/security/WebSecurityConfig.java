package com.yanmade.plat.framework.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.util.RedisUtil;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    
    @Autowired
    private UserAccessDeniedHandler userAccessDeniedHandler;
    
    @Autowired
    private UserLogoutSuccessHandler userLogoutSuccessHandler;

    @Autowired
    private WebFilterInvocationSecurityMetadataSource metadataSource;

    @Value("${spring.isExternalToken}")
    private boolean isExternalToken;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private RedisUtil redisUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        fsi.setSecurityMetadataSource(metadataSource);
                        fsi.setAccessDecisionManager(accessDecisionManager());
                        return fsi;
                    }
                }).and() 

                // 认证以及鉴权处理
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), redisUtil))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), isExternalToken, restTemplate, mapper,
                        redisUtil))

                // 未登录以及登录没有权限的处理
                .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
                .accessDeniedHandler(userAccessDeniedHandler).and()

                // 不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .logout()
                // 禁用csrf保护下生效
                .logoutRequestMatcher(new AntPathRequestMatcher("/token", "DELETE"))
                .logoutSuccessHandler(userLogoutSuccessHandler).permitAll();
                 //不加这个配置，ie浏览器会阻止在弹出框做下载
                 http.headers().frameOptions().disable();
    }

    /**
     * swagger页面不需要认证
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui", "/swagger-resources",
                "/swagger-resources/configuration/security",
                "/swagger-ui.html", "/webjars/**", "/static/**");
        web.ignoring().antMatchers(HttpMethod.POST, "/users");
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters
                = Arrays.asList(new SecurityVoter());
        return new AffirmativeBased(decisionVoters);
    }

}
