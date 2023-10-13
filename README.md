# 概览

符合springboot/cloud组件规范的微服务框架，可选任意组件搭配使用
版本管理使用nursery-spring-boot-dependencies、nursery-spring-cloud-dependencies，自动管理springboot/cloud和alibaba-spring-cloud的版本

# 包结构

* nursery-spring-boot-starter 基础starter、无损上下线
* nursery-spring-boot-starter-beecomb 自动化beecomb的Client和Executor的JobHandler
* nursery-spring-boot-starter-cache 自动化缓存框架，优雅的api集，删除缓存无需考虑事务提交、延迟删除
* nursery-spring-boot-starter-elasticsearch 自动化es客户端
* nursery-spring-boot-starter-hbase 自动化hbase客户端
* nursery-spring-boot-starter-kafka 自动化kafka可靠producer
* nursery-spring-boot-starter-mybatis 自动化mybatis
* nursery-spring-boot-starter-redis 自动化redis客户端
* nursery-spring-boot-starter-shardingsphere 自动化shardingsphere的DataSource
* nursery-spring-boot-starter-web 自动化web的filter、异常handler
* nursery-spring-boot-starter-zookeeper 自动化zk客户端
* nursery-spring-cloud-starter 自动化微服务调用传参配置、FlowTag负载均衡、无损上下线
* nursery-spring-cloud-starter-gateway 符合openapi/api规范的网关
* nursery-spring-cloud-starter-seata 自动化seata集成
* nursery-spring-cloud-starter-sentinel 自动化sentinel集成