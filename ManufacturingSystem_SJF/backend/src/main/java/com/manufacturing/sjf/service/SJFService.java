package com.manufacturing.sjf.service;

import com.manufacturing.sjf.dto.ScheduleResponse;
import com.manufacturing.sjf.exception.InvalidTaskException;
import com.manufacturing.sjf.model.SchedulingResult;
import com.manufacturing.sjf.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Non-preemptive Shortest Job First (SJF) scheduler for one production machine.
 *
 * Assumptions: every task arrives at time 0, there is a single machine,
 * and a task that has started always runs until it is finished.
 */
@Service
public class SJFService {

    /** All tasks arrive at the same moment in this version of the project. */
    private static final int ARRIVAL_TIME = 0;

    public ScheduleResponse schedule(List<Task> tasks) {
        validate(tasks);

        List<Task> orderedTasks = sortByShortestJobFirst(tasks);
        List<SchedulingResult> results = calculateTimes(orderedTasks);

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        for (SchedulingResult result : results) {
            totalWaitingTime += result.waitingTime();
            totalTurnaroundTime += result.turnaroundTime();
        }

        int taskCount = results.size();
        int totalProductionTime = results.get(taskCount - 1).completionTime();

        return new ScheduleResponse(
                results,
                average(totalWaitingTime, taskCount),
                average(totalTurnaroundTime, taskCount),
                totalProductionTime);
    }

    /**
     * Step 1 of SJF: the shortest burst time goes first.
     * List.sort is stable, so tasks with equal burst times keep the order
     * in which they were added (insertion order is the tie-breaker).
     */
    private List<Task> sortByShortestJobFirst(List<Task> tasks) {
        List<Task> ordered = new ArrayList<>(tasks); // copy: do not change the caller's list
        ordered.sort(Comparator.comparingInt(Task::burstTime));
        return ordered;
    }

    /**
     * Step 2: walk through the sorted tasks and calculate the times.
     *   start time      = completion time of the previous task (0 for the first)
     *   completion time = start time + burst time
     *   waiting time    = start time - arrival time
     *   turnaround time = completion time - arrival time
     */
    private List<SchedulingResult> calculateTimes(List<Task> orderedTasks) {
        List<SchedulingResult> results = new ArrayList<>();
        int currentTime = 0;

        for (int i = 0; i < orderedTasks.size(); i++) {
            Task task = orderedTasks.get(i);

            int startTime = currentTime;
            int completionTime = startTime + task.burstTime();
            int waitingTime = startTime - ARRIVAL_TIME;
            int turnaroundTime = completionTime - ARRIVAL_TIME;

            results.add(new SchedulingResult(
                    i + 1,
                    task.id(),
                    task.name(),
                    task.burstTime(),
                    startTime,
                    completionTime,
                    waitingTime,
                    turnaroundTime));

            currentTime = completionTime; // the machine is busy until this task ends
        }
        return results;
    }

    /** Average rounded to 2 decimal places, e.g. 7 / 3 = 2.33. */
    private double average(int total, int count) {
        return Math.round((double) total / count * 100.0) / 100.0;
    }

    private void validate(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            throw new InvalidTaskException("Task list is required and must not be empty");
        }

        Set<String> seenIds = new HashSet<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new InvalidTaskException("Task must not be null");
            }
            if (task.id() == null || task.id().isBlank()) {
                throw new InvalidTaskException("Task id is required");
            }
            if (task.name() == null || task.name().isBlank()) {
                throw new InvalidTaskException("Task '" + task.id() + "': name must not be blank");
            }
            if (task.burstTime() <= 0) {
                throw new InvalidTaskException(
                        "Task '" + task.id() + "': processing time must be a positive integer");
            }
            if (!seenIds.add(task.id())) {
                throw new InvalidTaskException("Duplicate task id: " + task.id());
            }
        }
    }
}
