package org.jeantsai.spring.sec.ex.oauth2.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/hello")
public class HelloWorldController {

	@GetMapping("/")
	public Mono<String> hello() {
		return Mono.just("Hello World!");
	}
}
