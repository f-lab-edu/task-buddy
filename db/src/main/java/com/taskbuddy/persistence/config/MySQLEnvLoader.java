package com.taskbuddy.persistence.config;

import io.github.cdimascio.dotenv.Dotenv;

public class MySQLEnvLoader {
    private static final Dotenv DOTENV = Dotenv.configure()
            .directory("env")
            .load();

    public static String get(String key, String defaultValue) {
        return DOTENV.get(key, defaultValue);
    }
}
