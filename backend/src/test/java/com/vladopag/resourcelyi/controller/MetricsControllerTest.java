package com.vladopag.resourcelyi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.vladopag.resourcelyi.service.MetricsService;
import com.vladopag.resourcelyi.support.TestSnapshots;

@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetricsService metricsService;

    @Test
    void metricsReturnsSnapshot() throws Exception {
        when(metricsService.collect()).thenReturn(TestSnapshots.sample());

        mockMvc.perform(get("/api/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.system.hostname").value("test-host"))
                .andExpect(jsonPath("$.cpu.totalPercent").value(25.5))
                .andExpect(jsonPath("$.memory.usedPercent").value(50.0));
    }

    @Test
    void metricsReturnsServerErrorOnFailure() throws Exception {
        when(metricsService.collect()).thenThrow(new RuntimeException("collection failed"));

        mockMvc.perform(get("/api/metrics"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("collection failed"));
    }
}
