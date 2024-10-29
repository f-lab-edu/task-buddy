package com.taskbuddy.api.controller.request;

import com.taskbuddy.api.domain.TimeFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TaskContentUpdateRequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    void Title이_Null이거나_비어있는_값이면_예외를_던진다(String emptyTitle) {
        //given
        TimeFrame dummyTimeFrame = new TimeFrame(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        //when & then
        assertThatThrownBy(() -> new TaskContentUpdateRequest(emptyTitle, null, dummyTimeFrame))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The title of task must not be blank.");
    }

    @Test
    void description길이가_500자_초과라면_예외를_던진다() {
        //given
        String longDescription = "A".repeat(501);
        TimeFrame dummyTimeFrame = new TimeFrame(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        //when & then
        assertThatThrownBy(() -> new TaskContentUpdateRequest("sample title", longDescription, dummyTimeFrame))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The description length must be equal or less than 500");
    }

    @Test
    void description길이가_500자_이하라면_정상적으로_객체가_생성된다() {
        //given
        String longDescription = "A".repeat(500);
        TimeFrame dummyTimeFrame = new TimeFrame(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        //when & then
        assertDoesNotThrow(() -> new TaskContentUpdateRequest("sample title", longDescription, dummyTimeFrame));
    }

    @Test
    void timeFrame이_null이라면_예외를_던진다() {
        //given & when & then
        assertThatThrownBy(() -> new TaskContentUpdateRequest("sample title", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The timeFrame must not be null.");
    }

    @Test
    void 정상적인_생성자_파라미터로_객체를_생성할_수_있다() {
        //given
        TimeFrame mockTimeFrame = new TimeFrame(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        //when & then
        assertDoesNotThrow(() -> new TaskContentUpdateRequest("sample title", null, mockTimeFrame));
    }
}
