package com.example.demo.util;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class PasswordEncoderTest {


    // 테스트 객체 설정
    private static final String rawPassword = "123qwe!@#";
    private static final String wrongPassword = "321ewq#@!";

    @Test
    @DisplayName("같은 비밀번호를 입력하여 암호화가 정상 동작하면 true")
    void PasswordMatch() {

        // Given: 원본 비밀번호
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // When: 동일한 비밀번호로 암호화된 비밀번호를 검증
        boolean isMatch = PasswordEncoder.matches(rawPassword, encodedPassword);

        // Then: 비밀번호가 일치함
        assertTrue(isMatch, "비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않을 때 false 반환")
    void PasswordNotMatch() {

        // Given: 원본 비밀번호와 암호화될 비밀번호를 입력
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // When: 입력한 비밀번호와 암호화된 비밀번호를 비교
        boolean isMatch = PasswordEncoder.matches(wrongPassword, encodedPassword);

        // Then: 비밀번호가 일치하지 않으면 메세지 출력
        assertFalse(isMatch, "기존 비밀번호와 인코드한 비밀번호가 다릅니다.");
    }
}