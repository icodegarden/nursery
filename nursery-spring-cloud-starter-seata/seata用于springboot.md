#官方文档
	https://seata.apache.org/zh-cn/docs/overview/what-is-seata
#github
	https://github.com/apache/incubator-seata
#使用例子
	https://github.com/apache/incubator-seata-samples

#部署seata-server（TC）
##下载解压
	https://seata.apache.org/zh-cn/unversioned/download/seata-server 下载 apache-seata-2.2.0-incubating-bin.tar.gz 解压
	或 https://github.com/apache/incubator-seata/releases （最新版可能只有源码可下载了）
##使用注册中心
	修改conf/application.yml ， 有application.example.yml可参考
	
	seata:
	  registry:
	    type: nacos
	    nacos:
	      application: seata-server
	      server-addr: 172.22.122.27:8848
	      group: DEFAULT_GROUP
	      namespace: seata
	      #cluster: default
	      #username:
	      #password:
	      #context-path:
	      ##if use MSE Nacos with auth, mutex with username/password attribute
	      #access-key:
	      #secret-key:	      
##使用配置中心	 
	修改conf/application.yml ， 有application.example.yml可参考
	
	seata:
	  config:
	    type: nacos
	    nacos:
	      server-addr: 172.22.122.27:8848
	      group: DEFAULT_GROUP
	      namespace: seata
	      #username:
	      #password:
	      #context-path:
	      ##if use MSE Nacos with auth, mutex with username/password attribute
	      #access-key:
	      #secret-key:
	      #data-id: seataServer.properties     
