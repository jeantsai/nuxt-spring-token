package org.jeantsai.spring.sec.ex.oauth2.backend.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

	public static final String CLIENT_REGISTRATION_ID = "github";

	@Autowired
	private ReactiveOAuth2AuthorizedClientService authorizedClientService;

	@PostMapping("/stk")
	public Mono<ResponseEntity<AccessTokenResponse>> nuxtLocalSignIn(Authentication auth) {
		return this.getAccessToken(auth);
	}

	@GetMapping("/stk")
	public Mono<ResponseEntity<AccessTokenResponse>> getAccessToken(Authentication auth) {
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return Mono.just(
					ResponseEntity.status(401)
							.body(new AccessTokenResponse("Unauthorized access", "You do not have permission to access this resource."))
			);
		}
		Mono<? extends OAuth2AuthorizedClient> objMono = this.authorizedClientService.loadAuthorizedClient(CLIENT_REGISTRATION_ID, auth.getName());
		return objMono.flatMap(
						client -> {
							if (client.getAccessToken() != null) {
								return Mono.just(
										ResponseEntity.ok().body(
												new AccessTokenResponse(client.getAccessToken().getTokenValue())
										)
								);
							} else {
								return Mono.just(
										ResponseEntity.status(404).body(
												new AccessTokenResponse(
														"Missing access token",
														String.format(
																"No access token could be found for OAuth2 provider %s.",
																CLIENT_REGISTRATION_ID
														)
												)
										)
								);
							}
						})
				.switchIfEmpty(Mono.just(
						ResponseEntity.status(404)
								.body(new AccessTokenResponse("Missing access token", "No access token could be found for OAuth2 provider github-login."))
				));
	}

	@GetMapping("/sui")
	public Mono<ResponseEntity<UserInfoResponse>> nuxtGetSession(Authentication auth) {
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return Mono.just(
					ResponseEntity.status(401)
							.body(new UserInfoResponse("Unauthorized access", "You do not have permission to access this resource."))
			);
		}
		final var principal = auth.getPrincipal();
		String username = auth.getName();
		String image = null;
		if (principal instanceof OidcUser oidcUser) {
			username = oidcUser.getAttribute("name");
			image = oidcUser.getIdToken().getPicture();
		}
		final var userInfoResponse = new UserInfoResponse(username);
		if (image != null) {
			userInfoResponse.setImage(image);
		}
		return Mono.just(
				ResponseEntity.ok().body(userInfoResponse)
		);
	}
}
