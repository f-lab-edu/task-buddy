package com.taskbuddy.api.presentation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class})
public interface SpringTestContainer {

    interface RestDocsSnippetProvider {
        static ResponseFieldsSnippet errorResponseFields() {
            return responseFields(
                    fieldWithPath("code").type(JsonFieldType.STRING).description("실패 코드")
            );
        }
    }

}
