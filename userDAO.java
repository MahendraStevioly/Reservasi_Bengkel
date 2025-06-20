import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userDAO {
    //create
    public void createuser (User user) throws SQLException {
        String sql = "INSERT INTO user (username, password, role, nama, no_tlp) "+"VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            // Query Dinamis, gunakan PreparedStatement, karena  ada input dari pengguna
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, user.getusername());
                stmt.setString(2, user.getpassword());
                stmt.setString(3, user.getrole().toString());
                stmt.setString(4, user.getnama());
                stmt.setString(5, user.getno_tlp());
                stmt.executeUpdate();
                System.out.println("User berhasil dibuat !!");
            }catch(SQLException e){
                e.printStackTrace();
            }
    }

    //read
    public List<User> readAllUser() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = DBConnection.getConnection();
            // Query Statis, gunakan Statement saja, karena tidak ada input dari pengguna
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        User.Role.fromDb(rs.getString("role")),
                        rs.getString("nama"),
                        rs.getString("no_tlp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //update
    public void updateUser(String username, String newPassword) {
        String selectSql = "SELECT id_user FROM user WHERE username = ?";
        String updateSql = "UPDATE user SET password = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, username);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newPassword);
                    updateStmt.setString(2, username);
                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Password berhasil diperbarui untuk user: " + username);
                    }
                }
            } else {
                System.out.println("Username tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete
    public void deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id_user=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("User berhasil dihapus.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //get user by id
    public User getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    User.Role.fromDb(rs.getString("role")),
                    rs.getString("nama"),
                    rs.getString("no_tlp")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Optimized login method - queries only specific user instead of loading all
    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                String role = rs.getString("role");
                if (role.equals("PELANGGAN")) {
                    // If password in DB is null/empty, allow any password (including empty)
                    if (dbPassword == null || dbPassword.trim().isEmpty()) {
                        return new User(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("password"),
                            User.Role.fromDb(rs.getString("role")),
                            rs.getString("nama"),
                            rs.getString("no_tlp")
                        );
                    } else {
                        // If password is set in DB, must match
                        if (dbPassword.equals(password)) {
                            return new User(
                                rs.getInt("id_user"),
                                rs.getString("username"),
                                rs.getString("password"),
                                User.Role.fromDb(rs.getString("role")),
                                rs.getString("nama"),
                                rs.getString("no_tlp")
                            );
                        }
                    }
                } else {
                    // For admin, password must match
                    if (dbPassword != null && dbPassword.equals(password)) {
                        return new User(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("password"),
                            User.Role.fromDb(rs.getString("role")),
                            rs.getString("nama"),
                            rs.getString("no_tlp")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to check if username exists (for validation)
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

