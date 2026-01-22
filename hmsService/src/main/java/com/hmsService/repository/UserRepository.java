package com.hmsService.repository;

import com.hmsService.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> mapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("userid"));
        user.setFullName(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(
                "select * from users where username = ?",
                mapper,
                username);
        return users.stream().findFirst();
    }

    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(
                "select * from users where userid = ?",
                mapper,
                id);
        return users.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from users where email = ?",
                Integer.class,
                email);
        return count != null && count > 0;
    }

    public boolean existsByMobile(String mobile) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from users where mobile = ?",
                Integer.class,
                mobile);
        return count != null && count > 0;
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from users where username = ?",
                Integer.class,
                username);
        return count != null && count > 0;
    }

    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into users (fullname, email, mobile, username, password, role, created_at)
                    values (?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getMobile());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(user.getCreatedAt()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
        }
        return user;
    }

    public void update(User user) {
        jdbcTemplate.update("""
                update users
                set fullname = ?, email = ?, mobile = ?, username = ?, password = ?, role = ?
                where userid = ?
                """,
                user.getFullName(),
                user.getEmail(),
                user.getMobile(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getId());
    }

    public List<User> findAll() {
        return jdbcTemplate.query("select * from users order by userid", mapper);
    }

    public List<User> search(String query) {
        List<User> all = findAll();
        if (query == null || query.isBlank()) {
            return all;
        }
        String lowerQuery = query.toLowerCase();
        return all.stream()
                .filter(u -> (u.getFullName() != null && u.getFullName().toLowerCase().contains(lowerQuery))
                        || (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerQuery))
                        || (u.getUsername() != null && u.getUsername().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }

    public void delete(Long userId) {
        jdbcTemplate.update("delete from users where userid = ?", userId);
    }
}
