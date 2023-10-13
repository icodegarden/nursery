package io.github.icodegarden.nursery.springboot.web.servlet.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ContentCachingRequestWrapper 功能
 * 
 * @author Fangfang.Xu
 *
 */
public class ServletCacheRequestBodyFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		ContentCachingRequestWrapper wrapper = null;
		if (request instanceof ContentCachingRequestWrapper) {
			wrapper = (ContentCachingRequestWrapper) request;
		} else {
			wrapper = new ContentCachingRequestWrapper(request);
		}
		filterChain.doFilter(wrapper, response);
	}
}
