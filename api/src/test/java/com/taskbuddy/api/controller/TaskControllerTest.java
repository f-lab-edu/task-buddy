package com.taskbuddy.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.request.TaskUpdateRequest;
import com.taskbuddy.api.controller.response.ResultStatus;
import com.taskbuddy.api.controller.response.task.TimeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO IntegrationTest와 UnitTest가 모였을 때 Fast 태그로 나누자
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
public class TaskControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("v1/tasks/"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper = objectMapper;
    }

    @Test
    void 사용자는_Task_정보를_조회할_수_있다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/tasks/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResultStatus.SUCCESS.name()))
                .andExpect(jsonPath("$.data.id", allOf(notNullValue(), greaterThan(0))))
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.isDone").exists())
                .andExpect(jsonPath("$.data.timeFrame").exists())
                .andDo(document("get-a-task-with-id/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        pathParameters(
                                parameterWithName("id").description("task id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("응답 헤더")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("The status of teh response, e.g."))
                                .andWithPrefix("data.",
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("task id"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("추가 설명"),
                                        fieldWithPath("isDone").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                                        fieldWithPath("timeFrame").type(JsonFieldType.OBJECT).description("수행기간"),
                                        fieldWithPath("timeFrame.startDateTime").type(JsonFieldType.STRING).description("시작일시 (yyyy-MM-dd)"),
                                        fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)"))));
    }

    @Test
    void 조회할_Task가_존재하지_않는다면_실패응답을_받는다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/tasks/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("Task를 찾을 수 없습니다."))
                .andDo(document("get-a-task-with-id/fail/does-not-exist"));
    }

    @Test
    void ID가_음수값이면_실패응답을_받는다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/tasks/{id}", -1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The id value must be positive."))
                .andDo(document("get-a-task-with-id/fail/negative-id-value"));
    }

    @Test
    void 사용자는_Task를_생성할_수_있다() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 1, 23, 59, 59)));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(ResultStatus.SUCCESS.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("create-a-task/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("timeFrame").type(JsonFieldType.OBJECT).description("수행기간"),
                                fieldWithPath("timeFrame.startDateTime").type(JsonFieldType.STRING).description("시작일시 (yyyy-MM-dd)"),
                                fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("response body content type"),
                                headerWithName(HttpHeaders.LOCATION).description("생성된 Task 조회 URL")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"))));
    }

    @Test
    void 사용자는_Task내용을_수정할_수_있다() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/v1/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResultStatus.SUCCESS.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andDo(document("update-a-task/success",
                        pathParameters(
                                parameterWithName("id").description("task id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("timeFrame").type(JsonFieldType.OBJECT).description("수행기간"),
                                fieldWithPath("timeFrame.startDateTime").type(JsonFieldType.STRING).description("시작일시 (yyyy-MM-dd)"),
                                fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("response body content type")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"))));
    }

    @Test
    void 사용자는_Task를_삭제할_수_있다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/tasks/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResultStatus.SUCCESS.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andDo(document("remove-a-task/success",
                        pathParameters(
                                parameterWithName("id").description("task id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("response body content type")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"))));
    }
}
