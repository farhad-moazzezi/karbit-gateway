package org.karbit.gateway.filter;

import java.util.Objects;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karbit.gateway.auth.client.UserManagementService;
import org.karbit.user.common.dto.response.AuthResp;
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

	private static final String LOGIN_URL = "/user/auth/login";

	private static final String SIGNUP_URL = "/user/auth/signup";

	private static final String ACTIVATE_URL = "/user/auth/activate";

	private static final Set<String> NOT_PROTECTED_PATH = Set.of(LOGIN_URL, SIGNUP_URL, ACTIVATE_URL);

	private final UserManagementService userService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		log.info(exchange.getRequest().getHeaders().toString());
		if (Boolean.FALSE.equals(NOT_PROTECTED_PATH.contains(request.getPath().value()))) {
			try {
				return checkAuthentication(request.getHeaders()).flatMap(authResp -> {
					ServerWebExchange webExchange = exchange.mutate()
							.request(exchange.getRequest().mutate().headers(header -> {
								header.set("User-Id", authResp.getUserId());
								header.set("Token", authResp.getToken());
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

	private Mono<AuthResp> checkAuthentication(HttpHeaders headers) throws IllegalAccessException {
		if (Objects.isNull(headers.get("Token"))) {
			throw new IllegalAccessException();
		}
		String token = headers.get("Token").stream().findFirst().orElse(null);
		return userService.authentication(token);
	}
}
