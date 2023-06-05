package kr.taeu.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {

    @GetMapping
    public Mono<String> get(final ServerHttpRequest request,
                            final ServerHttpResponse response) {
        log.info("Shop MSA Start");
        final HttpHeaders httpHeader = request.getHeaders();
        httpHeader.forEach((key, values) -> log.info("{}: {}", key, values));
        
        log.info("Shop MSA End");

        return Mono.just("Shop MSA Response");
    }
}
