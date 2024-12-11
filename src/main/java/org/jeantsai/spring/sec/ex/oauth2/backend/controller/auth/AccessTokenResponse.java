package org.jeantsai.spring.sec.ex.oauth2.backend.controller.auth;

public class AccessTokenResponse extends AbstractAuthResponse {
	private String accessToken;


	public AccessTokenResponse(String accessToken) {
		super (null, null);
		this.accessToken = accessToken;
	}

	public AccessTokenResponse(String error, String message) {
		super(error, message);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
