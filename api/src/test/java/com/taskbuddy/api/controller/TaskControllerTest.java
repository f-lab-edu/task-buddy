package com.taskbuddy.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskbuddy.api.controller.request.TaskContentUpdateRequest;
import com.taskbuddy.api.controller.request.TaskCreateRequest;
import com.taskbuddy.api.controller.response.task.TimeFrame;
import com.taskbuddy.api.error.code.ErrorCodes;
import com.taskbuddy.core.database.repository.TaskReminderRepository;
import com.taskbuddy.core.domain.Task;
import com.taskbuddy.core.domain.TaskReminder;
import com.taskbuddy.core.service.port.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

//TODO (#12) 태그로 테스트 분리
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class})
public class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    private ObjectMapper objectMapper;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private TaskReminderRepository taskReminderRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper = objectMapper;
    }

    @Test
    void 사용자는_Task_정보를_조회할_수_있다() {
        final long taskId = 1L;
        Task mockTask = Task.builder()
                .id(taskId)
                .title("알고리즘 풀기")
                .isDone(false)
                .description("백준1902")
                .timeFrame(new com.taskbuddy.core.domain.TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));

        webTestClient.get()
                .uri("/v1/tasks/{id}", taskId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").value(allOf(notNullValue(), greaterThan(0)))
                .jsonPath("$.title").exists()
                .jsonPath("$.isDone").exists()
                .jsonPath("$.timeFrame").exists()
                .consumeWith(document("v1/tasks/get-a-task-with-id/success",
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
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("task id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("추가 설명"),
                                fieldWithPath("isDone").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                                fieldWithPath("timeFrame").type(JsonFieldType.OBJECT).description("수행기간"),
                                fieldWithPath("timeFrame.startDateTime").type(JsonFieldType.STRING).description("시작일시 (yyyy-MM-dd)"),
                                fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)"))));
    }

    @Test
    void 조회할_Task가_존재하지_않는다면_실패응답을_받는다() {
        webTestClient.get()
                .uri("/v1/tasks/{id}", 0)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/get-a-task-with-id/fail/does-not-exist"));
    }

    @Test
    void 조회할_Task_ID가_음수값이면_실패응답을_받는다() {
        webTestClient.get()
                .uri("/v1/tasks/{id}", -1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/get-a-task-with-id/fail/negative-id-value"));
    }

    @Test
    void 사용자는_Task를_생성할_수_있다() throws Exception {
        String givenTitle = "알고리즘 문제 풀기";
        String givenDescription = "백준1902..";
        LocalDateTime givenStartDateTime = LocalDateTime.of(2024, 8, 1, 0, 0, 0);
        LocalDateTime givenEndDateTime = LocalDateTime.of(2024, 8, 1, 23, 59, 59);

        Task mockTask = Task.builder()
                .id(1L)
                .title(givenTitle)
                .isDone(false)
                .description(givenDescription)
                .timeFrame(new com.taskbuddy.core.domain.TimeFrame(givenStartDateTime, givenEndDateTime))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(mockTask);

        TaskCreateRequest request = new TaskCreateRequest(
                givenTitle,
                givenDescription,
                new TimeFrame(givenStartDateTime, givenEndDateTime));

        webTestClient.post()
                .uri("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("v1/tasks/create-a-task/success",
                        requestHeaders(
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
                                headerWithName(HttpHeaders.LOCATION).description("생성된 Task 조회 URL")
                        )));
    }

    @Test
    void 등록데이터_조건을_만족하지_않는다면_실패응답을_받는다() {
        String emptyTitle = "";
        String json = "{\n" +
                "  \"title\":\" " + emptyTitle + " \",\n" +
                "  \"description\":\"백준1902..\",\n" +
                "  \"timeFrame\":{\n" +
                "    \"startDateTime\":\"2024-08-01T00:00:00\",\n" +
                "    \"endDateTime\":\"2024-08-01T23:59:59\"\n" +
                "  }\n" +
                "}";

        webTestClient.post()
                .uri("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/create-a-task/fail/invalid-request-data"));
    }

    @Test
    void 사용자는_Task내용을_수정할_수_있다() throws Exception {
        String givenTitle = "알고리즘 문제 풀기";
        String givenDescription = "백준1902..";
        LocalDateTime givenStartDateTime = LocalDateTime.of(2024, 8, 1, 0, 0, 0);
        LocalDateTime givenEndDateTime = LocalDateTime.of(2024, 8, 1, 23, 59, 59);

        long givenId = 1L;
        Task mockTask = Task.builder()
                .id(givenId)
                .title(givenTitle)
                .isDone(false)
                .description(givenDescription)
                .timeFrame(new com.taskbuddy.core.domain.TimeFrame(givenStartDateTime, givenEndDateTime))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(taskRepository.findById(givenId)).thenReturn(Optional.of(mockTask));
        Mockito.doNothing().when(taskReminderRepository).save(Mockito.any(TaskReminder.class));

        TaskContentUpdateRequest request = new TaskContentUpdateRequest(
                "update title",
                "update description",
                new TimeFrame(givenStartDateTime.plusDays(3), givenEndDateTime.plusDays(3)));

        webTestClient.patch()
                .uri("/v1/tasks/{id}/content", givenId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("v1/tasks/update-a-task-content/success",
                        pathParameters(
                                parameterWithName("id").description("task id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("timeFrame").type(JsonFieldType.OBJECT).description("수행기간"),
                                fieldWithPath("timeFrame.startDateTime").type(JsonFieldType.STRING).description("시작일시 (yyyy-MM-dd)"),
                                fieldWithPath("timeFrame.endDateTime").type(JsonFieldType.STRING).description("종료일시 (yyyy-MM-dd)")
                        )));
    }

    @Test
    void 수정할_Task_ID가_음수값이면_실패응답을_받는다() throws Exception {
        TaskContentUpdateRequest request = new TaskContentUpdateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

        webTestClient.patch()
                .uri("/v1/tasks/{id}/content", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/update-a-task-content/fail/negative-id-value"));
    }

    @Test
    void 수정데이터_조건을_만족하지_않는다면_실패응답을_받는다() {
        String emptyTitle = "";
        String json = "{\n" +
                "  \"title\":\" " + emptyTitle + " \",\n" +
                "  \"description\":\"백준1902..\",\n" +
                "  \"timeFrame\":{\n" +
                "    \"startDateTime\":\"2024-08-01T00:00:00\",\n" +
                "    \"endDateTime\":\"2024-08-01T23:59:59\"\n" +
                "  }\n" +
                "}";

        webTestClient.patch()
                .uri("/v1/tasks/{id}/content", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/update-a-task-content/fail/invalid-request-data"));
    }

    @Test
    void 수정할_Task가_존재하지_않는다면_실패응답을_받는다() throws Exception {
        TaskContentUpdateRequest request = new TaskContentUpdateRequest(
                "알고리즘 문제 풀기",
                "백준1902..",
                new TimeFrame(
                        LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)));

        webTestClient.patch()
                .uri("/v1/tasks/{id}/content", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/update-a-task-content/fail/does-not-exist"));
    }

    @Test
    void 완료여부를_업데이트할_Task가_존재하지_않는다면_실패응답을_받는다() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("isDone", "true");

        webTestClient.patch()
                .uri("/v1/tasks/{id}/is-done", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(body))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/update-a-task-done/fail/does-not-exist"));
    }

    @Test
    void 사용자는_Task의_완료여부를_업데이트할_수_있다() throws Exception {
        long givenTaskId = 1L;
        Task task = Task.builder()
                .id(givenTaskId)
                .title("알고리즘 풀기")
                .isDone(false)
                .description("백준1902")
                .timeFrame(new com.taskbuddy.core.domain.TimeFrame(
                        LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 8, 31, 23, 59, 59)
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(taskRepository.findById(givenTaskId)).thenReturn(Optional.of(task));

        Map<String, Boolean> body = new HashMap<>();
        body.put("isDone", true);

        webTestClient.patch()
                .uri("/v1/tasks/{id}/is-done", givenTaskId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(body))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("v1/tasks/update-a-task-done/success",
                        pathParameters(
                                parameterWithName("id").description("task id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("isDone").type(JsonFieldType.BOOLEAN).description("완료 여부")
                        )));
    }

    @Test
    void 사용자는_Task를_삭제할_수_있다() {
        long givenTaskId = 1L;
        Mockito.when(taskRepository.existsById(givenTaskId)).thenReturn(true);

        webTestClient.delete()
                .uri("/v1/tasks/{id}", givenTaskId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("v1/tasks/remove-a-task/success",
                        pathParameters(
                                parameterWithName("id").description("task id")
                        )));
    }

    @Test
    void 삭제할_Task가_존재하지_않는다면_실패응답을_받는다() {
        long givenTaskId = 1L;
        Mockito.when(taskRepository.existsById(givenTaskId)).thenReturn(false);

        webTestClient.delete()
                .uri("/v1/tasks/{id}", givenTaskId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/remove-a-task/fail/does-not-exist"));
    }

    @Test
    void 삭제할_Task_ID가_음수값이면_실패응답을_받는다() {
        webTestClient.delete()
                .uri("/v1/tasks/{id}", -1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.code").isEqualTo(ErrorCodes.INVALID_PARAMETER_STATE.code())
                .consumeWith(document("v1/tasks/remove-a-task/fail/negative-id-value"));
    }
}
