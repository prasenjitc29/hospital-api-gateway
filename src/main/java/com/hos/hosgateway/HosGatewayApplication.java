package com.hos.hosgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
@Configuration
@EnableWebFlux
public class HosGatewayApplication {

	  private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
	  private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
	  private static final String ALLOWED_ORIGIN = "*";
	  private static final String MAX_AGE = "3600";
	  
	public static void main(String[] args) {
		SpringApplication.run(HosGatewayApplication.class, args);
	}
	
	@Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
//		return builder.routes()
//                .route(r -> r
//                        .alwaysTrue()
//                        .filters(f -> f.filter(stripPrefixGatewayFilterFactory().apply(c -> c.setParts(1))))
//                        .uri("no://op"))
//                .build();
        return builder.routes()
        		.route("hos-user-service", r -> r.path("/**")
        		.filters(f -> f.filter(stripPrefixGatewayFilterFactory().apply(c -> c.setParts(1))))        
        		.uri("lb://hos-user-service"))
                .build();
	}
	
	@Bean
	public StripPrefixGatewayFilterFactory stripPrefixGatewayFilterFactory() {
	  return new StripPrefixGatewayFilterFactory();
	}
	
	@Bean
	public WebFilter corsWebFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
		      ServerHttpRequest request = ctx.getRequest();
		      if (CorsUtils.isCorsRequest(request)) {
		        ServerHttpResponse response = ctx.getResponse();
		        HttpHeaders headers = response.getHeaders();
		        headers.add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
		        headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
		        headers.add("Access-Control-Max-Age", MAX_AGE);
		        headers.add("Access-Control-Allow-Headers",ALLOWED_HEADERS);
		        if (request.getMethod() == HttpMethod.OPTIONS) {
		          response.setStatusCode(HttpStatus.OK);
		          return Mono.empty();
		        }
		      }
		      return chain.filter(ctx);
		    };
    }
	@Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
