package io.github.icodegarden.nursery.springboot.web.servlet.util.matcher;

import org.springframework.http.HttpMethod;

import io.github.icodegarden.nursery.springboot.web.util.matcher.AbstractAntPathRequestMatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ServletAntPathRequestMatcher extends AbstractAntPathRequestMatcher<HttpServletRequest>
		implements ServletRequestMatcher {

	public ServletAntPathRequestMatcher(String pattern) {
		this(pattern, null);
	}

	public ServletAntPathRequestMatcher(String pattern, String httpMethod) {
		this(pattern, httpMethod, true);
	}

	public ServletAntPathRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
		super(pattern, httpMethod, caseSensitive);
	}

	@Override
	protected HttpMethod getHttpMethod(HttpServletRequest request) {
		return request.getMethod() != null ? HttpMethod.valueOf(request.getMethod()) : null;
	}

	@Override
	protected String getRequestPath(HttpServletRequest request) {
		return request.getRequestURI().toString();
	}

}
