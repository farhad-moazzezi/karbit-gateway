package org.karbit.gateway.filter;

import lombok.Getter;

@Getter
public enum UnprotectedPath {
	LOGOUT_URL("/user/auth/logout"),
	LOGIN_URL("/user/auth/login"),
	SIGNUP_URL("/user/auth/signup"),
	ACTIVATE_URL("/user/auth/activate"),
	GET_ARTICLE_SUMMARY_URL("doc/article/summary/**");


	UnprotectedPath(String path) {
		this.path = path;
	}

	private final String path;
}
