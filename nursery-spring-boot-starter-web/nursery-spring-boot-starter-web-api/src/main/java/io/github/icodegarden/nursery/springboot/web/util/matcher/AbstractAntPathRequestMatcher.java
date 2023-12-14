package io.github.icodegarden.nursery.springboot.web.util.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public abstract class AbstractAntPathRequestMatcher<R> implements RequestMatcher<R> {

	private static final String MATCH_ALL = "/**";

	private final AntPathMatcher matcher;

	private final String pattern;

	private final HttpMethod httpMethod;

	public AbstractAntPathRequestMatcher(String pattern) {
		this(pattern, null);
	}

	public AbstractAntPathRequestMatcher(String pattern, String httpMethod) {
		this(pattern, httpMethod, true);
	}

	public AbstractAntPathRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
		Assert.hasText(pattern, "Pattern cannot be null or empty");
		if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
			pattern = MATCH_ALL;
			this.matcher = null;
		} else {
			this.matcher = new AntPathMatcher();
			this.matcher.setTrimTokens(false);
			this.matcher.setCaseSensitive(caseSensitive);
		}

		this.pattern = pattern;
		this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
	}

	@Override
	public boolean matches(R request) {
		HttpMethod method = getHttpMethod(request);

		if (this.httpMethod != null && method != null && this.httpMethod != method) {
			return false;
		}
		if (this.pattern.equals(MATCH_ALL)) {
			return true;
		}

		String url = getRequestPath(request);
		return this.matcher.match(pattern, url);
	}

	public String getPattern() {
		return this.pattern;
	}

	protected abstract HttpMethod getHttpMethod(R r);

	protected abstract String getRequestPath(R r);
}
