import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class booking_servisDAO {
//create
    public void createBooking (booking_servis Booking_servis) throws SQLException {
        String sql = "INSERT INTO booking_servis (id_mobil, id_teknisi, tanggal_booking, jam_booking, status_booking, no_transaksi) "+"VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            // Query Dinamis, gunakan PreparedStatement, karena  ada input dari pengguna
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, Booking_servis.getidMobil());
                stmt.setInt(2, Booking_servis.getidTeknisi());
                stmt.setDate(3, Booking_servis.gettanggal());
                stmt.setTime(4, Booking_servis.getjam());
                stmt.setString(5, Booking_servis.getstatusBook());
                stmt.setInt(6, Booking_servis.getno_transaksi());
                stmt.executeUpdate();
                System.out.println("Booking berhasil dibuat !!");
            }catch(SQLException e){
                e.printStackTrace();
            }
    }

    //read
    public List<booking_servis> readbBooking_servis() {
        List<booking_servis> list = new ArrayList<>();
        String sql = "SELECT * FROM booking_servis ORDER BY tanggal_booking, jam_booking";
        try (Connection conn = DBConnection.getConnection();
            // Query Statis, gunakan Statement saja, karena tidak ada input dari pengguna
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new booking_servis(
                        rs.getInt("id_booking"),
                        rs.getInt("id_mobil"),
                        rs.getInt("id_teknisi"),
                        rs.getDate("tanggal_booking"),
                        rs.getTime("jam_booking"),
                        rs.getString("status_booking"),
                        rs.getInt("no_transaksi")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getDefaultStatus() {
        String sql = "SELECT COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'booking_servis' AND COLUMN_NAME = 'status_booking'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String defaultValue = rs.getString("COLUMN_DEFAULT");
                // Remove quotes if present
                return defaultValue.replaceAll("'", "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Menunggu"; // fallback default
    }

    //update status booking
    public void updateBookingStatus(int id, String newStatus) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Get default status if newStatus is "Diproses"
            if (newStatus.equals("Diproses")) {
                newStatus = getDefaultStatus();
            }
            
            // Update booking status
            String sql = "UPDATE booking_servis SET status_booking = ? WHERE id_booking = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            
            // Get the booking to check technician status
            String getBookingSql = "SELECT id_teknisi FROM booking_servis WHERE id_booking = ?";
            PreparedStatement getBookingStmt = conn.prepareStatement(getBookingSql);
            getBookingStmt.setInt(1, id);
            ResultSet rs = getBookingStmt.executeQuery();
            
            if (rs.next()) {
                int teknisiId = rs.getInt("id_teknisi");
                if (teknisiId > 0) {
                    // Update technician status based on booking status
                    String teknisiStatus = (newStatus.equals("Selesai") || newStatus.equals("Ditolak")) ? "Tersedia" : "Sibuk";
                    String updateTeknisiSql = "UPDATE teknisi SET status = ? WHERE id_teknisi = ?";
                    PreparedStatement updateTeknisiStmt = conn.prepareStatement(updateTeknisiSql);
                    updateTeknisiStmt.setString(1, teknisiStatus);
                    updateTeknisiStmt.setInt(2, teknisiId);
                    updateTeknisiStmt.executeUpdate();
                }
            }
            
            conn.commit(); // Commit transaction
            System.out.println("Status booking dan teknisi berhasil diperbarui!");
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //batalkan booking
    public void deleteBooking(int id) {
        String sql = "DELETE FROM booking_servis WHERE id_booking=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Booking berhasil dibatalkan");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBookingTransactionNumber(int idBooking, int noTransaksi) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            String sql = "UPDATE booking_servis SET no_transaksi = ? WHERE id_booking = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, noTransaksi);
            stmt.setInt(2, idBooking);
            stmt.executeUpdate();
            
            conn.commit(); // Commit transaction
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
