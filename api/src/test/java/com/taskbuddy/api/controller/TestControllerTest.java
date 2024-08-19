package com.taskbuddy.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.controller.request.TaskCreateRequest;
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

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
public class TestControllerTest {
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

    //TODO status Enumeration 문서작성하는 법
    //TODO 실패 테스트케이스
    @Test
    void 사용자는_Task_정보를_조회할_수_있다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/tasks/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ResultStatus.SUCCESS.name()))
                .andDo(print())
                .andDo(document("get-task-with-id",
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
                                        fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)"))
                ));
    }

    @Test
    void name() throws JsonProcessingException {
        TimeFrame timeFrame = new TimeFrame(
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 1, 23, 59, 59));
        String value = objectMapper.writeValueAsString(LocalDateTime.of(2024, 8, 1, 0, 0, 0));
        System.out.println("value = " + value);
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(ResultStatus.SUCCESS.name()))
                .andDo(print())
                .andDo(document("create-a-task",
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("응답 헤더")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("The status of teh response, e.g."))));
    }
}
