package io.github.icodegarden.nursery.servlet.web.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import io.github.icodegarden.nursery.springboot.web.servlet.security.ServletNativeRestApiAccessDeniedHandler;
import io.github.icodegarden.nursery.springboot.web.servlet.security.ServletNativeRestApiAuthenticationEntryPoint;

/**
 * @author Fangfang.Xu
 */
@Configuration
@EnableWebSecurity
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
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http//
				.sessionManagement()//
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)//
				// .maximumSessions(32) // maximum number of concurrent sessions for one user
				// .sessionRegistry(sessionRegistry)
				.and()//
				.exceptionHandling()//
				.authenticationEntryPoint(new ServletNativeRestApiAuthenticationEntryPoint())//
				.accessDeniedHandler(new ServletNativeRestApiAccessDeniedHandler())//
				.and()//
				.csrf()//
				.disable()//
				.headers()//
				.frameOptions()//
				.disable()//
				.and()//
				.authorizeHttpRequests()//
				.anyRequest().permitAll()//
				.and()//
//				.addFilterBefore(new JWTAuthenticationFilter(jwtProperties,
//						Arrays.asList("/api/**", "/view/**", "/system/main"/* main页面需要用户信息 */)).setCookieEnable(true),
//						UsernamePasswordAuthenticationFilter.class)//
//				.addFilterBefore(
//						new BasicAuthenticationFilter(userService,
//								new BasicAuthenticationFilter.Config(Arrays.asList("/openapi/**"),
//										instanceProperties.getSecurity().getBasicAuth().getMaxUserCacheSeconds())),
//						UsernamePasswordAuthenticationFilter.class)//
				.build();
	}
}
