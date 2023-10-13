package io.github.icodegarden.nursery.servlet.web.demo.mapper;

import org.apache.ibatis.annotations.Param;

import io.github.icodegarden.nursery.servlet.web.demo.pojo.persistence.ConsumerSystemPO;
import io.github.icodegarden.nutrient.lang.query.BaseQuery;
import io.github.icodegarden.nutrient.mybatis.repository.MybatisRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public interface ConsumerSystemMapper
		extends MybatisRepository<ConsumerSystemPO, ConsumerSystemPO.Update, BaseQuery, Object, ConsumerSystemPO> {

	ConsumerSystemPO findOneByAppId(@Param("appId") String appId, @Param("with") Object with);
}
