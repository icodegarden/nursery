# 概览

符合springboot/cloud组件规范的微服务框架，可选任意组件搭配使用
版本管理使用nursery-spring-boot-dependencies、nursery-spring-cloud-dependencies，自动管理springboot/cloud和alibaba-spring-cloud的版本



# nursery-spring-boot-starter 

基础starter、无损上下线

# nursery-spring-boot-starter-beecomb

## 自动注入
```java
@Autowired
private BeeCombClient beeCombClient;
```

## 自动注册
```java
@Component
public class XXXJobHandler implements JobHandler {
      …
}
```

## 配置类 
NurseryBeeCombClientProperties
NurseryBeeCombExecutorProperties

# nursery-spring-boot-starter-cache 

## 作用
缓存框架，优雅的api集，删除缓存无需考虑事务提交、延迟删除等特性

## 自动注入
```java
@Autowired
private Cacher cacher;
```
默认使用redis缓存

# nursery-spring-boot-starter-elasticsearch

## 自动注入
```java
@Autowired
private ElasticsearchClient elasticsearchClient;
```

```java
@Autowired
private RestHighLevelClient restHighLevelClient;//V7版本
```

## 配置类
NurseryElasticsearchProperties

# nursery-spring-boot-starter-hbase

## 自动注入
```java
@Autowired
private HBaseEnv hbaseEnv;

Connection connection = hbaseEnv.getConnection();
```

## 配置类 
NurseryHBaseProperties

# nursery-spring-boot-starter-kafka

## 一般作用
可靠生产、消费，有序消费

## 自动注入
```java
@Autowired
private ReliabilityProducer reliabilityProducer;
```
## 配置类
NurseryKafkaProperties

## 消费
ReliabilityConsumer

## 有序消费 
OrderedReliabilityConsumer

# nursery-spring-boot-starter-mybatis

## 一般作用 
自动化mybatis

## 分布式锁
```java
@Autowired
private MysqlMybatisLockMapper mapper;

DistributedLock lock = new MysqlMybatisLock(mapper, lockName, expireSeconds);
```

## 分布式可重入锁
```java
@Autowired
private MysqlMybatisLockMapper mapper;

DistributedReentrantLock lock = new MysqlMybatisReentrantLock(mapper, lockName, expireSeconds);
```

## 分布式读写可重入锁
```java
@Autowired
private MysqlMybatisReadWriteLockMapper mapper;

DistributedReentrantReadWriteLock lock = new MysqlMybatisReentrantReadWriteLock(mapper, lockName, expireSeconds);
```

## 注册
不同于Nacos等注册中心，这个注册是抽象的，当没有注册中心设施时这个可以作为简易的注册中心
在大多数场景可以作为席位注册管理，例如雪花算法给每个实例自动分配唯一的datacenterId, machineId。

```java
@Autowired
private MysqlMybatisLockMapper lockMapper;
@Autowired
private MysqlMybatisRegistryMapper mapper;

String name = "myservice";
String identifier = "127.0.0.1:8080";
String metadata = "{\"ts\":1000}";
String info = "{\"ts2\":2000}";

class MyRegistryListener implements RegistryListener {
	public Integer index;
	public Boolean leaseExpired;

	@Override
	public void onRegistered(Registration registration, Integer index) {
		this.index = index;
	}

	@Override
	public void onLeaseExpired(Registration registration) {
		leaseExpired = true;
	}
}
	
MysqlMybatisRegistry mysqlMybatisRegistry = new MysqlMybatisRegistry(lockMapper, mapper, new MyRegistryListener());
Registration registration = new Registration.Default(name, identifier, 30L, metadata, info);
RegisterResult result = registry.register(registration);

long datacenterId = SnowflakeSequenceManager.extractDatacenterId(result.getIndex());
long machineId = SnowflakeSequenceManager.extractMachineId(result.getIndex());

List<Registration> list = registry.listInstances(name);

registry.deregister(registration);
```

## 配置类
NurseryMybatisProperties

# nursery-spring-boot-starter-redis

## 自动注入
```java
@Autowired
private RedisExecutor redisExecutor;
```

## 分布式锁
```java
@Autowired
private RedisExecutor redisExecutor;

DistributedLock lock = new RedisLock(redisExecutor, lockName, expireSeconds);
```

## 分布式可重入锁
```java
@Autowired
private RedisExecutor redisExecutor;

DistributedReentrantLock lock = new RedisReentrantLock(redisExecutor, lockName, expireSeconds);
```

## 分布式读写可重入锁
```java
@Autowired
private RedisExecutor redisExecutor;

DistributedReentrantReadWriteLock lock = new RedisReentrantReadWriteLock(redisExecutor, lockName, expireSeconds);
```

## 配置类
NurseryRedisProperties

# nursery-spring-boot-starter-shardingsphere 

自动化shardingsphere的DataSource

# nursery-spring-boot-starter-web 

自动化web的filter、异常handler

# nursery-spring-boot-starter-zookeeper

## 自动注入
```java
@Autowired
private ZooKeeperHolder zooKeeperHolder;
```

## 配置类
NurseryZookeeperProperties

# nursery-spring-cloud-starter 

自动化微服务调用传参配置、FlowTag负载均衡、无损上下线

# nursery-spring-cloud-starter-gateway
符合openapi/api规范的网关

# nursery-spring-cloud-starter-seata

自动化seata集成

# nursery-spring-cloud-starter-sentinel

自动化sentinel集成




