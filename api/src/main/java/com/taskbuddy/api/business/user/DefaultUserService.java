package com.taskbuddy.api.business.user;

import com.taskbuddy.api.business.EmailSender;
import com.taskbuddy.api.business.user.dto.SignupCache;
import com.taskbuddy.api.business.user.dto.SignupSession;
import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.error.exception.DuplicateEmailException;
import com.taskbuddy.api.persistence.cache.CacheKeys;
import com.taskbuddy.api.persistence.cache.CacheManager;
import com.taskbuddy.api.persistence.repository.UserJpaRepository;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.presentation.user.request.UserSignupVerifyRequest;
import com.taskbuddy.api.utils.RandomCodeGenerator;
import com.taskbuddy.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
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
    public SignupSession signupVerify(UserSignupVerifyRequest request) {
        validateIfEmailAndUsernameAreUnique(request.email(), request.username());

        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(LENGTH_OF_SESSION_KEY);
        final String verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(LENGTH_OF_VERIFICATION_CODE);

        sendEmail(request.email(), verificationCode);

        final String hashedPassword = passwordEncoder.encode(request.password());

        final SignupCache cacheValue = new SignupCache(verificationCode, request.email(), request.username(), hashedPassword);
        saveCacheData(sessionKey, cacheValue);

        return new SignupSession(sessionKey);
    }

    private void sendEmail(String email, String verificationCode) {
        final String title = "[task buddy] 회원가입 인증번호 안내";
        final String content =
                "안녕하세요. Task Buddy 입니다. \n" +
                        "아래 인증번호를 입력하여 이메일 인증 및 회원가입을 완료해주세요. \n\n" +
                        "인증번호 : " + verificationCode;

        emailSender.sendAsync(email, title, content);
    }

    @Override
    public User signup(String sessionKey, String verificationCode) {
        SignupCache signupCache = cacheManager.get(CacheKeys.SIGNUP_VERIFICATION.pattern(Map.of("SESSION", sessionKey)), SignupCache.class)
                .orElseThrow(() -> new ApplicationException(ResultCodes.U1004));

        validateVerificationCode(signupCache, verificationCode);

        final UserEntity savedEntity = saveUser(signupCache.email(), signupCache.username(), signupCache.password());

        invalidateCache(sessionKey, signupCache);

        return User.from(savedEntity);
    }

    private void validateVerificationCode(SignupCache signupCache, String requestVerificationCode) {
        if (!signupCache.verificationCode().equals(requestVerificationCode)) {
            throw new ApplicationException(ResultCodes.U1005);
        }
    }

    private UserEntity saveUser(String email, String username, String password) {
        final LocalDateTime createDateTime = LocalDateTime.now();

        UserEntity entity = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .passwordUpdatedAt(createDateTime)
                .createdAt(createDateTime)
                .updatedAt(createDateTime)
                .build();

        return userJpaRepository.save(entity);
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

        if (cacheManager.existsByPattern(CacheKeys.SIGNUP_VERIFICATION.pattern(Map.of("EMAIL", email)))) {
            throw new DuplicateEmailException(ResultCodes.U1001);
        }
    }

    private void validateIfUsernameDoesNotAlreadyExist(String username) {
        boolean exists = userJpaRepository.existsByUsername(username);
        if (exists) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }

        if (cacheManager.existsByPattern(CacheKeys.SIGNUP_VERIFICATION.pattern(Map.of("USERNAME", username)))) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }
    }

    private void saveCacheData(String sessionKey, SignupCache signupCache) {
        final String cacheKey = generateCacheKey(sessionKey, signupCache);

        cacheManager.save(cacheKey, signupCache, VERIFICATION_TIMEOUT);
    }

    private void invalidateCache(String sessionKey, SignupCache signupCache) {
        final String cacheKey = generateCacheKey(sessionKey, signupCache);

        cacheManager.delete(cacheKey);
    }

    private String generateCacheKey(String sessionKey, SignupCache signupCache) {
        // TODO #signup 캐시 동적데이터 매핑 로직 개선하기
        Map<String, String> argMap = new HashMap<>();
        argMap.put("SESSION", sessionKey);
        argMap.put("EMAIL", signupCache.email());
        argMap.put("USERNAME", signupCache.username());

        return CacheKeys.SIGNUP_VERIFICATION.generate(argMap);
    }
}
