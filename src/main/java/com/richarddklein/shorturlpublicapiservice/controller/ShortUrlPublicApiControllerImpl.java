/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.ShortUrlUserStatus;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.StatusAndShortUrlUserArray;
import com.richarddklein.shorturlpublicapiservice.service.ShortUrlPublicApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.ShortUrlUserStatus.SUCCESS;

@RestController
@RequestMapping({"/short-url", "/"})
public class ShortUrlPublicApiControllerImpl implements ShortUrlPublicApiController {
    private final ShortUrlPublicApiService shortUrlPublicApiService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlPublicApiControllerImpl(
            ShortUrlPublicApiService shortUrlPublicApiService) {

        this.shortUrlPublicApiService = shortUrlPublicApiService;
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUserArray>>
    getAllUsers() {
        return shortUrlPublicApiService.getAllUsers()
        .map(statusAndShortUrlUserArray -> {

            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndShortUrlUserArray.getStatus().getStatus();

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All users successfully retrieved";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
            statusAndShortUrlUserArray.getStatus().setMessage(message);

            return new ResponseEntity<>(statusAndShortUrlUserArray, httpStatus);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
