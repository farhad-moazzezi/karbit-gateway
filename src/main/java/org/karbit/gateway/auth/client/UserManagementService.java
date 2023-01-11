package org.karbit.gateway.auth.client;

import org.karbit.user.common.dto.response.AuthResp;
import reactor.core.publisher.Mono;

public interface UserManagementService {
	Mono<AuthResp> authentication(String token);
}
