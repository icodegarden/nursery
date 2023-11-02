//package io.github.icodegarden.nursery.springboot.web.reactive.handler;
//
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.server.ServerWebExchange;
//
//import io.github.icodegarden.nursery.springboot.security.Authentication;
//import io.github.icodegarden.nursery.springboot.security.SecurityUtils;
//import io.github.icodegarden.nursery.springboot.web.reactive.util.ReactiveWebUtils;
//
///**
// * 
// * @author Fangfang.Xu
// *
// */
//@ControllerAdvice
//public class ReactiveControllerAdvice {
//
//	/**
//	 * 因为reactor中的跨线程，WebFilter认证后即使设置到SecurityUtils，也无法在业务代码处获取认证。<br>
//	 * 所以需要使用ServerWebExchange先承载参数，在正式进controller前再设置到SecurityUtils，因为此时线程同步。<br>
//	 */
//	@ModelAttribute(value = "setAuthentication")
//	public void setAuthentication(ServerWebExchange exchange) {
//		Authentication authentication = (Authentication) exchange.getAttribute("authentication");
//		if (authentication != null) {
//			SecurityUtils.setAuthentication(authentication);
//		}
//
//		ReactiveWebUtils.setExchange(exchange);
//	}
//	
//}