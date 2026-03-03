package es.metrica.sept25.evolutivo.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineCacheConfig {

	@Bean(name = "staticCacheManager")
	@Primary
	public CacheManager staticCacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		return cacheManager;
	}

	@Bean(name = "climateCacheManager")
	public CacheManager climateCacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCaffeine(timedCacheBuilder());
		return cacheManager;
	}

	Caffeine<Object, Object> timedCacheBuilder() {
		return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES) // TTL: 30 minutes
				.maximumSize(10_000);
	}

	@Bean(name = "gasCacheManager")
	public CacheManager gasCacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCaffeine(gasCacheBuilder());
		return cacheManager;
	}

	Caffeine<Object, Object> gasCacheBuilder() {
		return Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS) // TTL: 1 hour
				.maximumSize(17_000);
	}
}