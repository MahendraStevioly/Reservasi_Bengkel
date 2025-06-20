import java.sql.Date;
import java.sql.Time;

public class booking_servis {
    private int idBooking, idMobil, idTeknisi, no_transaksi;
    private Date tanggal;
    private Time jam;
    private String statusBook;

    // Additional fields for optimized queries
    private String merk, tipe, ownerName, teknisiName;
    private int totalPayment;

    //cosntructor
    public booking_servis(int idBooking, int idMobil, int idTeknisi, Date tanggal, Time jam, String statusBook, int no_transaksi){
        this.idBooking = idBooking;
        this.idMobil = idMobil;
        this.idTeknisi = idTeknisi;
        this.tanggal = tanggal;
        this.jam = jam;
        this.statusBook = statusBook;
        this.no_transaksi = no_transaksi;
    }
    public booking_servis(int idMobil, int idTeknisi, Date tanggal, Time jam, String statusBook, int no_transaksi){
        this(0, idMobil, idTeknisi, tanggal, jam, statusBook, no_transaksi);
    }

    //getter
    public int getidBooking(){
        return idBooking;
    }
    public int getidMobil(){
        return idMobil;
    }
    public int getidTeknisi(){
        return idTeknisi;
    }
    public Date gettanggal(){
        return tanggal;
    }
    public Time getjam(){
        return jam;
    }
    public String getstatusBook(){
        return statusBook;
    }
    public int getno_transaksi(){
        return no_transaksi;
    }

    //setter
    public void setidBooking(int idBooking){
        this.idBooking = idBooking;
    }
    public void setidMobil(int idMobil){
        this.idMobil = idMobil;
    }
    public void setidTeknisi(int idTeknisi){
        this.idTeknisi = idTeknisi;
    }
    public void settanggal(Date tanggal){
        this.tanggal = tanggal;
    }
    public void setjam(Time jam){
        this.jam = jam;
    }
    public void setstatusBook(String statusBook){
        this.statusBook = statusBook;
    }
    public void setno_transaksi(int no_transaksi){
        this.no_transaksi = no_transaksi;
    }

    // Method to set additional information from JOIN queries
    public void setAdditionalInfo(String merk, String tipe, String ownerName, String teknisiName) {
        this.merk = merk;
        this.tipe = tipe;
        this.ownerName = ownerName;
        this.teknisiName = teknisiName;
    }

    // Method to set total payment
    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }

    // Getters for additional information
    public String getMerk() {
        return merk;
    }

    public String getTipe() {
        return tipe;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getTeknisiName() {
        return teknisiName;
    }

    public int getTotalPayment() {
        return totalPayment;
    }
}
