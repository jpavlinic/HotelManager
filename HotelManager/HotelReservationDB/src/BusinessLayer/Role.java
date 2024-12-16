package BusinessLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Role {
    private int roleId;
    private String roleName;

    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public static void populateRole(Connection connection, String roleName) {
        try {
            String sql = "INSERT INTO Role (role_name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, roleName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error while populating Role: " + e.getMessage());
        }
    }
}
