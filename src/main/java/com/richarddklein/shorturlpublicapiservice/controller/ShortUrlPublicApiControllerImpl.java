/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/short-url/public-api", "/"})
public class ShortUrlPublicApiControllerImpl implements ShortUrlPublicApiController {
    private final ShortUrlPublicApiService shortUrlPublicApiService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlPublicApiControllerImpl(
            ShortUrlPublicApiService shortUrlPublicApiService) {

        this.shortUrlPublicApiService = shortUrlPublicApiService;
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
