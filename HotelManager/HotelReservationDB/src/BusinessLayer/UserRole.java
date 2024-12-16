package BusinessLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRole {
    private int userRoleId;
    private String username;
    private int roleId;
    
    public UserRole(int userRoleId, String username, int roleId) {
        this.userRoleId = userRoleId;
        this.username = username;
        this.roleId = roleId;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public String getUsername() {
        return username;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public static void populateUserRole(Connection connection, String username, int roleId) {
        try {
            String sql = "INSERT INTO UserRole (username, role_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, roleId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error while populating UserRole: " + e.getMessage());
        }
    }
}

