// Kelas mobil merepresentasikan data mobil pelanggan
public class mobil {
    private int idMobil, tahun, fk_user;
    private String merk, tipe;
    private String ownerName; // Additional field for JOIN queries

    //constructor
    public mobil(int idMobil, String merk, String tipe, int tahun, int fk_user){
        this.idMobil = idMobil;
        this.merk = merk;
        this.tipe = tipe;
        this.tahun = tahun;
        this.fk_user = fk_user;
    }

    //getter
    public int getidMobil(){
        return idMobil;
    }
    public String getmerk(){
        return merk;
    }
    public String gettipe(){
        return tipe;
    }
    public int gettahun(){
        return tahun;
    }
    public int getfk_user(){
        return fk_user;
    }
    public String getOwnerName(){
        return ownerName;
    }

    //setter
    public void setidMobil(int idMobil){
        this.idMobil = idMobil;
    }
    public void setmerk(String merk){
        this.merk = merk;
    }
    public void settipe(String tipe){
        this.tipe = tipe;
    }
    public void settahun(int tahun){
        this.tahun = tahun;
    }
    public void setfk_user(int fk_user){
        this.fk_user = fk_user;
    }
    public void setOwnerName(String ownerName){
        this.ownerName = ownerName;
    }
}
