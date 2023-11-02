package io.github.icodegarden.nursery.springboot.web.servlet.security;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import io.github.icodegarden.nursery.springboot.exception.ErrorCodeAuthenticationException;
import io.github.icodegarden.nutrient.lang.spec.response.ApiResponse;
import io.github.icodegarden.nutrient.lang.spec.response.ClientParameterInvalidErrorCodeException;
import io.github.icodegarden.nutrient.lang.spec.response.ErrorCodeException;
import io.github.icodegarden.nutrient.lang.spec.response.InternalApiResponse;
import io.github.icodegarden.nutrient.lang.spec.response.ServerErrorCodeException;
import io.github.icodegarden.nutrient.lang.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ServletApiResponseAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger log = LoggerFactory.getLogger(ServletApiResponseAuthenticationEntryPoint.class);

	private static final Charset CHARSET = Charset.forName("utf-8");

	private final ApiResponseBuilder builder;

	public ServletApiResponseAuthenticationEntryPoint() {
		this.builder = new DefaultApiResponseBuilder();
	}

	public ServletApiResponseAuthenticationEntryPoint(ApiResponseBuilder builder) {
		this.builder = builder;
	}

	public static interface ApiResponseBuilder {
		ApiResponse build(ErrorCodeException ece);
	}

	private class DefaultApiResponseBuilder implements ApiResponseBuilder {
		@Override
		public ApiResponse build(ErrorCodeException ece) {
			return InternalApiResponse.fail(ece);
		}
	}

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

			ErrorCodeException ece;
			if (e instanceof ErrorCodeAuthenticationException) {
				ece = ((ErrorCodeAuthenticationException) e).getErrorCodeException();
			} else if (e instanceof AuthenticationServiceException) {
				/**
				 * 500类型
				 */
				ece = new ServerErrorCodeException("Authentication", message, e);
			} else {
				ece = new ClientParameterInvalidErrorCodeException(
						ClientParameterInvalidErrorCodeException.SubPair.INVALID_SIGNATURE.getSub_code(), message);
			}

			ApiResponse apiResponse = builder.build(ece);

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("utf-8");
//			response.getWriter().write("Access Denied, Unauthorized");
			response.getWriter().write(JsonUtils.serialize(apiResponse));
		}
	}
}
