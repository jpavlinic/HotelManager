package BusinessLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Permission {
    private int permissionId;
    private String permissionName;

    public Permission(int permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public static List<Permission> populatePermissions(Connection connection) {
        List<Permission> permissions = new ArrayList<>();
        try {
            String sql = "SELECT permission_id, permission_name FROM Permission";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int permissionId = resultSet.getInt("permission_id");
                        String permissionName = resultSet.getString("permission_name");
                        Permission permission = new Permission(permissionId, permissionName);
                        permissions.add(permission);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while populating permissions: " + e.getMessage());
        }
        return permissions;
    }
}
