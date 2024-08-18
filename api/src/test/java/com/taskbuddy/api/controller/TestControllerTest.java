package com.taskbuddy.api.controller;

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

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
public class TestControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("v1/tasks/"))
                .build();
    }

    //TODO status Enumeration 문서작성하는 법
    @Test
    void Task_정보를_조회할_수_있다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/tasks/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-task-with-id",
                        pathParameters(
                                parameterWithName("id").description("task id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("응답 헤더")
                        ),
                        responseFields(fieldWithPath("status").type(JsonFieldType.STRING).description("The status of teh response, e.g."))
                                .andWithPrefix("data.",
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("task id"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("추가 설명"),
                                        fieldWithPath("isDone").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                                        fieldWithPath("timeFrame").type(JsonFieldType.OBJECT).description("수행기간"),
                                        fieldWithPath("timeFrame.startDateTime").type(JsonFieldType.STRING).description("시작일시 (yyyy-MM-dd)"),
                                        fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)"))
                ))
        ;
    }
}
