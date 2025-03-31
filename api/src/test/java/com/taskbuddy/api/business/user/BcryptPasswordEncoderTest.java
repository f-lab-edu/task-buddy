package com.taskbuddy.api.business.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BcryptPasswordEncoderTest {
    private final BcryptPasswordEncoder sut = new BcryptPasswordEncoder();

    @Test
    void encode_비밀번호를_Bcrypt_해싱처리하여_암호화할_수_있다() {
        String plainPassword = "mySecret123";

        String result = sut.encode(plainPassword);

        assertThat(result).isNotNull();
        assertThat(result).isNotEqualTo(plainPassword);
    }

    @Test
    void matches_평문_비밀번호가_해싱된_비밀번호와_일치하지_않는다면_false를_반환한다() {
        String plainPassword = "mySecret123";
        String hashedPassword = sut.encode("wrongPassword123");

        assertThat(sut.matches(plainPassword, hashedPassword)).isFalse();
    }

    @Test
    void matches_평문_비밀번호가_해싱된_비밀번호와_일치한다면_true를_반환한다() {
        String plainPassword = "mySecret123";
        String hashedPassword = sut.encode(plainPassword);

        assertThat(sut.matches(plainPassword, hashedPassword)).isTrue();
    }
}
