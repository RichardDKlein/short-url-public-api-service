/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.config;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The Service @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Service package.</p>
 */
@Configuration
public class ServiceConfig {
    @Autowired
    HostUtils hostUtils;

    @Autowired
    ParameterStoreAccessor parameterStoreAccessor;

    @Autowired
    JwtUtils jwtUtils;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ShortUrlPublicApiService
    shortUrlPublicApiService() {
        return new ShortUrlPublicApiServiceImpl(
                hostUtils,
                parameterStoreAccessor,
                jwtUtils,
                webClientBuilder());
    }
}
