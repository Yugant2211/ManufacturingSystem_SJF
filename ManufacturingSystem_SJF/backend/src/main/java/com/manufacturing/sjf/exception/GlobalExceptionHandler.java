package com.manufacturing.sjf.exception;

import com.manufacturing.sjf.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts every failure into the same JSON shape (ErrorResponse) so the
 * frontend always knows how to read an error.
 *
 * Extending ResponseEntityExceptionHandler keeps Spring's correct status codes
 * for standard problems (405 wrong method, 415 wrong content type, ...).
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Matches field paths such as "tasks[2].burstTime" to find the task position. */
    private static final Pattern TASK_FIELD = Pattern.compile("^tasks\\[(\\d+)]");

    /** Bean Validation failed (blank name, burst time <= 0, missing field, empty list...). */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toReadableMessage)
                .distinct()
                .toList();

        return badRequest("Validation failed", messages);
    }

    /** Body is missing, is not valid JSON, or a field has the wrong type (e.g. "abc" or 2.5 for burstTime). */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        return badRequest("Malformed request", List.of(
                "Request body is missing or invalid. "
                        + "Check the JSON format and make sure processing time is a whole number."));
    }

    /** Business rule broken inside SJFService. */
    @ExceptionHandler(InvalidTaskException.class)
    public ResponseEntity<Object> handleInvalidTask(InvalidTaskException ex) {
        return badRequest("Validation failed", List.of(ex.getMessage()));
    }

    /** Anything unexpected: log the details, but show the user a generic message. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                List.of("Something went wrong on the server. Please try again."));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String toReadableMessage(FieldError error) {
        Matcher matcher = TASK_FIELD.matcher(error.getField());
        if (matcher.find()) {
            int taskNumber = Integer.parseInt(matcher.group(1)) + 1; // humans count from 1
            return "Task " + taskNumber + ": " + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private ResponseEntity<Object> badRequest(String error, List<String> messages) {
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), error, messages);
        return ResponseEntity.badRequest().body(body);
    }
}
