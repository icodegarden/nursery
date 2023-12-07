package io.github.icodegarden.nursery.reactive.web.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveNativeRestApiAccessDeniedHandler;
import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveNativeRestApiAuthenticationEntryPoint;
import reactor.netty.ReactorNetty;

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
				/**
				 * IMPT引入security后请求将经过ServerRequestCacheWebFilter，其内部默认使用的是WebSessionServerRequestCache<br>
				 * 使得进入Controller的线程不再是reactor-http-nio-*，而是parallel-*，导致ReactorNetty.IO_WORKER_COUNT等参数失效<br>
				 * 因此需要配置不需要session NoOpServerRequestCache<br>
				 */
				.requestCache().requestCache(NoOpServerRequestCache.getInstance())//
				.and()//
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
