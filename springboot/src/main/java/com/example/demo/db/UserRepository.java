package com.example.demo.db;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll() throws Exception;
    Optional<User> findById(long id) throws Exception;
    long insert(User u) throws Exception;
    boolean update(long id, User u) throws Exception;
    boolean delete(long id) throws Exception;
}

