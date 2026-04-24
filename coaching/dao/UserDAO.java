package coaching.dao;

import coaching.model.User;
import coaching.util.DatabaseConnection;
import coaching.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, username, password_hash) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getUsername());
            // The User object's passwordHash field actually contains plain password at this point
            stmt.setString(4, PasswordUtil.hashPassword(user.getPasswordHash()));
            return stmt.executeUpdate() > 0;
        }
    }

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    if (PasswordUtil.checkPassword(password, hash)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setFullName(rs.getString("full_name"));
                        user.setEmail(rs.getString("email"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(hash);
                        return user;
                    }
                }
            }
        }
        return null;
    }
}