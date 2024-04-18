package fr.norsys.gedapi.dao;

import fr.norsys.gedapi.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int registre(User user) {
        String query = "INSERT INTO ged_user (user_name, password, email, phone, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getFirstName());
            ps.setString(6, user.getLastName());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public User findByUsername(String username) {
        String query = "SELECT * FROM ged_user WHERE user_name = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{username}, getUserRowMapper());}

    public Optional<User> findById(int id) {
        try {
            String sql = "SELECT * FROM ged_user WHERE id = ?"; // replace with your actual SQL query
            User user = jdbcTemplate.queryForObject(sql, new Object[]{id}, getUserRowMapper());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

private RowMapper<User> getUserRowMapper() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("user_name"))
                .password(rs.getString("password"))
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .build();
    }
}
