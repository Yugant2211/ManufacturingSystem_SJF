package com.manufacturing.sjf.model;

/**
 * A manufacturing task waiting to be scheduled.
 *
 * @param id        unique identifier such as "T1"
 * @param name      human readable name such as "Welding"
 * @param burstTime processing time of the task in minutes (always positive)
 */
public record Task(String id, String name, int burstTime) {
}
