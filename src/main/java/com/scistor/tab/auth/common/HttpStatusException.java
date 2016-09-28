package com.scistor.tab.auth.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Wei Xing
 */
public class HttpStatusException {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequest extends RuntimeException {
        public BadRequest(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class Forbidden extends RuntimeException {
        public Forbidden(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFound extends RuntimeException {
        public NotFound(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class Conflict extends RuntimeException {
        public Conflict(String message) {
            super(message);
        }
    }
    
    @ResponseStatus(HttpStatus.LOCKED)
    public static class Locked extends RuntimeException {
        public Locked(String message) {
            super(message);
        }
    }
}
