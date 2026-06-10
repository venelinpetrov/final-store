package com.vpe.finalstore.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an external service is unavailable
 * Returns HTTP 503 Service Unavailable
 */
public class ServiceUnavailableException extends ApiException {
    public ServiceUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
        initCause(cause);
    }
}
