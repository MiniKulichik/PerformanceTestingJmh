package com.sevnis.jmhdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@State(Scope.Benchmark)
public class ControllerBenchmarkTest {

    private static MockMvc mockMvc;
    private static String dataStr;

    @BeforeAll
    public static void setup() throws JsonProcessingException {
        String[] args = {"--logging.level.root=off", "--spring.main.banner-mode=off"};
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) ctx).build();

        // Подготовка данных с "string500" для тестирования
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add("string" + i);
        }
        data.add("string500"); // добавляем данные, которые вызовут условие
        Collections.shuffle(data);
        dataStr = new ObjectMapper().writeValueAsString(RequestData.builder().data(data).build());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkEqualIgnoreCaseNotOk() throws Exception {
        mockMvc.perform(post("/equalignorecase")
                        .content(dataStr)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    if (result.getResponse().getStatus() == 200) {
                        throw new AssertionError("Expected status to be not OK but was OK");
                    }
                });
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkEqualOnlyNotOk() throws Exception {
        mockMvc.perform(post("/equalonly")
                        .content(dataStr)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    if (result.getResponse().getStatus() == 200) {
                        throw new AssertionError("Expected status to be not OK but was OK");
                    }
                });
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ControllerBenchmarkTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(2)
                .build();

        new Runner(options).run();
    }
}
