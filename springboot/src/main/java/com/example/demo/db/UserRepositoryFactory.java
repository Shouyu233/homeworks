package com.example.demo.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryFactory {
    private final UserRepository raw;
    private final UserRepository jt;

    public UserRepositoryFactory(
            @Qualifier("rawUserRepository") UserRepository raw,
            @Qualifier("jdbcTemplateUserRepository") UserRepository jt
    ) {
        this.raw = raw;
        this.jt = jt;
    }

    public UserRepository get(UserBackend backend) {
        return switch (backend) {
            case RAW -> raw;
            case JT -> jt;
        };
    }

    public UserRepository get(String backendParam) {
        return get(UserBackend.fromParam(backendParam));
    }
}

