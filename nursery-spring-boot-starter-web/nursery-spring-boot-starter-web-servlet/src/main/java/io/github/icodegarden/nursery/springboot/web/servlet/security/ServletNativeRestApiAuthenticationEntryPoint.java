package io.github.icodegarden.nursery.springboot.web.servlet.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ServletNativeRestApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger log = LoggerFactory.getLogger(ServletNativeRestApiAuthenticationEntryPoint.class);

	/**
	 * 认证失败时
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {
		if (!response.isCommitted()) {
			String message = e.getMessage() != null ? e.getMessage() : "Not Authenticated.";

			if (log.isInfoEnabled()) {
				log.info("request {}", message);
			}
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("utf-8");
//			response.getWriter().write("Access Denied, Unauthorized");
			response.getWriter().write(e.getMessage() != null ? e.getMessage() : "Not Authenticated.");
		}
	}
}
