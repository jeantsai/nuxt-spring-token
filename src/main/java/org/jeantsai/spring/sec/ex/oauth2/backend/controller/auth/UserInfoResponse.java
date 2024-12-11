package org.jeantsai.spring.sec.ex.oauth2.backend.controller.auth;

public class UserInfoResponse extends AbstractAuthResponse {
	private String username;
	private String image;
	private String error;
	private String message;

	public UserInfoResponse() {
		super (null, null);
	}

	public UserInfoResponse(String username) {
		super (null, null);
		this.username = username;
	}

	public UserInfoResponse(String error, String message) {
		super(error, message);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
