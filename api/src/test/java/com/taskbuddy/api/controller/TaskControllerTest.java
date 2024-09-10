package com.taskbuddy.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.request.TaskContentUpdateRequest;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO Task ContentUpdate 내용으로 수정 / Task isDone API 반영


//TODO (#12) 태그로 테스트 분리
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
public class TaskControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .alwaysDo(document("v1/tasks/"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper = objectMapper;
    }

    @Test
    void 사용자는_Task_정보를_조회할_수_있다() throws Exception {
        mockMvc.perform(get("/v1/tasks/{id}", 1L)
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
        mockMvc.perform(get("/v1/tasks/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The given task with id does not exist."))
                .andDo(document("get-a-task-with-id/fail/does-not-exist"));
    }

    @Test
    void 조회할_Task_ID가_음수값이면_실패응답을_받는다() throws Exception {
        mockMvc.perform(get("/v1/tasks/{id}", -1)
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

        mockMvc.perform(post("/v1/tasks")
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
    void 등록데이터_조건을_만족하지_않는다면_실패응답을_받는다() throws Exception {
        String emptyTitle = "";
        String json = "{\n" +
                "  \"title\":\" " + emptyTitle + " \",\n" +
                "  \"description\":\"백준1902..\",\n" +
                "  \"timeFrame\":{\n" +
                "    \"startDateTime\":\"2024-08-01T00:00:00\",\n" +
                "    \"endDateTime\":\"2024-08-01T23:59:59\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/v1/tasks")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The title of task must not be blank."))
                .andDo(document("create-a-task/fail/invalid-request-data"))
        ;
    }

    @Test
    void 사용자는_Task내용을_수정할_수_있다() throws Exception {
        TaskContentUpdateRequest request = new TaskContentUpdateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

        mockMvc.perform(patch("/v1/tasks/{id}/content", 1)
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
    void 수정할_Task_ID가_음수값이면_실패응답을_받는다() throws Exception {
        TaskContentUpdateRequest request = new TaskContentUpdateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

        mockMvc.perform(patch("/v1/tasks/{id}/content", -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The id value must be positive."))
                .andDo(document("update-a-task/fail/negative-id-value"));
    }

    @Test
    void 수정데이터_조건을_만족하지_않는다면_실패응답을_받는다() throws Exception {
        String emptyTitle = "";
        String json = "{\n" +
                "  \"title\":\" " + emptyTitle + " \",\n" +
                "  \"description\":\"백준1902..\",\n" +
                "  \"timeFrame\":{\n" +
                "    \"startDateTime\":\"2024-08-01T00:00:00\",\n" +
                "    \"endDateTime\":\"2024-08-01T23:59:59\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(patch("/v1/tasks/{id}/content", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The title of task must not be blank."))
                .andDo(document("update-a-task/fail/invalid-request-data"));
    }

    @Test
    void 수정할_Task가_존재하지_않는다면_실패응답을_받는다() throws Exception {
        TaskContentUpdateRequest request = new TaskContentUpdateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

        mockMvc.perform(patch("/v1/tasks/{id}/content", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The given task with id does not exist."))
                .andDo(document("update-a-task/fail/does-not-exist"));
    }

    @Test
    void 사용자는_Task를_삭제할_수_있다() throws Exception {
        mockMvc.perform(delete("/v1/tasks/{id}", 1)
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

    @Test
    void 삭제할_Task가_존재하지_않는다면_실패응답을_받는다() throws Exception {
        mockMvc.perform(delete("/v1/tasks/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The given task with id does not exist."))
                .andDo(document("remove-a-task/fail/does-not-exist"));
    }

    @Test
    void 삭제할_Task_ID가_음수값이면_실패응답을_받는다() throws Exception {
        mockMvc.perform(delete("/v1/tasks/{id}", -1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ResultStatus.FAIL.name()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_STATE"))
                .andExpect(jsonPath("$.error.message").value("The id value must be positive."))
                .andDo(document("remove-a-task/fail/negative-id-value"));
    }
}
