package io.github.icodegarden.nursery.springboot.web.servlet.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Fangfang.Xu
 */
public class ServletNativeRestApiAccessDeniedHandler implements AccessDeniedHandler {

	private static final Logger log = LoggerFactory.getLogger(AccessDeniedHandler.class);

	private String errorPage;

	/**
	 * 当授权未通过时
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			if (errorPage != null) {
				request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);

				RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
				dispatcher.forward(request, response);
			} else {
				String message = "Access Denied, Not Authorized.";
				if (log.isInfoEnabled()) {
					log.info("request {}", message);
				}
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("utf-8");
				response.getWriter().println(message);
			}
		}
	}

	public void setErrorPage(String errorPage) {
		if ((errorPage != null) && !errorPage.startsWith("/")) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}
		this.errorPage = errorPage;
	}
}
