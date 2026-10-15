package com.manufacturing.sjf.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Tests the full HTTP layer: JSON in, validation, SJF service, JSON out. */
@SpringBootTest
@AutoConfigureMockMvc
class SchedulingControllerTest {

    private static final String URL = "/api/schedule";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validRequest_returnsOptimizedSchedule() throws Exception {
        String body = """
                {"tasks":[
                  {"id":"T1","name":"Cutting","burstTime":8},
                  {"id":"T2","name":"Welding","burstTime":3},
                  {"id":"T3","name":"Assembly","burstTime":2}]}
                """;

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*].taskName", contains("Assembly", "Welding", "Cutting")))
                .andExpect(jsonPath("$.results[0].executionOrder").value(1))
                .andExpect(jsonPath("$.results[0].startTime").value(0))
                .andExpect(jsonPath("$.results[2].waitingTime").value(5))
                .andExpect(jsonPath("$.results[2].turnaroundTime").value(13))
                .andExpect(jsonPath("$.results[2].completionTime").value(13))
                .andExpect(jsonPath("$.averageWaitingTime").value(2.33))
                .andExpect(jsonPath("$.averageTurnaroundTime").value(6.67))
                .andExpect(jsonPath("$.totalProductionTime").value(13));
    }

    @Test
    void emptyTaskList_returns400() throws Exception {
        assertBadRequest("{\"tasks\":[]}", "Task list is required and must not be empty");
    }

    @Test
    void missingTaskList_returns400() throws Exception {
        assertBadRequest("{}", "Task list is required and must not be empty");
    }

    @Test
    void nullTaskList_returns400() throws Exception {
        assertBadRequest("{\"tasks\":null}", "Task list is required and must not be empty");
    }

    @Test
    void blankTaskName_returns400() throws Exception {
        assertBadRequest(
                "{\"tasks\":[{\"id\":\"T1\",\"name\":\"   \",\"burstTime\":3}]}",
                "Task 1: Task name must not be blank");
    }

    @Test
    void missingTaskName_returns400() throws Exception {
        assertBadRequest(
                "{\"tasks\":[{\"id\":\"T1\",\"burstTime\":3}]}",
                "Task 1: Task name must not be blank");
    }

    @Test
    void zeroBurstTime_returns400() throws Exception {
        assertBadRequest(
                "{\"tasks\":[{\"id\":\"T1\",\"name\":\"Cutting\",\"burstTime\":0}]}",
                "Task 1: Processing time must be a positive integer");
    }

    @Test
    void negativeBurstTime_returns400() throws Exception {
        assertBadRequest(
                "{\"tasks\":[{\"id\":\"T1\",\"name\":\"Cutting\",\"burstTime\":-5}]}",
                "Task 1: Processing time must be a positive integer");
    }

    @Test
    void missingBurstTime_returns400() throws Exception {
        assertBadRequest(
                "{\"tasks\":[{\"id\":\"T1\",\"name\":\"Cutting\"}]}",
                "Task 1: Processing time is required");
    }

    @Test
    void nonNumericBurstTime_returns400() throws Exception {
        assertMalformed("{\"tasks\":[{\"id\":\"T1\",\"name\":\"Cutting\",\"burstTime\":\"abc\"}]}");
    }

    @Test
    void decimalBurstTime_returns400() throws Exception {
        assertMalformed("{\"tasks\":[{\"id\":\"T1\",\"name\":\"Cutting\",\"burstTime\":2.5}]}");
    }

    @Test
    void invalidJson_returns400() throws Exception {
        assertMalformed("{ this is not json");
    }

    @Test
    void emptyBody_returns400() throws Exception {
        assertMalformed("");
    }

    @Test
    void nullTaskInsideList_returns400() throws Exception {
        assertBadRequest("{\"tasks\":[null]}", "Task 1: Task must not be null");
    }

    @Test
    void duplicateTaskIds_returns400() throws Exception {
        String body = """
                {"tasks":[
                  {"id":"T1","name":"Cutting","burstTime":8},
                  {"id":"T1","name":"Welding","burstTime":3}]}
                """;
        assertBadRequest(body, "Duplicate task id: T1");
    }

    @Test
    void multipleProblems_areAllReportedTogether() throws Exception {
        String body = """
                {"tasks":[
                  {"id":"T1","name":"","burstTime":3},
                  {"id":"T2","name":"Welding","burstTime":0}]}
                """;

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasItem("Task 1: Task name must not be blank")))
                .andExpect(jsonPath("$.messages", hasItem("Task 2: Processing time must be a positive integer")));
    }

    @Test
    void wrongHttpMethod_returns405() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void corsPreflight_allowsViteOrigin() throws Exception {
        mockMvc.perform(options(URL)
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void corsPreflight_rejectsOtherOrigins() throws Exception {
        mockMvc.perform(options(URL)
                        .header("Origin", "http://evil.example.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    // ---------- helpers ----------

    private void assertBadRequest(String body, String expectedMessage) throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.messages", hasItem(expectedMessage)));
    }

    private void assertMalformed(String body) throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed request"));
    }
}
