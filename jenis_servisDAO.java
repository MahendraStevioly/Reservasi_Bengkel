import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class jenis_servisDAO {
    //create
    public void createJenisServis (jenis_servis Jenis_servis) throws SQLException {
        String sql = "INSERT INTO jenis_servis (nama_servis, estimasi_waktu, harga) "+"VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            // Query Dinamis, gunakan PreparedStatement, karena  ada input dari pengguna
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, Jenis_servis.getNamaServis());
                stmt.setTime(2, Jenis_servis.gettime());
                stmt.setInt(3, Jenis_servis.getharga());
                stmt.executeUpdate();
                System.out.println("Jenis_servis berhasil dibuat !!");
            }catch(SQLException e){
                e.printStackTrace();
            }
    }

    //read
    public List<jenis_servis> readJenisServis() {
        List<jenis_servis> list = new ArrayList<>();
        String sql = "SELECT * FROM jenis_servis";
        try (Connection conn = DBConnection.getConnection();
            // Query Statis, gunakan Statement saja, karena tidak ada input dari pengguna
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new jenis_servis(
                        rs.getInt("id_jenis_servis"),
                        rs.getString("nama_servis"),
                        rs.getTime("estimasi_waktu"),
                        rs.getInt("harga")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //update harga
    public void updateUser(String nama_servis, int newHarga) {
        String selectSql = "SELECT id_jenis_servis FROM jenis_servis WHERE nama_servis = ?";
        String updateSql = "UPDATE jenis_servis SET harga = ? WHERE nama_servis = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, nama_servis);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, newHarga);
                    updateStmt.setString(2, nama_servis);
                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Harga berhasil diperbarui untuk servis: " + nama_servis);
                    }
                }
            } else {
                System.out.println("servis tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete
    public void deleteServis(int id) {
        String sql = "DELETE FROM jenis_servis WHERE id_jenis_servis=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Servis berhasil dihapus.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteJenisServis(int id) {
        String sql = "DELETE FROM jenis_servis WHERE id_jenis_servis=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Jenis servis berhasil dihapus.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //add jenis servis
    public void addJenisServis(jenis_servis JenisServis) throws SQLException {
        String sql = "INSERT INTO jenis_servis (nama_servis, estimasi_waktu, harga) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, JenisServis.getNamaServis());
            stmt.setTime(2, JenisServis.gettime());
            stmt.setInt(3, JenisServis.getharga());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    JenisServis.setidJenisServis(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Jenis servis berhasil ditambahkan!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //update jenis servis
    public void updateJenisServis(int id, String newNama, Time newTime, int newHarga) {
        String sql = "UPDATE jenis_servis SET nama_servis = ?, estimasi_waktu = ?, harga = ? WHERE id_jenis_servis = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newNama);
            stmt.setTime(2, newTime);
            stmt.setInt(3, newHarga);
            stmt.setInt(4, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Jenis servis berhasil diperbarui!");
            } else {
                System.out.println("Jenis servis tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
