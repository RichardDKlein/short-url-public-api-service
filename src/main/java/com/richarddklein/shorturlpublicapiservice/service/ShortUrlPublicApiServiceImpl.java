/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import java.util.Base64;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ShortUrlPublicApiServiceImpl implements ShortUrlPublicApiService {
    private final HostUtils hostUtils;
    private final ParameterStoreAccessor parameterStoreAccessor;
    private final WebClient.Builder webClientBuilder;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlPublicApiServiceImpl(
            HostUtils hostUtils,
            ParameterStoreAccessor parameterStoreAccessor,
            WebClient.Builder webClientBuilder) {

        this.hostUtils = hostUtils;
        this.parameterStoreAccessor = parameterStoreAccessor;
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
                    .toEntity(StatusAndJwtToken.class))
            );
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
