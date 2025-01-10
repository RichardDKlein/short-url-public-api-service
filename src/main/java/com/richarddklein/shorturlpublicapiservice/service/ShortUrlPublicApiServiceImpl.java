/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import java.util.Base64;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.WRONG_USER;

@Service
public class ShortUrlPublicApiServiceImpl implements ShortUrlPublicApiService {
    private static final String ADMIN_ROLE = "ADMIN";

    private final HostUtils hostUtils;
    private final ParameterStoreAccessor parameterStoreAccessor;
    private final JwtUtils jwtUtils;
    private final WebClient.Builder webClientBuilder;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlPublicApiServiceImpl(
            HostUtils hostUtils,
            ParameterStoreAccessor parameterStoreAccessor,
            JwtUtils jwtUtils,
            WebClient.Builder webClientBuilder) {

        this.hostUtils = hostUtils;
        this.parameterStoreAccessor = parameterStoreAccessor;
        this.jwtUtils = jwtUtils;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<ResponseEntity<Status>>
    signupUser(ShortUrlUser shortUrlUser) {
        return hostUtils.getShortUrlUserServiceBaseUrl()
            .flatMap(baseUrl -> getAdminJwtToken(baseUrl)
                .flatMap(adminJwtToken -> webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/signup")
                    .header("Authorization", "Bearer " + adminJwtToken)
                    .bodyValue(shortUrlUser)
                    .retrieve()
                    .onStatus(
                        status -> !status.is2xxSuccessful(),
                        // Don't throw an exception, just continue
                        response -> Mono.empty()
                    )
                    .toEntity(Status.class))
            );
    }

    @Override
    public Mono<ResponseEntity<StatusAndJwtToken>>
    login(UsernameAndPassword usernameAndPassword) {
        return hostUtils.getShortUrlUserServiceBaseUrl()
            .flatMap(baseUrl -> getAdminJwtToken(baseUrl)
                .flatMap(adminJwtToken -> webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/login")
                    .header("Authorization", "Bearer " + adminJwtToken)
                    .bodyValue(usernameAndPassword)
                    .retrieve()
                    .onStatus(
                        status -> !status.is2xxSuccessful(),
                        // Don't throw an exception, just continue
                        response -> Mono.empty()
                    )
                    .toEntity(StatusAndJwtToken.class))
            );
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUser>>
    getUser(String username, String jwtToken) {
        return jwtUtils.extractUsernameAndRoleFromToken(jwtToken)
            .flatMap(usernameAndRole -> {
                if (!usernameAndRole.getRole().equals(ADMIN_ROLE) &&
                        !usernameAndRole.getUsername().equals(username)) {
                    return Mono.just(
                        new ResponseEntity<>(
                            new StatusAndShortUrlUser(
                                new Status(
                                    WRONG_USER,
                                    "Must be logged in as specified user or as admin"),
                                null),
                            HttpStatus.FORBIDDEN
                        )
                    );
                }

                return hostUtils.getShortUrlUserServiceBaseUrl()
                    .flatMap(baseUrl -> getAdminJwtToken(baseUrl)
                        .flatMap(adminJwtToken -> webClientBuilder.build()
                            .get()
                            .uri(baseUrl + "/specific/" + username)
                            .header("Authorization", "Bearer " + adminJwtToken)
                            .retrieve()
                            .onStatus(
                                status -> !status.is2xxSuccessful(),
                                // Don't throw an exception, just continue
                                response -> Mono.empty()
                            )
                            .toEntity(StatusAndShortUrlUser.class)));
            });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private Mono<String> getAdminJwtToken(String baseUrl) {
        return parameterStoreAccessor.getAdminUsername().flatMap(adminUsername ->
            parameterStoreAccessor.getAdminPassword().flatMap(adminPassword -> {
                String auth = adminUsername + ":" + adminPassword;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

                return webClientBuilder
                    .build()
                    .get()
                    .uri(baseUrl + "/admin-jwt")
                    .header("Authorization", "Basic " + encodedAuth)
                    .retrieve()
                    .bodyToMono(StatusAndJwtToken.class)
                        .map(StatusAndJwtToken::getJwtToken);
            }));
    }
}
