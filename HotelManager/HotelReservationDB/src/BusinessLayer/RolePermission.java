package BusinessLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RolePermission {
    private int rolePermissionId;
    private int roleId;
    private int permissionId;

    public RolePermission(int rolePermissionId, int roleId, int permissionId) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public int getRolePermissionId() {
        return rolePermissionId;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setRolePermissionId(int rolePermissionId) {
        this.rolePermissionId = rolePermissionId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public static void populateRolePermissions(Connection connection, int roleId, int permissionId) {
        try {
            String sql = "INSERT INTO RolePermission (role_id, permission_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, roleId);
                preparedStatement.setInt(2, permissionId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error while populating RolePermission: " + e.getMessage());
        }
    }
}
