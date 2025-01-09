/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.ShortUrlUserStatus;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.ShortUrlUserStatus.NOT_LOGGED_IN;

@RestController
@RequestMapping({"/short-url", "/"})
public class ShortUrlPublicApiControllerImpl implements ShortUrlPublicApiController {
    private static final String AUTH_TOKEN = "auth_token";

    private final ShortUrlPublicApiService shortUrlPublicApiService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlPublicApiControllerImpl(
            ShortUrlPublicApiService shortUrlPublicApiService) {

        this.shortUrlPublicApiService = shortUrlPublicApiService;
    }

    @Override
    public Mono<ResponseEntity<Status>>
    signupUser(@RequestBody ShortUrlUser shortUrlUser) {
        return shortUrlPublicApiService.signupUser(shortUrlUser)
            .map(serviceResponseEntity -> serviceResponseEntity);
    }

    @Override
    public Mono<ResponseEntity<Status>>
    login(@RequestBody UsernameAndPassword usernameAndPassword) {
        return shortUrlPublicApiService.login(usernameAndPassword)
            .map(serviceResponseEntity -> {
                HttpStatusCode httpStatus = serviceResponseEntity.getStatusCode();
                Status status = Objects.requireNonNull(
                        serviceResponseEntity.getBody()).getStatus();
                String jwtToken = Objects.requireNonNull(
                        serviceResponseEntity.getBody()).getJwtToken();

                HttpHeaders headers = new HttpHeaders();
                if (jwtToken != null && !jwtToken.isBlank()) {
                    headers.add(HttpHeaders.SET_COOKIE,
                        ResponseCookie.from(AUTH_TOKEN, jwtToken)
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("None")
                            .path("/")
                            .build()
                            .toString());
                }
                return new ResponseEntity<>(status, headers, httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<Status>> logout(ServerHttpRequest request) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie authCookie = cookies.getFirst(AUTH_TOKEN);

        if (authCookie == null) {
            return Mono.just(
                new ResponseEntity<>(
                    new Status(
                        NOT_LOGGED_IN,
                        "No user is currently logged in"),
                    HttpStatus.BAD_REQUEST));
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE,
            ResponseCookie.from(AUTH_TOKEN, "")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build()
                .toString());

        return Mono.just(
            new ResponseEntity<>(
                new Status(
                    ShortUrlUserStatus.SUCCESS,
                    "Logged out successfully"),
                responseHeaders,
                HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUser>>
    getUser(String username, ServerHttpRequest request) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie authCookie = cookies.getFirst(AUTH_TOKEN);

        if (authCookie == null) {
            return Mono.just(
                new ResponseEntity<>(
                    new StatusAndShortUrlUser(
                        new Status(
                            NOT_LOGGED_IN,
                            "No user is currently logged in"),
                        null),
                        HttpStatus.UNAUTHORIZED));
        }

        String jwtToken = authCookie.getValue();

        return shortUrlPublicApiService.getUser(username, jwtToken)
                .map(serviceResponseEntity -> serviceResponseEntity);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
