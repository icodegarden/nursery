package io.github.icodegarden.nursery.springboot.web.servlet.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.github.icodegarden.nutrient.lang.spec.response.ApiResponse;
import io.github.icodegarden.nutrient.lang.spec.response.ClientParameterInvalidErrorCodeException;
import io.github.icodegarden.nutrient.lang.spec.response.ClientParameterMissingErrorCodeException;
import io.github.icodegarden.nutrient.lang.spec.response.ErrorCodeException;
import io.github.icodegarden.nutrient.lang.spec.response.OpenApiResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 使用 @Bean
 * 
 * @author Fangfang.Xu
 *
 */
public class ServletApiResponseExceptionHandler extends AbstractServletExceptionHandler<ApiResponse> {

	private static final Logger log = LoggerFactory.getLogger(ServletApiResponseExceptionHandler.class);

	@Override
	public ResponseEntity<ApiResponse> onPathVariableMissing(HttpServletRequest request,
			MissingPathVariableException cause) throws Exception {
		ErrorCodeException ece = new ClientParameterMissingErrorCodeException(
				ClientParameterMissingErrorCodeException.SubPair.MISSING_PARAMETER.getSub_code(),
				"pathVariable:" + cause.getVariableName());
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}
	
	@Override
	public ResponseEntity<ApiResponse> onRequestHeaderMissing(HttpServletRequest request,
			MissingRequestHeaderException cause) throws Exception {
		ErrorCodeException ece = new ClientParameterMissingErrorCodeException(
				ClientParameterMissingErrorCodeException.SubPair.MISSING_PARAMETER.getSub_code(),
				"header:" + cause.getHeaderName());
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}
	
	@Override
	public ResponseEntity<ApiResponse> onRequestCookieMissing(HttpServletRequest request,
			MissingRequestCookieException cause) throws Exception {
		ErrorCodeException ece = new ClientParameterMissingErrorCodeException(
				ClientParameterMissingErrorCodeException.SubPair.MISSING_PARAMETER.getSub_code(),
				"cookie:" + cause.getCookieName());
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}
	
	@Override
	public ResponseEntity<ApiResponse> onParameterMissing(HttpServletRequest request,
			MissingServletRequestParameterException cause) throws Exception {
		ErrorCodeException ece = new ClientParameterMissingErrorCodeException(
				ClientParameterMissingErrorCodeException.SubPair.MISSING_PARAMETER.getSub_code(),
				"parameter:" + cause.getParameterName());
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}
//		OpenApiRequestBody body = extractOpenApiRequestBody(request);
//		if (body != null) {
//			return ResponseEntity.ok(OpenApiResponse.fail(body.getMethod(), ece));
//		}
//
//		return ResponseEntity.ok(InternalApiResponse.fail(ece));
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}

	@Override
	public ResponseEntity<ApiResponse> onParameterTypeInvalid(HttpServletRequest request,
			MethodArgumentTypeMismatchException cause) {
		ErrorCodeException ece = new ClientParameterInvalidErrorCodeException(
				ClientParameterInvalidErrorCodeException.SubPair.INVALID_PARAMETER.getSub_code(),
				"parameter:" + cause.getName()/* 对应的字段名 */);
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}
//		OpenApiRequestBody body = extractOpenApiRequestBody(request);
//		if (body != null) {
//			return ResponseEntity.ok(OpenApiResponse.fail(body.getMethod(), ece));
//		}
//
//		return ResponseEntity.ok(InternalApiResponse.fail(ece));
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}

	@Override
	public ResponseEntity<ApiResponse> onBodyParameterInvalid(HttpServletRequest request,
			MethodArgumentNotValidException cause) {
		ErrorCodeException ece;
		if (cause.getBindingResult().hasErrors()) {
			List<ObjectError> allErrors = cause.getBindingResult().getAllErrors();
			String subMsg = allErrors.stream().findFirst().map(error -> {
				if (error instanceof FieldError) {
					String field = ((FieldError) error).getField();
					return "parameter:" + field;
				}
				return cause.getMessage();
			}).get();

			ece = new ClientParameterInvalidErrorCodeException(
					ClientParameterInvalidErrorCodeException.SubPair.INVALID_PARAMETER.getSub_code(), subMsg);
		} else {
			ece = new ClientParameterMissingErrorCodeException(
					ClientParameterInvalidErrorCodeException.SubPair.INVALID_PARAMETER.getSub_code(),
					cause.getMessage());
		}
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}

//		OpenApiRequestBody body = extractOpenApiRequestBody(request);
//		if (body != null) {
//			return ResponseEntity.ok(OpenApiResponse.fail(body.getMethod(), ece));
//		}
//
//		return ResponseEntity.ok(InternalApiResponse.fail(ece));
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}

	@Override
	public ResponseEntity<ApiResponse> onBodyParameterTypeInvalid(HttpServletRequest request,
			HttpMessageNotReadableException cause) {
		/**
		 * 由于类型不匹配，这种情况实际上是 JSON parse error
		 */

		ErrorCodeException ece = new ClientParameterInvalidErrorCodeException(
				ClientParameterInvalidErrorCodeException.SubPair.INVALID_PARAMETER.getSub_code(),
				"Invalid:json-field-type");
		if (log.isWarnEnabled()) {
			log.warn("{} {}", PARAMETER_INVALID_LOG_MODULE, ece.getMessage(), cause);
		}
//		OpenApiRequestBody body = extractOpenApiRequestBody(request);
//		if (body != null) {
//			return ResponseEntity.ok(OpenApiResponse.fail(body.getMethod(), ece));
//		}
//
//		return ResponseEntity.ok(InternalApiResponse.fail(ece));
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}

	@Override
	public ResponseEntity<ApiResponse> onException(HttpServletRequest request, Exception cause) {
//		OpenApiRequestBody body = extractOpenApiRequestBody(request);

		ErrorCodeException ece = convertErrorCodeException(cause);

//		if (body != null) {
//			return ResponseEntity.ok(OpenApiResponse.fail(body.getMethod(), ece));
//		}
//
//		return ResponseEntity.ok(InternalApiResponse.fail(ece));
		
		/**
		 * 一律使用OpenApiResponse来构造即可<br>
		 * biz_code会被gateway补充，那里有BodyCache<br>
		 */
		return ResponseEntity.ok(OpenApiResponse.fail(null, ece));
	}

}