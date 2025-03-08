package com.taskbuddy.api.business.user.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SignupSessionTest {

    @Test
    void key를_6자리초과하여_생성하면_AssertionError가_발생한다() {
        Assertions.assertThatThrownBy(() -> new SignupSession("1234567"))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void key에_문자를_포함하여_생성하면_AssertionError가_발생한다() {
        Assertions.assertThatThrownBy(() -> new SignupSession("1234AB"))
                .isInstanceOf(AssertionError.class);
    }
}
