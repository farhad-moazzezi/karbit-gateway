package org.karbit.gateway.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("authentication")
public class AuthenticationConfiguration {
	private Boolean enabled;
}
