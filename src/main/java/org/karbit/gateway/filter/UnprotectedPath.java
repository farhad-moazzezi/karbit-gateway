package org.karbit.gateway.filter;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum UnprotectedPath {
	LOGOUT_URL("/user/auth/logout"),
	LOGIN_URL("/user/auth/login"),
	SIGNUP_URL("/user/auth/signup"),
	GET_ARTICLE_SUMMARY_URL("/doc/article/summary/**"),
	READ_ARTICLE_URL("/doc/article/detail/**"),
	GET_ARTICLES_URL("/articles");


	UnprotectedPath(String path) {
		this.path = path;
	}

	private final String path;

	public static boolean isPathUnprotected(String requestedPath)
	{
		return Arrays.stream(UnprotectedPath.values()).map(rawPath -> {
			String pathRegex = rawPath.getPath();
			if (rawPath.getPath().endsWith("/**")){
				pathRegex = rawPath.getPath()
						.replace("/**", "(/.*)?");
			}
			return "^".concat(pathRegex);
		}).anyMatch(requestedPath::matches);
	}

}
