package com.leoga.jobapp.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private final ServicesUrl servicesUrl;
    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);

    public GatewayConfig(ServicesUrl servicesUrl) {
        this.servicesUrl = servicesUrl;
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10,20,1);
    }

    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        logger.info("Environment in use: {}", servicesUrl.getEnvironment());
        return builder.routes()
                .route("company-service" , r -> r
                        .path("/companies/**")
                        .filters(f -> f
                                .retry(retryConfig -> retryConfig
                                        .setRetries(10)
                                        .setMethods(HttpMethod.GET)
                                )
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver())
                                )
                                .circuitBreaker(config -> config
                                .setName("jobappBreaker")
                                .setFallbackUri("forward:/fallback/companies")))
                        .uri(servicesUrl.getCompanyUrl()))
                .route("job-service" , r -> r
                        .path("/jobs/**")
                        .uri(servicesUrl.getJobUrl()))
                .route("review-service" , r -> r
                        .path("/reviews/**")
                        .uri(servicesUrl.getReviewUrl()))
                .route("eureka-server", r -> r
                        .path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
