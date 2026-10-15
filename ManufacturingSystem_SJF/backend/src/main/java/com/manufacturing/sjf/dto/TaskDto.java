package com.manufacturing.sjf.dto;

import com.manufacturing.sjf.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * A task as it arrives in the JSON request.
 * Validation annotations are checked automatically because the controller uses @Valid.
 */
public record TaskDto(

        @NotBlank(message = "Task id is required")
        String id,

        @NotBlank(message = "Task name must not be blank")
        String name,

        @NotNull(message = "Processing time is required")
        @Positive(message = "Processing time must be a positive integer")
        Integer burstTime) {

    /** Converts the validated request object into the internal model. */
    public Task toTask() {
        return new Task(id.trim(), name.trim(), burstTime);
    }
}
