package io.github.icodegarden.nursery.servlet.web.demo;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableFeignClients("io.github.icodegarden.nursery.servlet.web.demo.feign")
@SpringBootApplication
public class NurseryServletWebDemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(NurseryServletWebDemoApplication.class, args);
		
		MessageSource messageSource = run.getBean(MessageSource.class);
		System.out.println(messageSource);
		LocaleResolver localeResolver = run.getBean(LocaleResolver.class);
		System.out.println(localeResolver);
//		Locale locale = localeResolver.resolveLocale(request);

		String message = messageSource.getMessage("task.id.invalid", new Object[] { 123 }, new Locale("en-US"));
		System.out.println(message);
		message = messageSource.getMessage("task.id.invalid", new Object[] { 123 }, new Locale("zh-CN,zh;q=0.9"));
		System.out.println(message);
	}

	/**
	 * 国际化
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        // 设置资源文件刷新间隔（秒），这里设置为0表示不自动刷新，可以根据需要调整
        messageSource.setCacheSeconds(0);
        return messageSource;
	}
}