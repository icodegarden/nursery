package io.github.icodegarden.nursery.springboot.security;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import io.github.icodegarden.nursery.springboot.security.SecurityUtils;
import io.github.icodegarden.nursery.springboot.security.SimpleAuthentication;
import io.github.icodegarden.nursery.springboot.security.SimpleAuthenticationContainer;
import io.github.icodegarden.nursery.springboot.security.SimpleUser;
import io.github.icodegarden.nursery.springboot.security.SpringAuthentication;
import io.github.icodegarden.nursery.springboot.security.SpringUser;

public class SecurityUtilsTests {

	@Test
	void spring() throws Exception {
		SpringUser user = new SpringUser("id", "xff", "", Collections.emptyList());
		PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(user, "",
				Collections.emptyList());

		SpringAuthentication springAuthentication = new SpringAuthentication(authentication);

		SecurityUtils.setAuthentication(springAuthentication);

		Assertions.assertThat(SecurityUtils.getAuthentication()).isNotNull();
		Assertions.assertThat(SecurityUtils.getAuthenticatedUser()).isNotNull();
		Assertions.assertThat(SecurityUtils.getAuthenticatedUser().getUserId()).isEqualTo("id");
		Assertions.assertThat(SecurityUtils.getAuthenticatedUser().getUsername()).isEqualTo("xff");
	}

	@Test
	void simple() throws Exception {
		SecurityUtils.configAuthenticationContainer(new SimpleAuthenticationContainer());
		
		SimpleUser user = new SimpleUser("id", "xff", "", Collections.emptyList());
		SimpleAuthentication simpleAuthentication = new SimpleAuthentication(user, Collections.emptyList());

		SecurityUtils.setAuthentication(simpleAuthentication);

		Assertions.assertThat(SecurityUtils.getAuthentication()).isNotNull();
		Assertions.assertThat(SecurityUtils.getAuthenticatedUser()).isNotNull();
		Assertions.assertThat(SecurityUtils.getAuthenticatedUser().getUserId()).isEqualTo("id");
		Assertions.assertThat(SecurityUtils.getAuthenticatedUser().getUsername()).isEqualTo("xff");
	}
}
