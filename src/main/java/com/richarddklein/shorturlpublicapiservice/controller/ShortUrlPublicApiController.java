/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public interface ShortUrlPublicApiController {
    @PostMapping("/login")
    Mono<ResponseEntity<Status>>
    login(@RequestBody UsernameAndPassword usernameAndPassword);

    @PostMapping("/logout")
    Mono<ResponseEntity<Status>>
    logout(ServerHttpRequest request);
}
