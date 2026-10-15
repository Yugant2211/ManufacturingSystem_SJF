package com.manufacturing.sjf.dto;

import java.util.List;

/**
 * Body returned whenever a request fails.
 *
 * @param status   HTTP status code, e.g. 400
 * @param error    short description, e.g. "Validation failed"
 * @param messages one or more readable messages the frontend can show to the user
 */
public record ErrorResponse(int status, String error, List<String> messages) {
}
