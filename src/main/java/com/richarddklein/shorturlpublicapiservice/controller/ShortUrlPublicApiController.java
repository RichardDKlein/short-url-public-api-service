/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public interface ShortUrlPublicApiController {
    @PostMapping("/signup-user")
    Mono<ResponseEntity<Status>>
    signupUser(@RequestBody ShortUrlUser shortUrlUser);

    @PostMapping("/login")
    Mono<ResponseEntity<Status>>
    login(@RequestBody UsernameAndPassword usernameAndPassword);

    @PostMapping("/logout")
    Mono<ResponseEntity<Status>>
    logout(ServerHttpRequest request);

    @GetMapping("/get-user/{username}")
    Mono<ResponseEntity<StatusAndShortUrlUser>>
    getUser(@PathVariable String username, ServerHttpRequest request);
}
