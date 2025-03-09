package com.taskbuddy.api.presentation.user.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserSignupRequestIsValidTest {
    private UserSignupRequest sut;

    private final String MOCK_EMAIL = "testuser12@email.com";
    private final String MOCK_USERNAME = "TestUser12";
    private final String MOCK_PASSWORD = "test123456!";

    /**
     * email
     */

    @NullAndEmptySource
    @ParameterizedTest
    void email이_null이거나_빈값이면_false를_반환한다(String givenEmail) {
        sut = new UserSignupRequest(givenEmail, MOCK_USERNAME, MOCK_PASSWORD);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {"@email.com", "testuser12", "testuser12@email", "testuser12@emailcom"})
    @ParameterizedTest
    void email형식이_아니라면_false를_반환한다(String givenEmail) {
        sut = new UserSignupRequest(givenEmail, MOCK_USERNAME, MOCK_PASSWORD);

        assertThat(sut.isValid()).isFalse();
    }

    @Test
    void email형식이라면_true를_반환한다() {
        String givenEmail = "testuser12@email.com";

        sut = new UserSignupRequest(givenEmail, MOCK_USERNAME, MOCK_PASSWORD);

        assertThat(sut.isValid()).isTrue();
    }

    /**
     * username
     */

    @NullAndEmptySource
    @ParameterizedTest
    void username이_null이거나_빈값이면_false를_반환한다(String givenUsername) {
        sut = new UserSignupRequest(MOCK_EMAIL, givenUsername, MOCK_PASSWORD);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {"1testuser", "1234user", "u1234", "us1234"})
    @ParameterizedTest
    void username이_영문으로_시작하면서_3글자이상이_아니라면_false를_반환한다(String givenUsername) {
        sut = new UserSignupRequest(MOCK_EMAIL, givenUsername, MOCK_PASSWORD);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {"test#user", ".", "⭐︎", "testuser&", "-"})
    @ParameterizedTest
    void username이_영문과_숫자외_문자가_포함되어있다면_false를_반환한다(String givenUsername) {
        sut = new UserSignupRequest(MOCK_EMAIL, givenUsername, MOCK_PASSWORD);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {"tes1", "t", "tes", "testuser1234567890123"})
    @ParameterizedTest
    void username이_5자_미만이거나_20자_초과라면_false를_반환한다(String givenUsername) {
        sut = new UserSignupRequest(MOCK_EMAIL, givenUsername, MOCK_PASSWORD);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {MOCK_USERNAME, "testuser123456789012", "abcde", "test_user"})
    @ParameterizedTest
    void username이_영문으로_시작하며_5자이상_20자이하의_길이라면_true를_반환한다(String givenUsername) {
        sut = new UserSignupRequest(MOCK_EMAIL, givenUsername, MOCK_PASSWORD);

        assertThat(sut.isValid()).isTrue();
    }

    /**
     * password
     */

    @NullAndEmptySource
    @ParameterizedTest
    void password가_null이거나_빈값이면_false를_반환한다(String givenPassword) {
        sut = new UserSignupRequest(MOCK_EMAIL, MOCK_USERNAME, givenPassword);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {"test", "test12", "young!@$", "1234!@#$", "!@#$"})
    @ParameterizedTest
    void password가_영문과_숫자_특수문자가_1글자이상_포함되지_않는다면_false를_반환한다(String givenPassword) {
        sut = new UserSignupRequest(MOCK_EMAIL, MOCK_USERNAME, givenPassword);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {"te1!", "t1!", "abcde12345abcde!@#$%12", "abcde1234567890fghik!@#$"})
    @ParameterizedTest
    void password가_5자_미만이거나_20자_초과라면_false를_반환한다(String givenPassword) {
        sut = new UserSignupRequest(MOCK_EMAIL, MOCK_USERNAME, givenPassword);

        assertThat(sut.isValid()).isFalse();
    }

    @ValueSource(strings = {MOCK_PASSWORD, "testuser_1234!@#", "bdfads!34-asdf#"})
    @ParameterizedTest
    void password가_영문_숫자_특수문자가_1글자이상_포함되어있으며_5자이상_20자이하의_길이라면_true를_반환한다(String givenPassword) {
        sut = new UserSignupRequest(MOCK_EMAIL, MOCK_USERNAME, givenPassword);

        assertThat(sut.isValid()).isTrue();
    }
}
