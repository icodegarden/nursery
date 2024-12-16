package io.github.icodegarden.nursery.springboot.security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Fangfang.Xu
 */
public class SpringSecurityAuthenticationContainer implements AuthenticationContainer {

	/**
	 * 
	 * @return Nullable
	 */
	@Override
	public Authentication getAuthentication() {
		org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication instanceof Authentication) {
			return (Authentication) authentication;
		}
		return null;
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication((SpringAuthentication) authentication);
	}

}
