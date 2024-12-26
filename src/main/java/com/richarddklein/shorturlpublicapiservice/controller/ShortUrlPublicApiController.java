/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUserArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public interface ShortUrlPublicApiController {
    @GetMapping("/get-all-users")
    Mono<ResponseEntity<StatusAndShortUrlUserArray>>
    getAllUsers();
}
