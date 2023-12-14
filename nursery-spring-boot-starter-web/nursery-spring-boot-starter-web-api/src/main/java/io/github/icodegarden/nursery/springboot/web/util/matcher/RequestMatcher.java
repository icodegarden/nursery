package io.github.icodegarden.nursery.springboot.web.util.matcher;

/**
 * 
 * @author Fangfang.Xu
 *
 * @param <R> Request
 */
public interface RequestMatcher<R> {

	boolean matches(R r);
}
