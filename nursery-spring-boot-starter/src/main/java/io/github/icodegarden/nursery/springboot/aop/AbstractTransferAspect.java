package io.github.icodegarden.nursery.springboot.aop;

import java.lang.reflect.UndeclaredThrowableException;

import io.github.icodegarden.nutrient.lang.spec.response.ErrorCodeException;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Deprecated
public abstract class AbstractTransferAspect {

	protected boolean printErrorStackOnWarn = true;

	public void setPrintErrorStackOnWarn(boolean printErrorStackOnWarn) {
		this.printErrorStackOnWarn = printErrorStackOnWarn;
	}

	protected ErrorCodeException causeErrorCodeException(Throwable e) {
		while (e != null && !(e instanceof ErrorCodeException)) {
			if (e instanceof UndeclaredThrowableException) {
				e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
			} else if (e instanceof org.springframework.cglib.proxy.UndeclaredThrowableException) {
				e = ((org.springframework.cglib.proxy.UndeclaredThrowableException) e).getUndeclaredThrowable();
			} else {
				e = e.getCause();
			}
		}
		if (e != null && e instanceof ErrorCodeException) {
			return (ErrorCodeException) e;
		}
		return null;
	}
}