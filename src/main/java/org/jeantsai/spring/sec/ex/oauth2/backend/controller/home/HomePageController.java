package org.jeantsai.spring.sec.ex.oauth2.backend.controller.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class HomePageController {

	@GetMapping
	public Mono<String> getIndex(Authentication auth, Model model) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		model.addAttribute("isAuthenticated", auth != null && auth.isAuthenticated() && (!(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)));
		model.addAttribute("auth", auth);
		model.addAttribute("user", auth == null ? "" : objectMapper.writeValueAsString(auth.getPrincipal()));
		return Mono.just("index.html");
	}
}
