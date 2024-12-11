package org.jeantsai.spring.sec.ex.oauth2.backend.controller.auth;

public class AbstractAuthResponse {
	private String error;
	private String message;

	public AbstractAuthResponse(String error, String message) {
		this.error = error;
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
