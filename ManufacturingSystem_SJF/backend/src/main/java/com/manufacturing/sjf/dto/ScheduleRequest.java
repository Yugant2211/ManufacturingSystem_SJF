package com.manufacturing.sjf.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Body of POST /api/schedule.
 *
 * @param tasks the tasks to schedule; must be present and not empty
 */
public record ScheduleRequest(

        @NotEmpty(message = "Task list is required and must not be empty")
        List<@NotNull(message = "Task must not be null") @Valid TaskDto> tasks) {
}
