package io.github.icodegarden.nursery.springcloud.gateway.spi;

import io.github.icodegarden.nursery.springcloud.gateway.core.security.signature.App;
import io.github.icodegarden.nutrient.lang.spec.sign.OpenApiRequestBody;

/**
 * 
 * 
 * @author Fangfang.Xu
 *
 */
public interface OpenApiRequestValidator {

	/**
	 * @return 是否允许请求
	 */
	void validate(String requestPath, OpenApiRequestBody requestBody, App app);
}
