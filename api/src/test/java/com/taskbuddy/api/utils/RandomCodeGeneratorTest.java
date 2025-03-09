package com.taskbuddy.api.utils;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RandomCodeGeneratorTest {

    @RepeatedTest(10)
    void 숫자로만_이루어진_랜덤코드를_생성할_수_있다() {
        int length = 6;
        int code = RandomCodeGenerator.generateConsistingOfOnlyNumbers(length);

        String codeString = String.valueOf(code);
        assertEquals(length, codeString.length(), "생성된 코드의 길이가 전달된 길이와 같아야 한다.");
        assertTrue(codeString.matches("\\d+"), "숫자로만 이루어진 코드로 생성되어야 한다.");
    }

    @RepeatedTest(10)
    void 문자와_숫자_특수문자가_포함된_랜덤코드를_생성할_수_있다() {
        int length = 8;
        String code = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(length);

        assertEquals(length, code.length(), "생성된 코드의 길이가 전달된 길이와 같아야 한다.");
        assertTrue(Pattern.matches("[A-Za-z0-9!@#$%^&*()]+", code), "예상한 문자 범위 밖의 문자를 포함할 수 없다.");
    }

    @Test
    void 길이가_0_또는_음수로_랜덤코드_생성시_예외가_발생한다() {
        assertThrows(AssertionError.class, () -> RandomCodeGenerator.generateConsistingOfOnlyNumbers(0), "길이가 0일 때 예외가 발생해야 합니다.");
        assertThrows(AssertionError.class, () -> RandomCodeGenerator.generateConsistingOfOnlyNumbers(-1), "음수 길이일 때 예외가 발생해야 합니다.");
        assertThrows(AssertionError.class, () -> RandomCodeGenerator.generateConsistingOfLettersAndNumbers(0), "길이가 0일 때 예외가 발생해야 합니다.");
        assertThrows(AssertionError.class, () -> RandomCodeGenerator.generateConsistingOfLettersAndNumbers(-1), "음수 길이일 때 예외가 발생해야 합니다.");
    }
}
