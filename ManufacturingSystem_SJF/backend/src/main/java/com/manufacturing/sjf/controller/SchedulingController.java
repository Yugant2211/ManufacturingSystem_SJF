package com.manufacturing.sjf.controller;

import com.manufacturing.sjf.dto.ScheduleRequest;
import com.manufacturing.sjf.dto.ScheduleResponse;
import com.manufacturing.sjf.dto.TaskDto;
import com.manufacturing.sjf.model.Task;
import com.manufacturing.sjf.service.SJFService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST layer only: receives the request, hands the tasks to the service
 * and returns the result. The SJF algorithm itself lives in SJFService.
 */
@RestController
@RequestMapping("/api")
public class SchedulingController {

    private final SJFService sjfService;

    public SchedulingController(SJFService sjfService) {
        this.sjfService = sjfService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<ScheduleResponse> schedule(@Valid @RequestBody ScheduleRequest request) {
        List<Task> tasks = request.tasks().stream()
                .map(TaskDto::toTask)
                .toList();

        return ResponseEntity.ok(sjfService.schedule(tasks));
    }
}
