package com.taskbuddy.api.business.user;

import com.taskbuddy.api.business.EmailSender;
import com.taskbuddy.api.business.user.dto.SignupCache;
import com.taskbuddy.api.business.user.dto.SignupSession;
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
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements SignupService, SigninService {
    private final UserJpaRepository userJpaRepository;
    private final EmailSender emailSender;
    private final CacheManager cacheManager;

    private static final Duration VERIFICATION_TIMEOUT = Duration.ofMinutes(5);
    private static final int LENGTH_OF_SESSION_KEY = 50;
    private static final int LENGTH_OF_VERIFICATION_CODE = 6;

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        /**
         * username으로 유저를 조회 (존재하지 않으면 실패)
         * password가 일치하는지 확인 (라이브러리 사용)
         */

        return Optional.empty();
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

        if (cacheManager.hasKey(CacheManager.Keys.SIGNUP_USED_EMAIL.generate(email))) {
            throw new DuplicateEmailException(ResultCodes.U1001);
        }
    }

    private void validateIfUsernameDoesNotAlreadyExist(String username) {
        boolean exists = userJpaRepository.existsByUsername(username);
        if (exists) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }

        if (cacheManager.hasKey(CacheManager.Keys.SIGNUP_USED_USERNAME.generate(username))) {
            throw new DuplicateEmailException(ResultCodes.U1002);
        }
    }

    @Override
    public SignupSession signup(UserSignupRequest request) {
        // 3. 이메일 중복검사
        validateIfEmailAndUsernameAreUnique(request.email(), request.username());

        final String sessionKey = RandomCodeGenerator.generateConsistingOfLettersAndNumbers(LENGTH_OF_SESSION_KEY);
        final int verificationCode = RandomCodeGenerator.generateConsistingOfOnlyNumbers(LENGTH_OF_VERIFICATION_CODE);

        sendEmail(request.email(), verificationCode);

        // 4. 캐시 저장 (이메일, 인증코드, 세션 Key)
        saveCacheData(request, sessionKey, verificationCode);

        return new SignupSession(sessionKey);
    }

    private void sendEmail(String email, int verificationCode) {
        final String title = "[task buddy] 회원가입 인증번호 안내";
        final String content =
                "안녕하세요. Task Buddy 입니다. \n" +
                        "아래 인증번호를 입력하여 이메일 인증 및 회원가입을 완료해주세요. \n\n" +
                        "인증번호 : " + verificationCode;

        emailSender.sendAsync(email, title, content);
    }

    private void saveCacheData(UserSignupRequest request, String sessionKey, int verificationCode) {
        final String verificationKey = CacheManager.Keys.SIGNUP_VERIFICATION.generate(sessionKey, request.email(), request.username());
        SignupCache verificationValue = new SignupCache(verificationCode, request);
        cacheManager.save(verificationKey, verificationValue, VERIFICATION_TIMEOUT);

        final String usedEmailKey = CacheManager.Keys.SIGNUP_USED_EMAIL.generate(request.email());
        cacheManager.save(usedEmailKey, Boolean.TRUE.toString(), VERIFICATION_TIMEOUT);

        final String usedUsernameKey = CacheManager.Keys.SIGNUP_USED_USERNAME.generate(request.username());
        cacheManager.save(usedUsernameKey, Boolean.TRUE.toString(), VERIFICATION_TIMEOUT);
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
