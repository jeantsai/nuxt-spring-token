package org.jeantsai.spring.sec.ex.oauth2.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityWebFilterChain securityFilterChain(
			ServerHttpSecurity http,
			ReactiveClientRegistrationRepository clientRegistrationRepository
	) {
		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
				)
				.redirectToHttps(withDefaults())
				.oauth2Client(withDefaults())
				.oauth2Login(withDefaults())
				.logout(logout -> {
					final var handler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
					handler.setPostLogoutRedirectUri("{baseUrl}");
					logout.logoutSuccessHandler(handler);
				})
				.authorizeExchange(spec -> spec
						.pathMatchers("/error", "/webjars/**", "/favicon.ico", "/images/**").permitAll()
						.pathMatchers("/").permitAll()
//						.pathMatchers("/api/auth/**").permitAll()
						.pathMatchers("/actuator/**").permitAll()
						.pathMatchers("/login/**", "/oauth2/**").permitAll()
						.pathMatchers("/api/hello").authenticated()
						.anyExchange().authenticated()
				);
		return http.build();
	}

	@Bean
	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return (authorities) -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

			authorities.forEach(authority -> {
				GrantedAuthority mappedAuthority = authority;
				if (OidcUserAuthority.class.isInstance(authority)) {
					OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

					OidcIdToken idToken = oidcUserAuthority.getIdToken();
					OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

					// Map the claims found in idToken and/or userInfo
					// to one or more GrantedAuthority's and add it to mappedAuthorities
				} else if (OAuth2UserAuthority.class.isInstance(authority)) {
					OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;

					Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

					// Map the attributes found in userAttributes
					// to one or more GrantedAuthority's and add it to mappedAuthorities
				} else if (authority instanceof SimpleGrantedAuthority simpleGrantedAuthority) {
					if (StringUtils.startsWithIgnoreCase(simpleGrantedAuthority.getAuthority(), "SCOPE_")) {
						String scope = simpleGrantedAuthority.getAuthority().substring(6);
						mappedAuthority = new SimpleGrantedAuthority(scope);
					}
				}
				mappedAuthorities.add(mappedAuthority);
			});

			return mappedAuthorities;
		};
	}
}
