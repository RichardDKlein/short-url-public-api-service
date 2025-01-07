/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.ShortUrlUserStatus;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUserArray;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.ShortUrlUserStatus.SUCCESS;

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
    login(@RequestBody UsernameAndPassword usernameAndPassword,
          ServerHttpRequest request) {

        return shortUrlPublicApiService.login(usernameAndPassword)
            .flatMap(statusAndJwtToken -> {
                Status status = statusAndJwtToken.getStatus();
                String jwtToken = statusAndJwtToken.getJwtToken();

                ResponseCookie.ResponseCookieBuilder authCookieBuilder =
                    ResponseCookie.from(AUTH_TOKEN, jwtToken)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .path("/");

                if (!hostUtils.isRunningLocally(request)) {
                    return hostUtils.getDomain()
                        .flatMap(domain -> {
                            authCookieBuilder.domain(domain);
                            return buildResponseEntity(status, authCookieBuilder);
                        });
                }

                return buildResponseEntity(status, authCookieBuilder);
            });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUserArray>>
    getAllUsers(ServerHttpRequest request) {
        return shortUrlPublicApiService.getAllUsers(request)
        .map(statusAndShortUrlUserArray -> {

            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndShortUrlUserArray.getStatus().getStatus();

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All users successfully retrieved";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
            statusAndShortUrlUserArray.getStatus().setMessage(message);

            return new ResponseEntity<>(statusAndShortUrlUserArray, httpStatus);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private Mono<ResponseEntity<Status>>
    buildResponseEntity(
            Status status,
            ResponseCookie.ResponseCookieBuilder authCookieBuilder) {

        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authCookieBuilder.build().toString())
                .body(status));
    }

}
