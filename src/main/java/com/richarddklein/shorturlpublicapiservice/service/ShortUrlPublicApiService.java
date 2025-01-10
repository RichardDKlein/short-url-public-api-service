/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ShortUrlPublicApiService {
    Mono<ResponseEntity<Status>>
    signupUser(ShortUrlUser shortUrlUser);

    Mono<ResponseEntity<StatusAndJwtToken>>
    login(UsernameAndPassword usernameAndPassword);

    Mono<ResponseEntity<StatusAndShortUrlUser>>
    getUser(String username, String jwtToken);
}
