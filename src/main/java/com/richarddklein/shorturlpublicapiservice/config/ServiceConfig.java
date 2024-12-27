/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.config;

import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Service @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Service package.</p>
 */
@Configuration
public class ServiceConfig {
    @Bean
    public ShortUrlPublicApiService
    shortUrlPublicApiService() {
        return new ShortUrlPublicApiServiceImpl();
    }
}
