import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class detail_servisDAO {
    //create
    public void createDetailServis(detail_servis Detail_servis) throws SQLException {
        String sql = "INSERT INTO detail_servis (id_booking, id_jenis_servis) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Detail_servis.getidBooking());
            stmt.setInt(2, Detail_servis.getidJenisServis());
            stmt.executeUpdate();
            System.out.println("Detail servis berhasil ditambahkan!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //read
    public List<detail_servis> readDetail_Servis() {
        List<detail_servis> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_servis";
        try (Connection conn = DBConnection.getConnection();
            // Query Statis, gunakan Statement saja, karena tidak ada input dari pengguna
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new detail_servis(
                        rs.getInt("id_detail"),
                        rs.getInt("id_booking"),
                        rs.getInt("id_jenis_servis")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
