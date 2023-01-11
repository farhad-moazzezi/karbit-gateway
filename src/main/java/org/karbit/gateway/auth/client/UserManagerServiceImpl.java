package org.karbit.gateway.auth.client;

import lombok.AllArgsConstructor;
import org.karbit.user.common.dto.response.AuthResp;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@PropertySource("classpath:application.properties")
@AllArgsConstructor
public class UserManagerServiceImpl implements UserManagementService {

	private UserManagerProps userManagerProps;

	private final WebClient.Builder webClientBuilder;

	@Override
	public Mono<AuthResp> authentication(String token) {
		WebClient webClient = webClientBuilder.baseUrl(userManagerProps.getUrl()).build();
		return webClient.post().uri("/auth").header("Token", token).retrieve().bodyToMono(AuthResp.class);
	}
}
