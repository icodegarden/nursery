package io.github.icodegarden.nursery.springboot.exception;

import org.springframework.security.core.AuthenticationException;

import io.github.icodegarden.nutrient.lang.spec.response.ErrorCodeException;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ErrorCodeAuthenticationException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	private final ErrorCodeException ece;

	public ErrorCodeAuthenticationException(ErrorCodeException ece) {
		super(ece.getSub_msg());
		this.ece = ece;
	}

	public ErrorCodeException getErrorCodeException() {
		return ece;
	}
}