package io.github.icodegarden.nursery.springboot.web.reactive.handler;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.github.icodegarden.nursery.springboot.security.Authentication;
import io.github.icodegarden.nursery.springboot.security.SecurityUtils;
import io.github.icodegarden.nursery.springboot.web.reactive.util.ReactiveWebUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Aspect
@EnableAspectJAutoProxy
public class ReactiveControllerAspect implements InstantiationAwareBeanPostProcessor {

	/**
	 * 启动检测Controller的方法上是否有ServerWebExchange参数
	 */
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		if (beanClass.isAnnotationPresent(RestController.class) || beanClass.isAnnotationPresent(Controller.class)) {
			Method[] methods = beanClass.getDeclaredMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(PostMapping.class)//
						|| method.isAnnotationPresent(PutMapping.class)//
						|| method.isAnnotationPresent(GetMapping.class)//
						|| method.isAnnotationPresent(DeleteMapping.class)//
						|| method.isAnnotationPresent(PatchMapping.class)//
						|| method.isAnnotationPresent(RequestMapping.class)//
				) {
					Arrays.asList(method.getParameterTypes()).stream()
							.filter(parameterType -> ServerWebExchange.class.isAssignableFrom(parameterType))
							.findFirst()
							.orElseThrow(() -> new RuntimeException(String.format(
									"Parameter Type of ServerWebExchange must present on Controller Mapping Method [%s.%s()]",
									beanClass.getName(), method.getName())));
				}
			}
		}

		return null;
	}

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
	public void pointcut() {
	}

	/**
	 * 因为reactor中的跨线程，WebFilter认证后即使设置到SecurityUtils，也无法在业务代码处获取认证。<br>
	 * 所以需要使用ServerWebExchange @see ReactiveGatewayPreAuthenticatedAuthenticationFilter 先承载参数，在正式进controller前再设置到SecurityUtils，因为此时线程同步。<br>
	 * 注意使用@ControllerAdvice虽然原理差不多，但是有些情况下却会出现@ControllerAdvice中的线程和Controller中的线程不是相同的，依然无法达到目的。
	 */
	@Around("pointcut()")
	public Object doInvoke(ProceedingJoinPoint pjp) throws Throwable {
		SecurityUtils.setAuthentication(null);
		ReactiveWebUtils.setExchange(null);
		
		Object[] args = pjp.getArgs();
		for (Object arg : args) {
			if (arg == null) {
				continue;
			}
			Class<?> parameterType = arg.getClass();
			if (ServerWebExchange.class.isAssignableFrom(parameterType)) {
				ServerWebExchange exchange = (ServerWebExchange) arg;

				try {
					Authentication authentication = (Authentication) exchange.getAttribute("authentication");
					if (authentication != null) {
						SecurityUtils.setAuthentication(authentication);
					}
					ReactiveWebUtils.setExchange(exchange);

					return pjp.proceed();
				} finally {
					/**
					 * 无效方式，因为pjp.proceed()异步的
					 */
//					SecurityUtils.setAuthentication(null);
//					ReactiveWebUtils.setExchange(null);
				}
			}
		}

		return pjp.proceed();
	}
}