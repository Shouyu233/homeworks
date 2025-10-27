package com.example.demo.db;

public enum UserBackend {
    RAW, // 原始JDBC
    JT;  // JdbcTemplate

    public static UserBackend fromParam(String s) {
        if (s == null || s.isBlank()) return JT;
        s = s.trim().toLowerCase();
        return switch (s) {
            case "raw", "jdbc", "plain" -> RAW;
            case "jt", "jdbctemplate", "template" -> JT;
            default -> JT;
        };
    }
}

