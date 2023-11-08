package io.github.icodegarden.nursery.servlet.web.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.icodegarden.nutrient.lang.repository.OptimizeTableResults;
import io.github.icodegarden.nutrient.mybatis.repository.MysqlMybatisDatabaseMapper;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@RestController
public class DatabaseController {

	@Autowired
	MysqlMybatisDatabaseMapper mysqlMybatisDatabase;

	@GetMapping("database/mysqlMybatis")
	public ResponseEntity<?> mysqlMybatis() {
		String version = mysqlMybatisDatabase.version();
		List<String> listTables = mysqlMybatisDatabase.listTables();
		long countTable = mysqlMybatisDatabase.countTable(listTables.get(0));
		long countTable2 = mysqlMybatisDatabase.countTable(listTables.get(1));
		OptimizeTableResults optimizeTable = mysqlMybatisDatabase.optimizeTable(listTables.get(0));
		boolean errorInMysql = optimizeTable.isErrorInMysql();

		return ResponseEntity.ok("ok");
	}
}
