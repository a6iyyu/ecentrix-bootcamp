package com.bootcamp.mini_project.configuration;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.*;

/**
 * Configures Redis cache settings, serialization formats, and expiration rules.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * Defines default Redis cache behavior, key/value serialization, and TTL duration.
     *
     * @return a configured {@link RedisCacheConfiguration} instance
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
    }
}