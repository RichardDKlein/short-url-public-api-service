/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping({"/short-url", "/"})
public class ShortUrlPublicApiControllerImpl implements ShortUrlPublicApiController {
    private static final String AUTH_TOKEN = "auth_token";

    private final ShortUrlPublicApiService shortUrlPublicApiService;
    private final HostUtils hostUtils;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlPublicApiControllerImpl(
            ShortUrlPublicApiService shortUrlPublicApiService,
            HostUtils hostUtils) {

        this.shortUrlPublicApiService = shortUrlPublicApiService;
        this.hostUtils = hostUtils;
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

                ResponseEntity<Status> responseEntity = ResponseEntity
                        .status(httpStatus)
                        .body(status);

                HttpHeaders headers = new HttpHeaders();
                if (jwtToken != null && !jwtToken.isBlank()) {
                    headers.add(HttpHeaders.SET_COOKIE, ResponseCookie.from(AUTH_TOKEN, jwtToken)
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

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
