package com.example.demo.db;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository("jdbcTemplateUserRepository")
public class UserJdbcTemplateRepository implements UserRepository {
    private final JdbcTemplate jdbc;

    public UserJdbcTemplateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final org.springframework.jdbc.core.RowMapper<User> ROW_MAPPER = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        int age = rs.getInt("age");
        u.setAge(rs.wasNull() ? null : age);
        u.setEmail(rs.getString("email"));
        java.math.BigDecimal bal = rs.getBigDecimal("balance");
        u.setBalance(bal == null ? null : bal);
        Timestamp ts = rs.getTimestamp("created_at");
        u.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return u;
    };

    @Override
    public List<User> findAll() {
    return jdbc.query("select id, name, age, email, balance, created_at from users order by id", ROW_MAPPER);
    }

    @Override
    public Optional<User> findById(long id) {
        try {
            User u = jdbc.queryForObject(
                    "select id, name, age, email, balance, created_at from users where id = ?",
                    ROW_MAPPER,
                    id
            );
            return Optional.ofNullable(u);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public long insert(User u) {
    String sql = "insert into users(name, age, email, balance) values (?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getName());
            if (u.getAge() == null) ps.setNull(2, java.sql.Types.INTEGER); else ps.setInt(2, u.getAge());
            if (u.getEmail() == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, u.getEmail());
            if (u.getBalance() == null) ps.setBigDecimal(4, java.math.BigDecimal.ZERO); else ps.setBigDecimal(4, u.getBalance());
            return ps;
        }, kh);
        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Insert failed, no key");
        return key.longValue();
    }

    @Override
    public boolean update(long id, User u) {
        int rows = jdbc.update(
                "update users set name = ?, age = ?, email = ?, balance = ? where id = ?",
                ps -> {
                    ps.setString(1, u.getName());
                    if (u.getAge() == null) ps.setNull(2, java.sql.Types.INTEGER); else ps.setInt(2, u.getAge());
                    if (u.getEmail() == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, u.getEmail());
                    if (u.getBalance() == null) ps.setBigDecimal(4, java.math.BigDecimal.ZERO); else ps.setBigDecimal(4, u.getBalance());
                    ps.setLong(5, id);
                }
        );
        return rows > 0;
    }

    @Override
    public boolean delete(long id) {
        int rows = jdbc.update("delete from users where id = ?", id);
        return rows > 0;
    }
}

