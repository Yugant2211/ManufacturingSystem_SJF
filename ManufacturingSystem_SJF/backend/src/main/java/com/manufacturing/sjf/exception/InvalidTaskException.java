package com.manufacturing.sjf.exception;

/**
 * Thrown by the service when the tasks it receives cannot be scheduled
 * (empty list, blank name, burst time <= 0, duplicate ids).
 */
public class InvalidTaskException extends RuntimeException {

    public InvalidTaskException(String message) {
        super(message);
    }
}
