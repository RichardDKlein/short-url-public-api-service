/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ShortUrlPublicApiService {
    Mono<ResponseEntity<StatusAndJwtToken>>
    login(UsernameAndPassword usernameAndPassword);
}
