package com.taskbuddy.api.controller.response;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiResponseTest {

    @Test
    void success로_성공상태의_객체를_생성할_수_있다() {
        //given
        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "Hello");

        //when
        ApiResponse<Map<String, String>> result = ApiResponse.success(body);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ResultStatus.SUCCESS);
        assertThat(result.getData()).isEqualTo(body);
        assertThat(result.getError()).isNull();
    }

    @Test
    void success로_data가_Null이면서_성공상태의_객체를_생성할_수_있다() {
        //given
        //when
        ApiResponse<Object> result = ApiResponse.success(null);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ResultStatus.SUCCESS);
        assertThat(result.getData()).isNull();
        assertThat(result.getError()).isNull();
    }

    @Test
    void fail로_실패상태의_객체를_생성할_수_있다() {
        //given
        ErrorDetails givenErrorDetails = new ErrorDetails("NOT_FOUND_USER", "유저를 찾을 수 없습니다.");

        //when
        ApiResponse<?> result = ApiResponse.fail(givenErrorDetails);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ResultStatus.FAIL);
        assertThat(result.getData()).isNull();
        assertThat(result.getError()).isNotNull();
        assertThat(result.getError()).isEqualTo(givenErrorDetails);
    }

    @Test
    void error가_null이면_실패상태의_객체를_생성할_수_없다() {
        //given & when & then
        assertThatThrownBy(() -> ApiResponse.fail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The error argument must not be null.");
    }
}
