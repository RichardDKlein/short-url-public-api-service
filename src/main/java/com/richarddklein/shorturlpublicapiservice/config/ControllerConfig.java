/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.config;

import com.richarddklein.shorturlpublicapiservice.controller.ShortUrlPublicApiController;
import com.richarddklein.shorturlpublicapiservice.controller.ShortUrlPublicApiControllerImpl;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Controller @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Controller package.</p>
 */
@Configuration
public class ControllerConfig {
    @Autowired
    ShortUrlPublicApiService shortUrlPublicApiService;

    @Bean
    public ShortUrlPublicApiController
    shortUrlPublicApiController() {
        return new ShortUrlPublicApiControllerImpl(shortUrlPublicApiService);
    }
}
