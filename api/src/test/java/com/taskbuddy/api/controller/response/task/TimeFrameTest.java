package com.taskbuddy.api.controller.response.task;

import com.taskbuddy.api.domain.TimeFrame;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TimeFrameTest {

    @Test
    void startDateTime이_null이면_예외를_던진다() {
        //given & when & then
        assertThatThrownBy(() -> new TimeFrame(null, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The startDateTime must not be null.");
    }

    @Test
    void endDateTime이_null이면_예외를_던진다() {
        //given & when & then
        assertThatThrownBy(() -> new TimeFrame(LocalDateTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The endDateTime must not be null.");
    }

    @Test
    void endDateTime이_startDateTime보다_과거일시라면_예외를_던진다() {
        //given
        LocalDateTime mockStartDateTime = LocalDateTime.now();
        LocalDateTime mockEndDateTime = mockStartDateTime.minusDays(1);

        //when & then
        assertThatThrownBy(() -> new TimeFrame(mockStartDateTime, mockEndDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The endDateTime must be after than the startDateTime.");
    }

    @Test
    void 정상적인_생성자_파라미터로_객체를_생성할_수_있다() {
        //given
        LocalDateTime mockStartDateTime = LocalDateTime.now();
        LocalDateTime mockEndDateTime = mockStartDateTime.plusHours(1);

        //when & then
        assertDoesNotThrow(() -> new TimeFrame(mockStartDateTime, mockEndDateTime));
    }
}
