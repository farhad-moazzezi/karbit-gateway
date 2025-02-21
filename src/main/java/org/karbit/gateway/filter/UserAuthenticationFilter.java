package org.karbit.gateway.filter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karbit.gateway.auth.client.UserManagementService;
import org.karbit.gateway.config.AuthenticationConfiguration;
import org.karbit.skeleton.constant.Header;
import org.karbit.user.common.dto.response.AuthFullResponse;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter implements GlobalFilter {

	private final UserManagementService userService;

	private final AuthenticationConfiguration authenticationConfiguration;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info(exchange.getRequest().getHeaders().toString());
		if (isAuthenticationEnabled()) {
			return authentic(exchange, chain);
		}
		return chain.filter(exchange);
	}

	private boolean isAuthenticationEnabled() {
		return authenticationConfiguration.getEnabled();
	}

	private Mono<Void> authentic(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		if (Boolean.FALSE.equals(UnprotectedPath.isPathUnprotected(request.getPath().toString()))) {
			try {
				return checkAuthentication(request.getHeaders()).flatMap(authResp -> {
					ServerWebExchange webExchange = exchange.mutate()
							.request(exchange.getRequest().mutate().headers(header -> {
								header.set(Header.USER_ID, authResp.getUserId());
								header.set(Header.TOKEN, authResp.getToken());
							}).build()).build();

					return chain.filter(webExchange);
				});
			} catch (Exception exception) {
				log.error("error : ", exception);
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}
		}
		return chain.filter(exchange);
	}

	private Mono<AuthFullResponse> checkAuthentication(HttpHeaders headers) throws IllegalAccessException {
		if (Objects.isNull(headers.get(Header.TOKEN))) {
			throw new IllegalAccessException();
		}
		String token = Objects.requireNonNull(headers.get(Header.TOKEN))
				.stream().findFirst().orElse(null);
		return userService.authentication(token);
	}
}
