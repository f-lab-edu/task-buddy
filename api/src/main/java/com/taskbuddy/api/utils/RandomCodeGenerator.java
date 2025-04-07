package com.taskbuddy.api.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RandomCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 지정된 길이의 숫자로만 구성된 랜덤 코드를 생성한다.
     * <p>
     * 만약 생성된 숫자의 자릿수가 지정된 길이보다 작을 경우, 왼쪽에 0을 채워 길이를 맞춘다.
     *
     * @param length 생성할 코드의 길이 (1 이상이어야 함)
     * @return 길이에 맞춰 0으로 채워진 숫자 문자열
     * @throws AssertionError length가 1 이하일 경우
     */
    public static String generateConsistingOfOnlyNumbers(int length) {
        assert length > 0 : "길이는 1 이상이어야 한다.";

        int multiply = 9;
        for (int i = 0; i < length-1; i++) {
            multiply *= 10;
        }

        int randomCode = (int) (Math.random() * multiply) + 100_000;
        return padLeftZeros(String.valueOf(randomCode), length);
    }

    /**
     * 주어진 문자열의 길이가 부족할 경우, 왼쪽을 0으로 채워서 지정된 길이를 맞춘 문자열을 반환한다.
     *
     * @param target 원본 문자열
     * @param length 최종 문자열의 길이
     * @return target의 길이가 length보다 작으면 왼쪽에 0을 채워 반환하며, 그렇지 않으면 원본 문자열 반환
     */
    private static String padLeftZeros(String target, int length) {
        if (target.length() >= length) {
            return target;
        }

        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - target.length()) {
            sb.append('0');
        }
        sb.append(target);

        return sb.toString();
    }

    /**
     * 지정된 길이의 영문자, 숫자, 특수문자가 조합된 랜덤 코드를 생성한다.
     * <p>
     * 코드에 포함될 문자는 미리 정의된 문자열(대소문자 영문자, 숫자, 특수문자)에서 무작위로 선택된다.
     *
     * @param length 생성할 코드의 길이 (1 이상이어야 함)
     * @return 랜덤하게 선택된 문자들로 구성된 문자열
     * @throws AssertionError length가 1 이하일 경우
     */
    public static String generateConsistingOfLettersAndNumbers(int length) {
        assert length > 0 : "길이는 1 이상이어야 한다.";

        return IntStream.range(0, length)
                .map(i -> CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
