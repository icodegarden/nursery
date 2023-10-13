package io.github.icodegarden.nursery.servlet.web.demo.service;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.icodegarden.nursery.servlet.web.demo.service.CacherService;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@SpringBootTest
public class CacherServiceTests {

	@Autowired
	private CacherService cacherService;
	
	@Test
	public void remove1() throws Exception {
		cacherService.remove1("abc");
	}
	
	@Test
	public void remove2() throws Exception {
		cacherService.remove2(Arrays.asList("a", "b"));
	}
}