/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUserArray;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public interface ShortUrlPublicApiService {
    Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword);

    Mono<StatusAndShortUrlUserArray>
    getAllUsers(ServerHttpRequest request);
}
