package io.github.icodegarden.nursery.springboot.mybatis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.icodegarden.nutrient.mybatis.interceptor.SqlConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.mybatis")
@Getter
@Setter
@ToString(callSuper = true)
public class NurseryMybatisProperties {

	public static final String SCAN_BASE_PACKAGES_PROPERTIE_NAME = "icodegarden.nursery.mybatis.mapperScan.basePackages";

	private SqlConfig sql = new SqlConfig();

}