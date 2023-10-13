package io.github.icodegarden.nursery.springboot.mybatis.autoconfigure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springboot.mybatis.MybatisGracefullyStartup;
import io.github.icodegarden.nursery.springboot.mybatis.properties.NurseryMybatisProperties;
import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyStartup;
import io.github.icodegarden.nutrient.mybatis.interceptor.SqlInterceptor;
import io.github.icodegarden.nutrient.mybatis.repository.MysqlMybatisDatabase;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(SqlInterceptor.class)
@EnableConfigurationProperties({ NurseryMybatisProperties.class })
@Configuration
@Slf4j
public class NurseryMybatisAutoConfiguration {

	@Autowired
	private NurseryMybatisProperties mybatisProperties;

	@ConditionalOnProperty(value = "icodegarden.nursery.mybatis.interceptor.sql.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public SqlInterceptor sqlInterceptor() {
		log.info("nursery init bean of SqlInterceptor");

		SqlInterceptor sqlInterceptor = new SqlInterceptor();
		sqlInterceptor.setSqlConfig(mybatisProperties.getSql());
		sqlInterceptor.setSqlConsumer(sql -> {
			log.warn("{}ms Threshold sql: {}", mybatisProperties.getSql(), sql);
		});
		return sqlInterceptor;
	}

	@ConditionalOnClass(name = { "com.mysql.cj.MysqlConnection" })
	@ConditionalOnProperty(value = "icodegarden.nursery.mybatis.gracefullystartup.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public GracefullyStartup mybatisGracefullyStartup(MysqlMybatisDatabase mysqlMybatisDatabase) {
		log.info("nursery init bean of MybatisGracefullyStartup");

		return new MybatisGracefullyStartup(mysqlMybatisDatabase);
	}

	@ConditionalOnClass(MapperScan.class)
	@ConditionalOnProperty(value = "icodegarden.nursery.mybatis.mapperScan.enabled", havingValue = "true", matchIfMissing = true)
	@MapperScan(basePackages = "${" + NurseryMybatisProperties.SCAN_BASE_PACKAGES_PROPERTIE_NAME + "}", basePackageClasses = {
			io.github.icodegarden.nutrient.mybatis.repository.MysqlMybatisDatabase.class, //
			io.github.icodegarden.nutrient.mybatis.concurrent.lock.MysqlMybatisLockMapper.class,//
			io.github.icodegarden.nutrient.mybatis.concurrent.lock.MysqlMybatisReadWriteLockMapper.class,//
			io.github.icodegarden.nutrient.mybatis.registry.MysqlMybatisRegistryMapper.class//
	})
	@Configuration
	protected static class MapperScanAutoConfiguration {
		{
			log.info("nursery init bean of MapperScanAutoConfiguration");
		}
	}
}
