package fr.norsys.gedapi.dao;

import fr.norsys.gedapi.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int registre(User user) {
        String query = "INSERT INTO ged_user (user_name, password, email, phone, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(query, user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(), user.getFirstName(), user.getLastName());
    }

    public User findByUsername(String username) {
        String query = "SELECT * FROM ged_user WHERE user_name = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{username}, getUserRowMapper());}

    public User findById(int id) {
        String query = "SELECT * FROM ged_user WHERE id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, getUserRowMapper());
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
