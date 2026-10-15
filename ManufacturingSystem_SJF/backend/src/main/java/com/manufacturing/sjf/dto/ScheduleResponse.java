package com.manufacturing.sjf.dto;

import com.manufacturing.sjf.model.SchedulingResult;

import java.util.List;

/**
 * Body returned by POST /api/schedule.
 *
 * @param results               tasks in SJF execution order with their times
 * @param averageWaitingTime    total waiting time / number of tasks (2 decimals)
 * @param averageTurnaroundTime total turnaround time / number of tasks (2 decimals)
 * @param totalProductionTime   completion time of the last task
 */
public record ScheduleResponse(
        List<SchedulingResult> results,
        double averageWaitingTime,
        double averageTurnaroundTime,
        int totalProductionTime) {
}
