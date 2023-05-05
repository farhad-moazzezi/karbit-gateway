package org.karbit.gateway.auth.client;

import org.karbit.user.common.dto.response.AuthFullResponse;
import reactor.core.publisher.Mono;

public interface UserManagementService {
	Mono<AuthFullResponse> authentication(String token);
}
