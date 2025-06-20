import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Kelas booking_servisDAO untuk operasi database terkait booking servis
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

    // Get the last booking ID
    public int getLastBookingId() throws SQLException {
        String sql = "SELECT MAX(id_booking) as last_id FROM booking_servis";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
            return 0;
        }
    }

    // Optimized method to get bookings with user and car information in single query
    public List<booking_servis> readBookingsWithDetails() {
        List<booking_servis> list = new ArrayList<>();
        String sql = "SELECT bs.*, m.merk, m.tipe, u.nama as owner_name, t.nama as teknisi_name " +
                    "FROM booking_servis bs " +
                    "LEFT JOIN mobil m ON bs.id_mobil = m.id_mobil " +
                    "LEFT JOIN user u ON m.fk_user = u.id_user " +
                    "LEFT JOIN teknisi t ON bs.id_teknisi = t.id_teknisi " +
                    "ORDER BY bs.tanggal_booking, bs.jam_booking";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                booking_servis bs = new booking_servis(
                    rs.getInt("id_booking"),
                    rs.getInt("id_mobil"),
                    rs.getInt("id_teknisi"),
                    rs.getDate("tanggal_booking"),
                    rs.getTime("jam_booking"),
                    rs.getString("status_booking"),
                    rs.getInt("no_transaksi")
                );
                // Store additional info in the object for display purposes
                bs.setAdditionalInfo(rs.getString("merk"), rs.getString("tipe"), 
                                   rs.getString("owner_name"), rs.getString("teknisi_name"));
                list.add(bs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Optimized method to get bookings for specific user with details
    public List<booking_servis> readBookingsByUserId(int userId) {
        List<booking_servis> list = new ArrayList<>();
        String sql = "SELECT bs.*, m.merk, m.tipe, t.nama as teknisi_name " +
                    "FROM booking_servis bs " +
                    "LEFT JOIN mobil m ON bs.id_mobil = m.id_mobil " +
                    "LEFT JOIN teknisi t ON bs.id_teknisi = t.id_teknisi " +
                    "WHERE m.fk_user = ? " +
                    "ORDER BY bs.tanggal_booking, bs.jam_booking";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                booking_servis bs = new booking_servis(
                    rs.getInt("id_booking"),
                    rs.getInt("id_mobil"),
                    rs.getInt("id_teknisi"),
                    rs.getDate("tanggal_booking"),
                    rs.getTime("jam_booking"),
                    rs.getString("status_booking"),
                    rs.getInt("no_transaksi")
                );
                bs.setAdditionalInfo(rs.getString("merk"), rs.getString("tipe"), 
                                   null, rs.getString("teknisi_name"));
                list.add(bs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Optimized method to calculate total payment for a booking in single query
    public int calculateTotalPayment(int bookingId) {
        String sql = "SELECT SUM(js.harga) as total " +
                    "FROM detail_servis ds " +
                    "JOIN jenis_servis js ON ds.id_jenis_servis = js.id_jenis_servis " +
                    "WHERE ds.id_booking = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Optimized method to get completed transactions with total income
    public List<booking_servis> readCompletedTransactions() {
        List<booking_servis> list = new ArrayList<>();
        String sql = "SELECT bs.*, m.merk, m.tipe, u.nama as owner_name, t.nama as teknisi_name, " +
                    "SUM(js.harga) as total_payment " +
                    "FROM booking_servis bs " +
                    "LEFT JOIN mobil m ON bs.id_mobil = m.id_mobil " +
                    "LEFT JOIN user u ON m.fk_user = u.id_user " +
                    "LEFT JOIN teknisi t ON bs.id_teknisi = t.id_teknisi " +
                    "LEFT JOIN detail_servis ds ON bs.id_booking = ds.id_booking " +
                    "LEFT JOIN jenis_servis js ON ds.id_jenis_servis = js.id_jenis_servis " +
                    "WHERE bs.status_booking = 'Selesai' " +
                    "GROUP BY bs.id_booking, bs.id_mobil, bs.id_teknisi, bs.tanggal_booking, " +
                    "bs.jam_booking, bs.status_booking, bs.no_transaksi, m.merk, m.tipe, u.nama, t.nama " +
                    "ORDER BY bs.tanggal_booking, bs.jam_booking";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                booking_servis bs = new booking_servis(
                    rs.getInt("id_booking"),
                    rs.getInt("id_mobil"),
                    rs.getInt("id_teknisi"),
                    rs.getDate("tanggal_booking"),
                    rs.getTime("jam_booking"),
                    rs.getString("status_booking"),
                    rs.getInt("no_transaksi")
                );
                bs.setAdditionalInfo(rs.getString("merk"), rs.getString("tipe"), 
                                   rs.getString("owner_name"), rs.getString("teknisi_name"));
                bs.setTotalPayment(rs.getInt("total_payment"));
                list.add(bs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