##配置存储
	修改conf/application.yml ， 有application.example.yml可参考
	seata:
	  store:
	    db:
	      datasource: druid
	      db-type: mysql
	      driver-class-name: com.mysql.jdbc.Driver
	      url: jdbc:mysql://192.168.29.160:3306/seata?rewriteBatchedStatements=true
	      user: root
	      password: 123456
	      min-conn: 10
	      max-conn: 100
	      global-table: global_table
	      branch-table: branch_table
	      lock-table: lock_table
	      distributed-lock-table: distributed_lock_table
	      vgroup-table: vgroup_table
	      query-limit: 1000
	      max-wait: 5000    	  
	    redis: #到2.2.0仅支持单机、sentinel，不支持cluster，官方说lua、pipeline不支持cluster
	      mode: single
	      database: 0
	      min-conn: 10
	      max-conn: 100
	      password:
	      max-total: 100
	      query-limit: 1000
	      single:
	        host: 127.0.0.1
	        port: 6379
	      sentinel:
	        master-name:
	        sentinel-hosts:
	        
	若存储类型选择db，则seata-server需要建表，语句在解压包的seata-server\script\server\db目录找到，也可以在https://github.com/apache/incubator-seata/blob/v2.2.0/script/server/db/mysql.sql找到
	
	mysql的内容如下
	-- -------------------------------- The script used when storeMode is 'db' --------------------------------
	-- the table to store GlobalSession data
	CREATE TABLE IF NOT EXISTS `global_table`
	(
	    `xid`                       VARCHAR(128) NOT NULL,
	    `transaction_id`            BIGINT,
	    `status`                    TINYINT      NOT NULL,
	    `application_id`            VARCHAR(32),
	    `transaction_service_group` VARCHAR(32),
	    `transaction_name`          VARCHAR(128),
	    `timeout`                   INT,
	    `begin_time`                BIGINT,
	    `application_data`          VARCHAR(2000),
	    `gmt_create`                DATETIME,
	    `gmt_modified`              DATETIME,
	    PRIMARY KEY (`xid`),
	    KEY `idx_status_gmt_modified` (`status` , `gmt_modified`),
	    KEY `idx_transaction_id` (`transaction_id`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;
	
	-- the table to store BranchSession data
	CREATE TABLE IF NOT EXISTS `branch_table`
	(
	    `branch_id`         BIGINT       NOT NULL,
	    `xid`               VARCHAR(128) NOT NULL,
	    `transaction_id`    BIGINT,
	    `resource_group_id` VARCHAR(32),
	    `resource_id`       VARCHAR(256),
	    `branch_type`       VARCHAR(8),
	    `status`            TINYINT,
	    `client_id`         VARCHAR(64),
	    `application_data`  VARCHAR(2000),
	    `gmt_create`        DATETIME(6),
	    `gmt_modified`      DATETIME(6),
	    PRIMARY KEY (`branch_id`),
	    KEY `idx_xid` (`xid`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;
	
	-- the table to store lock data
	CREATE TABLE IF NOT EXISTS `lock_table`
	(
	    `row_key`        VARCHAR(128) NOT NULL,
	    `xid`            VARCHAR(128),
	    `transaction_id` BIGINT,
	    `branch_id`      BIGINT       NOT NULL,
	    `resource_id`    VARCHAR(256),
	    `table_name`     VARCHAR(32),
	    `pk`             VARCHAR(36),
	    `status`         TINYINT      NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
	    `gmt_create`     DATETIME,
	    `gmt_modified`   DATETIME,
	    PRIMARY KEY (`row_key`),
	    KEY `idx_status` (`status`),
	    KEY `idx_branch_id` (`branch_id`),
	    KEY `idx_xid` (`xid`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;
	
	CREATE TABLE IF NOT EXISTS `distributed_lock_table`
	(
	    `lock_key`       CHAR(20) NOT NULL,
	    `lock_value`     VARCHAR(20) NOT NULL,
	    `expire`         BIGINT,
	    primary key (`lock_key`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;
	
	INSERT INTO `distributed_lock_table` (lock_key, lock_value, expire) VALUES ('AsyncCommitting', ' ', 0);
	INSERT INTO `distributed_lock_table` (lock_key, lock_value, expire) VALUES ('RetryCommitting', ' ', 0);
	INSERT INTO `distributed_lock_table` (lock_key, lock_value, expire) VALUES ('RetryRollbacking', ' ', 0);
	INSERT INTO `distributed_lock_table` (lock_key, lock_value, expire) VALUES ('TxTimeoutCheck', ' ', 0);
	
	
	CREATE TABLE IF NOT EXISTS `vgroup_table`
	(
	    `vGroup`    VARCHAR(255),
	    `namespace` VARCHAR(255),
	    `cluster`   VARCHAR(255),
	  UNIQUE KEY `idx_vgroup_namespace_cluster` (`vGroup`,`namespace`,`cluster`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;    
	      
##启动seata-server	    
	linux ./bin/seata-server.sh 或 ./bin/seata-server.sh -p 8091 -h 127.0.0.1 -m file
	windows bin\seata-server.bat  		
##打开web	          
	http://localhost:7091 默认用户seata/seata
	seata-server会起2个端口，7091是web，8091是client rpc

##docker
	https://hub.docker.com/r/apache/seata-server
	
	docker run --name seata-server -p 8091:8091 -p7091:7091 apache/seata-server:2.2.0

##k8s
	先在nacos配置好seata-server的application.yml对应的配置，例如使用redis、db存储

	apiVersion: apps/v1
	kind: Deployment
	metadata:
	  name: seata-server
	  labels:
	    app: seata-server
	spec:
	  replicas: 2
	  selector:
	    matchLabels:
	      app: seata-server
	  template:
	    metadata:
	      labels:
	        app: seata-server
	    spec:
	      containers:
	        - name: seata-server
	          image: seataio/seata-server:1.7.0
	          imagePullPolicy: IfNotPresent
	          env:
	            - name: SEATA_CONFIG_NAME
	              value: file:/root/seata-config/registry
	          ports:
	            - name: rpc
	              containerPort: 8091
	              protocol: TCP
	            - name: web
	              containerPort: 7091
	              protocol: TCP	              
	          volumeMounts:
	            - name: seata-config
	              mountPath: /root/seata-config
	      volumes:
	        - name: seata-config
	          configMap:
	            name: seata-server-config
	---
	apiVersion: v1
	kind: ConfigMap
	metadata:
	  name: seata-server-config
	data:
	  registry.conf: |
	    registry {
	        type = "nacos"
	        nacos {
	          application = "seata-server"
	          serverAddr = "192.168.199.2"
	        }
	    }
	    config {
	      type = "nacos"
	      nacos {
	        serverAddr = "192.168.199.2"
	        group = "SEATA_GROUP"
	      }
	    }
	    
##k8s nacos配置不起作用	    
	截至2.2.0官方的k8s部署方式并不起作用，其原因是只要部署包含有application.yml文件(容器或本地都是这样)，外部配置就不起作用，他优先读的都是本地的application.yml，所以在k8s使用上述configMap来使用nacos是不起作用的。
	解决办法是使用configMap，把本地的application.yml整个都替换了，才能达到使用nacos、配置db的效果。
	apiVersion: apps/v1
	kind: Deployment
	metadata:
	  name: seata-server
	  namespace: ssp-dev
	  labels:
	    k8s-app: seata-server
	spec:
	  replicas: 2
	  selector:
	    matchLabels:
	      k8s-app: seata-server
	  template:
	    metadata:
	      labels:
	        k8s-app: seata-server
	    spec:
	      containers:
	        - name: seata-server
	          image: 'harbor.test.geely.com/star/icodegarden/seata-server:2.0.0'
	          imagePullPolicy: IfNotPresent
	          env:
	            - name: TZ
	              value: Asia/Shanghai			  
	          ports:
	            - name: http
	              containerPort: 8091
	              protocol: TCP
	          volumeMounts:
	            - name: seata-config
	              mountPath: /seata-server/resources/application.yml
	              subPath: application.yml #必须的，指定只覆盖application.yml文件
	      volumes:
	        - name: seata-config
	          configMap:
	            name: seata-server-config-yml	
	      imagePullSecrets:
	        - name: devops-pull-secret	
	
	
	apiVersion: v1  
	kind: ConfigMap  
	metadata:  
	  name: my-app-config  
	data:  
	  application.yml: |  
	    server:
	      port: 7091
	
	    spring:
	      application:
	        name: seata-server
	    logging:
	      config: classpath:logback-spring.xml
	      file:
	        path: ${log.home:${user.home}/logs/seata}
	      extend:
	        logstash-appender:
	          destination: 127.0.0.1:4560
	        kafka-appender:
	          bootstrap-servers: 127.0.0.1:9092
	          topic: logback_to_logstash
	          
	    console:
	      user:
	        username: seata
	        password: seata
	    seata:
	      registry:
	        # support: nacos 、 eureka 、 redis 、 zk  、 consul 、 etcd3 、 sofa
	        type: nacos
	        preferred-networks: 30.240.*
	        nacos:
	          application: seata-server
	          server-addr: 172.22.122.27:8848
	          #group: SEATA_GROUP
	          namespace: seata
	          cluster: default
	          username: nacos
	          password: otanacos01
	          context-path:
	          ##if use MSE Nacos with auth, mutex with username/password attribute
	          #access-key:
	          #secret-key:
	      config:
	        # support: nacos, consul, apollo, zk, etcd3
	        type: file
	        nacos:
	          server-addr: 172.22.122.27:8848
	          namespace: seata
	          #group: SEATA_GROUP
	          context-path:
	          ##1.The following configuration is for the open source version of Nacos
	          username: nacos
	          password: otanacos01
	          ##2.The following configuration is for the MSE Nacos on aliyun
	          #access-key:
	          #secret-key:
	          ##3.The following configuration is used to deploy on Aliyun ECS or ACK without authentication
	          #ram-role-name:
	          data-id: seata-server    
	      store:
	        # support: file 、 db 、 redis
	        mode: db
	        db:
	          datasource: druid
	          db-type: mysql
	          driver-class-name: com.mysql.jdbc.Driver
	          url: jdbc:mysql://192.168.29.160:3306/seata?rewriteBatchedStatements=true
	          user: root
	          password: 123456
	          min-conn: 10
	          max-conn: 100
	          global-table: global_table
	          branch-table: branch_table
	          lock-table: lock_table
	          distributed-lock-table: distributed_lock_table
	          vgroup-table: vgroup_table
	          query-limit: 1000
	          max-wait: 5000    
	      security:
	        secretKey: SeataSecretKey0c382ef121d778043159209298fd40bf3850a017
	        tokenValidityInMilliseconds: 1800000
	        csrf-ignore-urls: /metadata/v1/**
	        ignore:
	          urls: /,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.jpeg,/**/*.ico,/api/v1/auth/login,/version.json,/health,/error,/vgroup/v1/**
	          
	    
		    

#应用服务（TM/RM）
##引入依赖
		<dependency>
		    <groupId>io.seata</groupId>
		    <artifactId>seata-spring-boot-starter</artifactId>
		    <version>2.2.0</version><!--包含了seata-all -->
		</dependency>
<!-- 		<dependency> -->
<!-- 		    <groupId>com.alibaba.cloud</groupId> -->
<!-- 		    <artifactId>spring-cloud-alibaba-seata</artifactId> -->
<!-- 		    <version>2.2.0.RELEASE</version> -->
<!-- 		    <exclusions> -->
<!--                 <exclusion> -->
<!--                     <groupId>io.seata</groupId> -->
<!--                     <artifactId>seata-spring-boot-starter</artifactId> -->
<!--                 </exclusion> -->
<!--             </exclusions> -->
<!-- 		</dependency> -->

	不要引入以上版本的spring-cloud-alibaba-seata，因为这个版本不支持springcloud-loadbalancer，他还在使用ribbon导致类找不到无法启动，若有更新版本可以再尝试，到seata-server的2.2.0时查了下maven中央仓库还是没有比 spring-cloud-alibaba-seata:2.2.0.RELEASE 更新的版本
	
	所以重写了spring-cloud-alibaba-seata的传递xid代码，包括Feign/RestTemplate出去、HandlerInterceptor进来

##参数配置(springboot)
	在application.yml种配置如下参数
	seata: #io.seata.spring.boot.autoconfigure.properties.SeataProperties
	  tx-service-group: default_tx_group
	  service: #io.seata.spring.boot.autoconfigure.properties.client.ServiceProperties
	    vgroup-mapping:
	      default_tx_group: default
	  registry:
	    type: nacos
	    nacos: #io.seata.spring.boot.autoconfigure.properties.registry.RegistryNacosProperties
	      server-addr: 172.22.122.27:8848
	      username: nacos
	      password: otanacos01
	      namespace: seata
	#      group: SEATA_GROUP
	#      cluster: default
	#      context-path:
	      ##if use MSE Nacos with auth, mutex with username/password attribute
	#      access-key:
	#      secret-key:	    

##AT建表
	若代码中有使用AT模式，则需要对RM建表，语句在 https://github.com/apache/incubator-seata/blob/v2.2.0/script/client/at/db/mysql.sql
	
	mysql的内容如下
	-- for AT mode you must to init this sql for you business database. the seata server not need it.
	CREATE TABLE IF NOT EXISTS `undo_log`
	(
	    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
	    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
	    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
	    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
	    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
	    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
	    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
	    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
	) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
	ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);
	  
##TCC建表
	若代码中有使用TCC模式的useTCCFence=true，则需要对RM建表，语句在 https://github.com/apache/incubator-seata/blob/v2.2.0/script/client/tcc/db/mysql.sql
	
	mysql的内容如下
	-- -------------------------------- The script use tcc fence  --------------------------------
	-- -------------------------------- The script use tcc fence  --------------------------------
	CREATE TABLE IF NOT EXISTS `tcc_fence_log`
	(
	    `xid`           VARCHAR(128)  NOT NULL COMMENT 'global id',
	    `branch_id`     BIGINT        NOT NULL COMMENT 'branch id',
	    `action_name`   VARCHAR(64)   NOT NULL COMMENT 'action name',
	    `status`        TINYINT       NOT NULL COMMENT 'status(tried:1;committed:2;rollbacked:3;suspended:4)',
	    `gmt_create`    DATETIME(3)   NOT NULL COMMENT 'create time',
	    `gmt_modified`  DATETIME(3)   NOT NULL COMMENT 'update time',
	    PRIMARY KEY (`xid`, `branch_id`),
	    KEY `idx_gmt_modified` (`gmt_modified`),
	    KEY `idx_status` (`status`)
	) ENGINE = InnoDB
	DEFAULT CHARSET = utf8mb4;
	
##SAGA建表	
	若代码中有使用SAGA模式，则需要对RM建表，语句在 https://github.com/apache/incubator-seata/blob/v2.2.0/script/client/saga/db/mysql.sql
	
	mysql的内容如下
	-- -------------------------------- The script used for sage  --------------------------------


	CREATE TABLE IF NOT EXISTS `seata_state_machine_def`
	(
	    `id`               VARCHAR(32)  NOT NULL COMMENT 'id',
	    `name`             VARCHAR(128) NOT NULL COMMENT 'name',
	    `tenant_id`        VARCHAR(32)  NOT NULL COMMENT 'tenant id',
	    `app_name`         VARCHAR(32)  NOT NULL COMMENT 'application name',
	    `type`             VARCHAR(20)  COMMENT 'state language type',
	    `comment_`         VARCHAR(255) COMMENT 'comment',
	    `ver`              VARCHAR(16)  NOT NULL COMMENT 'version',
	    `gmt_create`       DATETIME(3)  NOT NULL COMMENT 'create time',
	    `status`           VARCHAR(2)   NOT NULL COMMENT 'status(AC:active|IN:inactive)',
	    `content`          TEXT COMMENT 'content',
	    `recover_strategy` VARCHAR(16) COMMENT 'transaction recover strategy(compensate|retry)',
	    PRIMARY KEY (`id`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;
	
	CREATE TABLE IF NOT EXISTS `seata_state_machine_inst`
	(
	    `id`                  VARCHAR(128)            NOT NULL COMMENT 'id',
	    `machine_id`          VARCHAR(32)             NOT NULL COMMENT 'state machine definition id',
	    `tenant_id`           VARCHAR(32)             NOT NULL COMMENT 'tenant id',
	    `parent_id`           VARCHAR(128) COMMENT 'parent id',
	    `gmt_started`         DATETIME(3)             NOT NULL COMMENT 'start time',
	    `business_key`        VARCHAR(48) COMMENT 'business key',
	    `start_params`        TEXT COMMENT 'start parameters',
	    `gmt_end`             DATETIME(3) COMMENT 'end time',
	    `excep`               BLOB COMMENT 'exception',
	    `end_params`          TEXT COMMENT 'end parameters',
	    `status`              VARCHAR(2) COMMENT 'status(SU succeed|FA failed|UN unknown|SK skipped|RU running)',
	    `compensation_status` VARCHAR(2) COMMENT 'compensation status(SU succeed|FA failed|UN unknown|SK skipped|RU running)',
	    `is_running`          TINYINT(1) COMMENT 'is running(0 no|1 yes)',
	    `gmt_updated`         DATETIME(3) NOT NULL,
	    PRIMARY KEY (`id`),
	    UNIQUE KEY `unikey_buz_tenant` (`business_key`, `tenant_id`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;
	
	CREATE TABLE IF NOT EXISTS `seata_state_inst`
	(
	    `id`                       VARCHAR(48)  NOT NULL COMMENT 'id',
	    `machine_inst_id`          VARCHAR(128) NOT NULL COMMENT 'state machine instance id',
	    `name`                     VARCHAR(128) NOT NULL COMMENT 'state name',
	    `type`                     VARCHAR(20)  COMMENT 'state type',
	    `service_name`             VARCHAR(128) COMMENT 'service name',
	    `service_method`           VARCHAR(128) COMMENT 'method name',
	    `service_type`             VARCHAR(16) COMMENT 'service type',
	    `business_key`             VARCHAR(48) COMMENT 'business key',
	    `state_id_compensated_for` VARCHAR(50) COMMENT 'state compensated for',
	    `state_id_retried_for`     VARCHAR(50) COMMENT 'state retried for',
	    `gmt_started`              DATETIME(3)  NOT NULL COMMENT 'start time',
	    `is_for_update`            TINYINT(1) COMMENT 'is service for update',
	    `input_params`             TEXT COMMENT 'input parameters',
	    `output_params`            TEXT COMMENT 'output parameters',
	    `status`                   VARCHAR(2)   NOT NULL COMMENT 'status(SU succeed|FA failed|UN unknown|SK skipped|RU running)',
	    `excep`                    BLOB COMMENT 'exception',
	    `gmt_updated`              DATETIME(3) COMMENT 'update time',
	    `gmt_end`                  DATETIME(3) COMMENT 'end time',
	    PRIMARY KEY (`id`, `machine_inst_id`)
	) ENGINE = InnoDB
	  DEFAULT CHARSET = utf8mb4;

##使用注解	
	AT只需 @GlobalTransactional
	TCC需要 @GlobalTransactional + @TwoPhaseBusinessAction + @LocalTcc
	
	
#FAQ
##AT和TCC对比
	https://developer.aliyun.com/article/1116097	
##seata是如何解决TCC空回滚、幂等、悬挂
	https://blog.csdn.net/Saintmm/article/details/127735963	
	