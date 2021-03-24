package com.yanmade.plat.framework.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

public class SecurityVoter implements AccessDecisionVoter<Object> {

	/**
	 * 管理员角色
	 */
	private static final String ROLE_ADMIN = "-1";

	private static final String ROLE_EVERYONE = "-2";

	// ~ Methods
	// ========================================================================================================
	@Override
	public boolean supports(ConfigAttribute attribute) {
		return attribute.getAttribute() != null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		if (authentication == null) {
			return ACCESS_DENIED;
		}

		Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);
		if (isAdmin(authorities)) {
			return ACCESS_GRANTED;
		}

		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {

				// 如果是GET请求并且url没在function表中,直接放过
				if (attribute.getAttribute().equalsIgnoreCase(ROLE_EVERYONE)) {
					return ACCESS_GRANTED;
				}

				// Attempt to find a matching granted authority
				for (GrantedAuthority authority : authorities) {
					if (attribute.getAttribute().equals(authority.getAuthority())) {
						return ACCESS_GRANTED;
					}
				}
			}
		}

		return ACCESS_DENIED;
	}

	private boolean isAdmin(Collection<? extends GrantedAuthority> authorities) {
		for (GrantedAuthority grantedAuthority : authorities) { // 如果是管理员角色，通过验证
			if (grantedAuthority != null && ROLE_ADMIN.equals(grantedAuthority.getAuthority())) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	Collection<SimpleGrantedAuthority> extractAuthorities(Authentication authentication) {
		return (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
	}

}
