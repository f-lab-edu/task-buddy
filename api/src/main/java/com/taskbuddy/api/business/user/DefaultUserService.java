package com.taskbuddy.api.business.user;

import com.taskbuddy.api.business.user.dto.SignupCache;
import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.error.exception.DuplicateEmailException;
import com.taskbuddy.api.persistence.cache.CacheManager;
import com.taskbuddy.api.persistence.repository.UserJpaRepository;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.presentation.user.request.UserSignupRequest;
import com.taskbuddy.api.utils.RandomCodeGenerator;
import com.taskbuddy.persistence.entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements SignupService, SigninService {
    private final UserJpaRepository userJpaRepository;
    private final JavaMailSender javaMailSender;
    private final CacheManager cacheManager;

    private static final int LENGTH_OF_SESSION_KEY = 50;
    private static final int LENGTH_OF_VERIFICATION_CODE = 6;

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        /**
         * username으로 유저를 조회 (존재하지 않으면 실패)
         * password가 일치하는지 확인 (라이브러리 사용)
         */

        return Optional.empty();
    }

    @Override
    public SignupSession signup(UserSignupRequest request) {
        // 3. 이메일 중복검사
        boolean existedEmail = userJpaRepository.existsByEmail(request.email());
        if (existedEmail) {
            throw new DuplicateEmailException(ResultCodes.U1001);
        }

        boolean existedUsername = userJpaRepository.existsByUsername(request.username());
        if (existedUsername) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }

        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(LENGTH_OF_SESSION_KEY);
        final int verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(LENGTH_OF_VERIFICATION_CODE);

        sendEmail(request.email(), verificationCode);

        // 4. 캐시 저장 (이메일, 인증코드, 세션 Key)
        final String cacheKey = CacheManager.Keys.SIGNUP_VERIFICATION.generate(sessionKey);
        SignupCache cache = new SignupCache(verificationCode, request);
        cacheManager.save(cacheKey, cache, Duration.ofMinutes(5));

        return new SignupSession(sessionKey);
    }

    // TODO #signup email sender 클래스 추출
    private void sendEmail(String email, int verificationCode) {
        final String fixedTitle = "[task buddy] 회원가입 인증번호 안내";
        final String content =
                "안녕하세요. Task Buddy 입니다. \n" +
                "아래 인증번호를 입력하여 이메일 인증 및 회원가입을 완료해주세요. \n\n" +
                "인증번호 : " + verificationCode;

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(fixedTitle);
            helper.setText(content);
        } catch (MessagingException e) {
            throw new ApplicationException(ResultCodes.U1003);
        }
    }

    public User createAndSave(UserCreate userCreate) {
        validateIfEmailAndUsernameAreUnique(userCreate.email(), userCreate.username());
        final String encodedPassword = encodePassword(userCreate.password());

        final UserEntity entity = save(userCreate.email(), userCreate.username(), encodedPassword);

        return User.from(entity);
    }

    private void validateIfEmailAndUsernameAreUnique(String email, String username) {
        Assert.notNull(email, "email must not be null");
        Assert.notNull(username, "username must not be null");

        // unique 검증 구현
    }

    private String encodePassword(String password) {
        // 단방향 암호화 (Server -> DB)

        return password;
    }

    private UserEntity save(String email, String username, String password) {
        // 필드 DB 길이 검증
        final LocalDateTime createDateTime = LocalDateTime.now();

        UserEntity entity = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .passwordUpdatedAt(createDateTime)
                .createdAt(createDateTime)
                .updatedAt(createDateTime)
                .build();

//        return userJpaRepository.save(entity);
        return entity;
    }
}
