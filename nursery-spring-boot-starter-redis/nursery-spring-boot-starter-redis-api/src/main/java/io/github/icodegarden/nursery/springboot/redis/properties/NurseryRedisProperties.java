package io.github.icodegarden.nursery.springboot.redis.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.redis")
@Getter
@Setter
@ToString
public class NurseryRedisProperties {

	private Cluster cluster;
	private Pool pool;

	@Getter
	@Setter
	@ToString
	public static class Cluster {
		private ClusterRedis redis;
		private ClusterLettuce lettuce;

		@Getter
		@Setter
		@ToString
		public static class ClusterRedis extends JedisCommon {
			private List<Node> nodes;
			private int maxAttempts = 5;
		}

		@Getter
		@Setter
		@ToString
		public static class ClusterLettuce extends LettuceCommon {
			private List<Node> nodes;
		}

		@Getter
		@Setter
		@ToString
		public static class Node {
			private String host;
			private int port;
		}
	}

	@Getter
	@Setter
	@ToString
	public static class Pool {
		private PoolRedis redis;
		private PoolLettuce lettuce;

		@Getter
		@Setter
		@ToString
		public static class PoolRedis extends JedisCommon {
			private String host;
			private int port;
			private int database = 0;
		}

		@Getter
		@Setter
		@ToString
		public static class PoolLettuce extends LettuceCommon {
			private String host;
			private int port;
			private int database = 0;
		}
	}

	@Getter
	@Setter
	@ToString
	public static class JedisCommon {
		private int connectionTimeout = 3000;
		private int soTimeout = 3000;
		private String user;
		private String password;
		private String clientName;
		private boolean ssl = false;

		private int maxIdle = 8;// 默认8
		private int maxTotal = 8;// 默认8
		private int maxWaitMillis = -1;// 默认-1
		private int minIdle = 0;// 默认0
		private long minEvictableIdleTimeMillis = 60000;// 默认60000
		private long timeBetweenEvictionRunsMillis = -1;// 默认-1
	}

	@Getter
	@Setter
	@ToString
	public static class LettuceCommon {
		private int connectionTimeout = 3000;
		private int soTimeout = 3000;
		private String user;
		private String password;
		private String clientName = "lettuce";
		private boolean ssl = false;

		private Integer ioThreadPoolSize;
		private Integer computationThreadPoolSize;
		private Long reconnectDelayMs;
	}
}