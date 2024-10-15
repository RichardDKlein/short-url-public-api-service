/**
 * The Short URL Public API Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlpublicapiservice.dto;

import com.richarddklein.shorturlcommonlibrary.status.ShortUrlPublicApiStatus;

public class Status {
    private ShortUrlPublicApiStatus status;
    private String message;

    public Status(ShortUrlPublicApiStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status(ShortUrlPublicApiStatus status) {
        this.status = status;
    }

    public ShortUrlPublicApiStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlPublicApiStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
