package io.github.icodegarden.nursery.springboot.redis.jedis;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import io.github.icodegarden.nursery.springboot.redis.properties.NurseryRedisProperties;
import io.github.icodegarden.nursery.springboot.redis.properties.NurseryRedisProperties.Cluster;
import io.github.icodegarden.nursery.springboot.redis.properties.NurseryRedisProperties.Cluster.ClusterRedis;
import io.github.icodegarden.nursery.springboot.redis.properties.NurseryRedisProperties.Pool;
import io.github.icodegarden.nursery.springboot.redis.properties.NurseryRedisProperties.Pool.PoolRedis;
import io.github.icodegarden.nutrient.redis.RedisExecutor;
import io.github.icodegarden.nutrient.redis.jedis.JedisClusterRedisExecutor;
import io.github.icodegarden.nutrient.redis.jedis.JedisPoolRedisExecutor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class JedisRedisExecutorBuilder {

	public static RedisExecutor create(NurseryRedisProperties redisProperties) {
		Cluster cluster = redisProperties.getCluster();
		if (cluster != null) {
			log.info("create RedisExecutor by Cluster");
			ClusterRedis redis = cluster.getRedis();

			Set<HostAndPort> clusterNodes = redis.getNodes().stream()
					.map(node -> new HostAndPort(node.getHost(), node.getPort())).collect(Collectors.toSet());

			ConnectionPoolConfig connectionPoolConfig = new ConnectionPoolConfig();// 使用默认值
			configGenericObjectPoolConfig(connectionPoolConfig, cluster.getRedis());

			JedisCluster jc = new JedisCluster(clusterNodes, redis.getConnectionTimeout(), redis.getSoTimeout(),
					redis.getMaxAttempts(), redis.getUser(), redis.getPassword(), redis.getClientName(),
					connectionPoolConfig, redis.isSsl());
			return new JedisClusterRedisExecutor(jc);
		}

		Pool pool = redisProperties.getPool();
		if (pool != null) {
			log.info("create RedisExecutor by Pool");
			PoolRedis redis = pool.getRedis();

			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();// 使用默认值
			configGenericObjectPoolConfig(jedisPoolConfig, redis);

			JedisPool jp = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getPort(),
					redis.getConnectionTimeout(), redis.getSoTimeout(), redis.getUser(), redis.getPassword(),
					redis.getDatabase(), redis.getClientName(), redis.isSsl());
			return new JedisPoolRedisExecutor(jp);
		}

		return null;
	}

	private static void configGenericObjectPoolConfig(GenericObjectPoolConfig<?> genericObjectPoolConfig,
			NurseryRedisProperties.JedisCommon common) {
		genericObjectPoolConfig.setMaxIdle(common.getMaxIdle());
		genericObjectPoolConfig.setMaxTotal(common.getMaxTotal());
		genericObjectPoolConfig.setMaxWaitMillis(common.getMaxWaitMillis());
		genericObjectPoolConfig.setMinEvictableIdleTimeMillis(common.getMinEvictableIdleTimeMillis());
		genericObjectPoolConfig.setMinIdle(common.getMinIdle());
		genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(common.getTimeBetweenEvictionRunsMillis());
	}
}
