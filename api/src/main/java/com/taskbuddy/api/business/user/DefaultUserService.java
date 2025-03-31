package com.taskbuddy.api.business.user;

import com.taskbuddy.api.business.EmailSender;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements SignupService {
    private static final Duration VERIFICATION_TIMEOUT = Duration.ofMinutes(5);
    private static final int LENGTH_OF_SESSION_KEY = 50;
    private static final int LENGTH_OF_VERIFICATION_CODE = 6;

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final CacheManager cacheManager;

    @Override
    public SignupSession signup(UserSignupRequest request) {
        validateIfEmailAndUsernameAreUnique(request.email(), request.username());

        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(LENGTH_OF_SESSION_KEY);
        final int verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(LENGTH_OF_VERIFICATION_CODE);

        sendEmail(request.email(), verificationCode);

        final String hashedPassword = passwordEncoder.encode(request.password());

        final SignupCache cacheValue = new SignupCache(verificationCode, request.email(), request.username(), hashedPassword);
        saveCacheData(sessionKey, cacheValue);

        return new SignupSession(sessionKey);
    }

    private void validateIfEmailAndUsernameAreUnique(String email, String username) {
        Assert.notNull(email, "email must not be null");
        Assert.notNull(username, "username must not be null");

        // unique 검증 구현
        validateIfEmailDoesNotAlreadyExist(email);
        validateIfUsernameDoesNotAlreadyExist(username);
    }

    private void validateIfEmailDoesNotAlreadyExist(String email) {
        boolean exists = userJpaRepository.existsByEmail(email);
        if (exists) {
            throw new DuplicateEmailException(ResultCodes.U1001);
        }

        if (cacheManager.existsByPattern(CacheManager.Keys.SIGNUP_VERIFICATION.pattern(new AbstractMap.SimpleEntry<>("EMAIL", email)))) {
            throw new DuplicateEmailException(ResultCodes.U1001);
        }
    }

    private void validateIfUsernameDoesNotAlreadyExist(String username) {
        boolean exists = userJpaRepository.existsByUsername(username);
        if (exists) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }

        if (cacheManager.existsByPattern(CacheManager.Keys.SIGNUP_VERIFICATION.pattern(new AbstractMap.SimpleEntry<>("USERNAME", username)))) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }
    }

    private void sendEmail(String email, int verificationCode) {
        final String title = "[task buddy] 회원가입 인증번호 안내";
        final String content =
                "안녕하세요. Task Buddy 입니다. \n" +
                        "아래 인증번호를 입력하여 이메일 인증 및 회원가입을 완료해주세요. \n\n" +
                        "인증번호 : " + verificationCode;

        emailSender.sendAsync(email, title, content);
    }

    private void saveCacheData(String sessionKey, SignupCache verificationValue) {
        // TODO #signup 캐시 동적데이터 매핑 로직 개선하기
        Map<String, String> argMap = new HashMap<>();
        argMap.put("SESSION", sessionKey);
        argMap.put("EMAIL", verificationValue.email());
        argMap.put("USERNAME", verificationValue.username());

        final String verificationKey = CacheManager.Keys.SIGNUP_VERIFICATION.generate(argMap);

        cacheManager.save(verificationKey, verificationValue, VERIFICATION_TIMEOUT);
    }

    @Override
    public User signupComplete(String sessionKey, String verificationCode) {
        /**
         * 회원가입 요청정보 조회
         */
        SignupCache signupCache = cacheManager.get(sessionKey, SignupCache.class)
                .orElseThrow(() -> new ApplicationException(ResultCodes.U1004));

        return null;
    }

    public User createAndSave(UserCreate userCreate) {
        validateIfEmailAndUsernameAreUnique(userCreate.email(), userCreate.username());
        final String encodedPassword = encodePassword(userCreate.password());

        final UserEntity entity = save(userCreate.email(), userCreate.username(), encodedPassword);

        return User.from(entity);
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
