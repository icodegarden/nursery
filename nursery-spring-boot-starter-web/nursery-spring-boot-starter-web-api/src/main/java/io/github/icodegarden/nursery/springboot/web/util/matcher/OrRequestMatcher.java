package io.github.icodegarden.nursery.springboot.web.util.matcher;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class OrRequestMatcher<R> implements RequestMatcher<R> {

	private final List<RequestMatcher<R>> requestMatchers;

	public OrRequestMatcher(List<RequestMatcher<R>> requestMatchers) {
		Assert.notEmpty(requestMatchers, "requestMatchers must contain a value");
		Assert.isTrue(!requestMatchers.contains(null), "requestMatchers cannot contain null values");
		this.requestMatchers = requestMatchers;
	}

	public OrRequestMatcher(RequestMatcher<R>... requestMatchers) {
		this(Arrays.asList(requestMatchers));
	}

	@Override
	public boolean matches(R request) {
		for (RequestMatcher<R> matcher : this.requestMatchers) {
			if (matcher.matches(request)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Or " + this.requestMatchers;
	}

}