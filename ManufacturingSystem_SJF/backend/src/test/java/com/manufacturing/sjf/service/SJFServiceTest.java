package com.manufacturing.sjf.service;

import com.manufacturing.sjf.dto.ScheduleResponse;
import com.manufacturing.sjf.exception.InvalidTaskException;
import com.manufacturing.sjf.model.SchedulingResult;
import com.manufacturing.sjf.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SJFServiceTest {

    private SJFService service;

    @BeforeEach
    void setUp() {
        service = new SJFService();
    }

    // ---------- 1. Normal scheduling (the example from the project description) ----------

    @Test
    void normalScheduling_runsShortestTaskFirst() {
        List<Task> tasks = List.of(
                new Task("T1", "Cutting", 8),
                new Task("T2", "Welding", 3),
                new Task("T3", "Assembly", 2),
                new Task("T4", "Painting", 6),
                new Task("T5", "Quality Inspection", 4));

        ScheduleResponse response = service.schedule(tasks);

        assertThat(names(response))
                .containsExactly("Assembly", "Welding", "Quality Inspection", "Painting", "Cutting");
        assertThat(response.totalProductionTime()).isEqualTo(23);
        assertThat(response.averageWaitingTime()).isEqualTo(6.2);
        assertThat(response.averageTurnaroundTime()).isEqualTo(10.8);
    }

    // ---------- 2. Single task ----------

    @Test
    void singleTask_hasZeroWaitingTime() {
        ScheduleResponse response = service.schedule(List.of(new Task("T1", "Cutting", 7)));

        assertThat(response.results()).hasSize(1);
        SchedulingResult only = response.results().get(0);
        assertThat(only.executionOrder()).isEqualTo(1);
        assertThat(only.startTime()).isZero();
        assertThat(only.waitingTime()).isZero();
        assertThat(only.completionTime()).isEqualTo(7);
        assertThat(only.turnaroundTime()).isEqualTo(7);
        assertThat(response.averageWaitingTime()).isZero();
        assertThat(response.averageTurnaroundTime()).isEqualTo(7.0);
        assertThat(response.totalProductionTime()).isEqualTo(7);
    }

    // ---------- 3. Multiple tasks (A=8, B=3, C=2) ----------

    @Test
    void multipleTasks_areOrderedCThenBThenA() {
        ScheduleResponse response = service.schedule(abcTasks());

        assertThat(names(response)).containsExactly("C", "B", "A");
        assertThat(response.results())
                .extracting(SchedulingResult::executionOrder)
                .containsExactly(1, 2, 3);
    }

    // ---------- 4. Equal burst times keep insertion order ----------

    @Test
    void equalBurstTimes_keepInsertionOrder() {
        List<Task> tasks = List.of(
                new Task("T1", "A", 5),
                new Task("T2", "B", 5),
                new Task("T3", "C", 3));

        ScheduleResponse response = service.schedule(tasks);

        assertThat(names(response)).containsExactly("C", "A", "B");
    }

    @Test
    void allBurstTimesEqual_keepOriginalOrder() {
        List<Task> tasks = List.of(
                new Task("T1", "First", 4),
                new Task("T2", "Second", 4),
                new Task("T3", "Third", 4));

        ScheduleResponse response = service.schedule(tasks);

        assertThat(names(response)).containsExactly("First", "Second", "Third");
    }

    // ---------- 5. Invalid burst time ----------

    @Test
    void zeroBurstTime_isRejected() {
        List<Task> tasks = List.of(new Task("T1", "Cutting", 0));

        assertThatThrownBy(() -> service.schedule(tasks))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("positive integer");
    }

    @Test
    void negativeBurstTime_isRejected() {
        List<Task> tasks = List.of(new Task("T1", "Cutting", -4));

        assertThatThrownBy(() -> service.schedule(tasks))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("positive integer");
    }

    // ---------- 6. Empty task list ----------

    @Test
    void emptyTaskList_isRejected() {
        assertThatThrownBy(() -> service.schedule(List.of()))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("must not be empty");
    }

    @Test
    void nullTaskList_isRejected() {
        assertThatThrownBy(() -> service.schedule(null))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("must not be empty");
    }

    // ---------- 7. Waiting time (A=8, B=3, C=2 -> C=0, B=2, A=5) ----------

    @Test
    void waitingTime_isCompletionTimeOfPreviousTask() {
        ScheduleResponse response = service.schedule(abcTasks());

        assertThat(response.results())
                .extracting(SchedulingResult::waitingTime)
                .containsExactly(0, 2, 5);
    }

    // ---------- 8. Turnaround time (C=2, B=5, A=13) ----------

    @Test
    void turnaroundTime_equalsCompletionTimeBecauseArrivalIsZero() {
        ScheduleResponse response = service.schedule(abcTasks());

        assertThat(response.results())
                .extracting(SchedulingResult::turnaroundTime)
                .containsExactly(2, 5, 13);
    }

    // ---------- 9. Completion time and start time ----------

    @Test
    void completionTime_isPreviousCompletionPlusBurstTime() {
        ScheduleResponse response = service.schedule(abcTasks());

        assertThat(response.results())
                .extracting(SchedulingResult::completionTime)
                .containsExactly(2, 5, 13);
        assertThat(response.results())
                .extracting(SchedulingResult::startTime)
                .containsExactly(0, 2, 5);
        assertThat(response.totalProductionTime()).isEqualTo(13);
    }

    // ---------- 10. Average waiting time: (0 + 2 + 5) / 3 = 2.33 ----------

    @Test
    void averageWaitingTime_isTotalWaitingDividedByTaskCount() {
        ScheduleResponse response = service.schedule(abcTasks());

        assertThat(response.averageWaitingTime()).isEqualTo(2.33);
    }

    // ---------- 11. Average turnaround time: (2 + 5 + 13) / 3 = 6.67 ----------

    @Test
    void averageTurnaroundTime_isTotalTurnaroundDividedByTaskCount() {
        ScheduleResponse response = service.schedule(abcTasks());

        assertThat(response.averageTurnaroundTime()).isEqualTo(6.67);
    }

    // ---------- Extra safety checks ----------

    @Test
    void blankTaskName_isRejected() {
        List<Task> tasks = List.of(new Task("T1", "   ", 3));

        assertThatThrownBy(() -> service.schedule(tasks))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("name must not be blank");
    }

    @Test
    void duplicateTaskIds_areRejected() {
        List<Task> tasks = List.of(
                new Task("T1", "Cutting", 3),
                new Task("T1", "Welding", 4));

        assertThatThrownBy(() -> service.schedule(tasks))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("Duplicate task id");
    }

    @Test
    void schedule_doesNotChangeTheCallersList() {
        List<Task> original = new ArrayList<>(abcTasks());

        service.schedule(original);

        assertThat(original).containsExactlyElementsOf(abcTasks());
    }

    // ---------- helpers ----------

    private List<Task> abcTasks() {
        return List.of(
                new Task("T1", "A", 8),
                new Task("T2", "B", 3),
                new Task("T3", "C", 2));
    }

    private List<String> names(ScheduleResponse response) {
        return response.results().stream().map(SchedulingResult::taskName).toList();
    }
}
