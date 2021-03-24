package com.yanmade.plat.framework.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.yanmade.plat.framework.dao.FunctionMapper;
import com.yanmade.plat.framework.dao.RoleFunctionMapper;
import com.yanmade.plat.framework.entity.SmFunction;

/**
 * 获取拥有该api权限的角色集合
 * 
 * @author 0103379
 *
 */
@Component
public class WebFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	@Autowired
	private FunctionMapper mapper;

	@Autowired
	private RoleFunctionMapper roleFunctionMapper;

	private AntPathMatcher matcher = new AntPathMatcher();

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) {

		Collection<ConfigAttribute> collection = new HashSet<>();

		FilterInvocation fi = (FilterInvocation) object;
		String url = fi.getRequest().getRequestURI();
		String method = fi.getRequest().getMethod();

		// 查询function列表
		List<SmFunction> set = mapper.getFunctions();

		boolean urlExists = false;

		// 遍历function匹配请求的url
		for (SmFunction function : set) {
			if (method.equalsIgnoreCase(function.getMethod()) && matcher.match(function.getUrl(), url)) {
				urlExists = true;
				List<Integer> list = roleFunctionMapper.getRoleIdByFunctionId(function.getId());

				for (Integer roleId : list) {
					collection.add(new SecurityConfig("" + roleId));
				}

				break;
			}
		}

		// 如果url不存在funcion表里面 ，则不需要权限验证
		if (!urlExists) {
			collection.add(new SecurityConfig("-2"));
		}
		
		// 如果url不存在funcion表里面 并且是get请求，则不需要权限验证
		/*
		 * if (!urlExists && method.equalsIgnoreCase(HttpMethod.GET.toString())) {
		 * collection.add(new SecurityConfig("-2")); }
		 */

		// 如果Collection为空，需要设置一个默认的ConfigAttribute，否则不会拦截
		if (collection.isEmpty()) {
			collection.add(new SecurityConfig("AnonymousAuthen"));
		}

		return collection;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return new HashSet<>();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}
}
