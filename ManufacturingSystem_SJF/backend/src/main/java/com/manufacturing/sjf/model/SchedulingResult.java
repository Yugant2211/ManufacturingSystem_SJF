package com.manufacturing.sjf.model;

/**
 * One row of the schedule: a task together with the times calculated for it.
 *
 * @param executionOrder position in the schedule (1 = runs first)
 * @param taskId         id of the task
 * @param taskName       name of the task
 * @param burstTime      processing time of the task
 * @param startTime      time at which the machine starts the task
 * @param completionTime time at which the task finishes
 * @param waitingTime    time the task waited before starting
 * @param turnaroundTime total time from arrival (0) until completion
 */
public record SchedulingResult(
        int executionOrder,
        String taskId,
        String taskName,
        int burstTime,
        int startTime,
        int completionTime,
        int waitingTime,
        int turnaroundTime) {
}
