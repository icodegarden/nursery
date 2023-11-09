package io.github.icodegarden.nursery.reactive.web.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveNativeRestApiAccessDeniedHandler;
import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveNativeRestApiAuthenticationEntryPoint;

/**
 * @author Fangfang.Xu
 */
@Configuration
@EnableWebFluxSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

//	@Autowired
//	private UserDetailsService userDetailsService;
//
//	@Bean
//	public AuthenticationManager authenticationManager(ObjectPostProcessor<Object> objectPostProcessor,
//			PasswordEncoder passwordEncoder) throws Exception {
//		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//		authenticationProvider.setPasswordEncoder(passwordEncoder);
//		authenticationProvider.setUserDetailsService(userDetailsService);
//
//		DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
//				.postProcess(new DefaultAuthenticationEventPublisher());
//		AuthenticationManagerBuilder auth = new AuthenticationManagerBuilder(objectPostProcessor);
//		auth.authenticationEventPublisher(eventPublisher);
//		auth.authenticationProvider(authenticationProvider);
//		return auth.build();
//	}

	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
		return http//
//				.sessionManagement()//
//				.sessionCreationPolicy(SessionCreationPolicy.NEVER)//
		// .maximumSessions(32) // maximum number of concurrent sessions for one user
		// .sessionRegistry(sessionRegistry)
//				.and()//
				.exceptionHandling()//
				.authenticationEntryPoint(new ReactiveNativeRestApiAuthenticationEntryPoint())//
				.accessDeniedHandler(new ReactiveNativeRestApiAccessDeniedHandler())//
				.and()//
				.csrf()//
				.disable()//
				.headers()//
				.frameOptions()//
				.disable()//
				.and()//
				.authorizeExchange().anyExchange().permitAll()//
				.and()//
//				.addFilterBefore(new JWTAuthenticationFilter(jwtProperties,
//						Arrays.asList("/api/**", "/view/**", "/system/main"/* main页面需要用户信息 */)).setCookieEnable(true),
//						SecurityWebFiltersOrder.AUTHENTICATION)//
//				.addFilterBefore(
//						new BasicAuthenticationFilter(userService,
//								new BasicAuthenticationFilter.Config(Arrays.asList("/openapi/**"),
//										instanceProperties.getSecurity().getBasicAuth().getMaxUserCacheSeconds())),
//						SecurityWebFiltersOrder.AUTHENTICATION)//
				.build();
	}
}
