package com.example.demo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

@Repository("rawUserRepository")
public class UserJdbcRepository implements UserRepository {
    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    

    private User mapRow(ResultSet rs) throws SQLException {
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
    }

    @Override
    public List<User> findAll() throws SQLException {
    String sql = "select id, name, age, email, balance, created_at from users order by id";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    @Override
    public Optional<User> findById(long id) throws SQLException {
    String sql = "select id, name, age, email, balance, created_at from users where id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public long insert(User u) throws SQLException {
    String sql = "insert into users(name, age, email, balance) values (?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            if (u.getAge() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, u.getAge());
            if (u.getEmail() == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, u.getEmail());
            if (u.getBalance() == null) ps.setBigDecimal(4, java.math.BigDecimal.ZERO); else ps.setBigDecimal(4, u.getBalance());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Insert failed, no ID obtained");
    }

    @Override
    public boolean update(long id, User u) throws SQLException {
    String sql = "update users set name = ?, age = ?, email = ?, balance = ? where id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            if (u.getAge() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, u.getAge());
            if (u.getEmail() == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, u.getEmail());
            if (u.getBalance() == null) ps.setBigDecimal(4, java.math.BigDecimal.ZERO); else ps.setBigDecimal(4, u.getBalance());
            ps.setLong(5, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        String sql = "delete from users where id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }
}
