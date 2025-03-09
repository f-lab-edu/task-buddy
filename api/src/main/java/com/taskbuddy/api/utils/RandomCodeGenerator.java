package com.taskbuddy.api.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RandomCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static int generateConsistingOfOnlyNumbers(int length) {
        int multiply = 9;
        for (int i = 0; i < length-1; i++) {
            multiply *= 10;
        }

        return (int) (Math.random() * multiply) + 100_000;
    }

    public static String generateConsistingOfLettersAndNumbers(int length) {
        return IntStream.range(0, length)
                .map(i -> CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
