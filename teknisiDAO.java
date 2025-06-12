import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class teknisiDAO {
    //create
    public void createTeknisi(String nama) {
    String sql = "INSERT INTO teknisi (nama) VALUES (?)";
    try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.executeUpdate();
            System.out.println("Teknisi berhasil ditambahkan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //read
    public List<teknisi> readAllteknisi() {
        List<teknisi> list = new ArrayList<>();
        String sql = "SELECT * FROM teknisi";
        try (Connection conn = DBConnection.getConnection();
            // Query Statis, gunakan Statement saja, karena tidak ada input dari pengguna
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new teknisi(
                        rs.getInt("id_teknisi"),
                        rs.getString("nama"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //update
    public void updateNamaTeknisi(int idTeknisi, String newNama) {
        String sql = "UPDATE teknisi SET nama = ? WHERE id_teknisi = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newNama);
            stmt.setInt(2, idTeknisi);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Nama teknisi berhasil diperbarui.");
            } else {
                System.out.println("Teknisi tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete
    public void deleteTeknisi(int id) {
        String sql = "DELETE FROM teknisi WHERE id_teknisi=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Teknisi berhasil dihapus.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //get teknisi by id
    public teknisi getTeknisiById(int id) {
        String sql = "SELECT * FROM teknisi WHERE id_teknisi = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new teknisi(
                    rs.getInt("id_teknisi"),
                    rs.getString("nama"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //add teknisi
    public void addTeknisi(teknisi Teknisi) throws SQLException {
        String sql = "INSERT INTO teknisi (nama, status) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Teknisi.getnama());
            stmt.setString(2, Teknisi.getstatus());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Teknisi.setidTeknisi(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Teknisi berhasil ditambahkan!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //update status teknisi
    public void updateStatusTeknisi(int id, String newStatus) {
        String sql = "UPDATE teknisi SET status = ? WHERE id_teknisi = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Status teknisi berhasil diperbarui!");
            } else {
                System.out.println("Teknisi tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
