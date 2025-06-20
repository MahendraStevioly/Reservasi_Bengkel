import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class mobilDAO {
//create
    public void addMobil (mobil Mobil) throws SQLException {
        String sql = "INSERT INTO mobil (merk, tipe, tahun, fk_user) "+"VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            // Query Dinamis, gunakan PreparedStatement, karena  ada input dari pengguna
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                stmt.setString(1, Mobil.getmerk());
                stmt.setString(2, Mobil.gettipe());
                stmt.setInt(3, Mobil.gettahun());
                stmt.setInt(4, Mobil.getfk_user());
                stmt.executeUpdate();
                
                // Get the generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Mobil.setidMobil(generatedKeys.getInt(1));
                    }
                }
                
                System.out.println("Mobil berhasil ditambahkan !!");
            }catch(SQLException e){
                e.printStackTrace();
            }
    }

    //read all cars for a specific user
    public List<mobil> readMobilByUserId(int userId) {
        List<mobil> list = new ArrayList<>();
        String sql = "SELECT * FROM mobil WHERE fk_user = ? ORDER BY id_mobil";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mobil m = new mobil(
                    rs.getInt("id_mobil"),
                    rs.getString("merk"),
                    rs.getString("tipe"),
                    rs.getInt("tahun"),
                    rs.getInt("fk_user")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //read all cars (for admin use)
    public List<mobil> readAllMobil() {
        List<mobil> list = new ArrayList<>();
        String sql = "SELECT * FROM mobil ORDER BY id_mobil";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mobil m = new mobil(
                        rs.getInt("id_mobil"),
                        rs.getString("merk"),
                        rs.getString("tipe"),
                        rs.getInt("tahun"),
                        rs.getInt("fk_user")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //delete
    public void deleteMobil(int id) {
        String sql = "DELETE FROM mobil WHERE id_mobil=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Mobil berhasil dihapus.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public mobil getMobilById(int id) {
        String sql = "SELECT * FROM mobil WHERE id_mobil = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new mobil(
                    rs.getInt("id_mobil"),
                    rs.getString("merk"),
                    rs.getString("tipe"),
                    rs.getInt("tahun"),
                    rs.getInt("fk_user")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Optimized method to get all cars with user information in single query
    public List<mobil> readAllMobilWithUserInfo() {
        List<mobil> list = new ArrayList<>();
        String sql = "SELECT m.*, u.nama as owner_name FROM mobil m " +
                    "LEFT JOIN user u ON m.fk_user = u.id_user " +
                    "ORDER BY m.id_mobil";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mobil m = new mobil(
                    rs.getInt("id_mobil"),
                    rs.getString("merk"),
                    rs.getString("tipe"),
                    rs.getInt("tahun"),
                    rs.getInt("fk_user")
                );
                // Store owner name in the mobil object for display
                m.setOwnerName(rs.getString("owner_name"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
