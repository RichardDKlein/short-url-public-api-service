/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUserArray;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
    public Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword) {
        return null;
    }

    @Override
    public Mono<StatusAndShortUrlUserArray>
    getAllUsers(ServerHttpRequest request) {
        return hostUtils.getShortUrlUserServiceBaseUrl(request)
            .flatMap(baseUrl -> getAdminJwtToken(baseUrl)
            .flatMap(adminJwtToken -> webClientBuilder.build()
                .get()
                .uri(baseUrl + "/all")
                .header("Authorization", "Bearer " + adminJwtToken)
                .retrieve()
                .bodyToMono(StatusAndShortUrlUserArray.class))
            );
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private Mono<String> getAdminJwtToken(String baseUrl) {
        return parameterStoreAccessor.getAdminUsername().flatMap(adminUsername ->
            parameterStoreAccessor.getAdminPassword().flatMap(adminPassword ->
                webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/admin-jwt")
                    .header("Authorization",
                            "Basic " + adminUsername + ":" + adminPassword)
                    .retrieve()
                    .bodyToMono(String.class)));
    }
}
