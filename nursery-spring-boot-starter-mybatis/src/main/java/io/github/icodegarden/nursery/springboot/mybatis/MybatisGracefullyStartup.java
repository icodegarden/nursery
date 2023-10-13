package io.github.icodegarden.nursery.springboot.mybatis;

import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyStartup;
import io.github.icodegarden.nutrient.mybatis.repository.MybatisDatabase;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class MybatisGracefullyStartup implements GracefullyStartup {

	private final MybatisDatabase mybatisDatabase;

	public MybatisGracefullyStartup(MybatisDatabase mybatisDatabase) {
		this.mybatisDatabase = mybatisDatabase;
	}

	@Override
	public void start() throws Throwable {
		/**
		 * 无损上线,完成mybatis代码初始化（首次耗时几百毫秒）
		 */
		try {
			mybatisDatabase.version();
		} catch (Exception e) {
			log.error("ex on GracefullyStartup", e);
		}
	}
}
