/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUserArray;
import reactor.core.publisher.Mono;

public interface ShortUrlPublicApiService {
    Mono<StatusAndShortUrlUserArray>
    getAllUsers();
}
