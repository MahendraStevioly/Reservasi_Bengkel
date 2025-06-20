import java.sql.Time;

public class jenis_servis {
    private int idJenisServis, harga;
    private String NamaServis;
    private Time time;

    //constructor
    public jenis_servis(int idJenisServis, String NamaServis, Time time, int harga){
        this.idJenisServis = idJenisServis;
        this.NamaServis = NamaServis;
        this.time = time;
        this.harga = harga;
    }
    public jenis_servis(String NamaServis, Time time, int harga){
        this(0, NamaServis, time, harga);
    }

    //getter
    public int getidJenisServis(){
        return idJenisServis;
    }
    public String getNamaServis(){
        return NamaServis;
    }
    public Time gettime(){
        return time;
    }
    public int getharga(){
        return harga;
    }

    //setter
    public void setidJenisServis(int idJenisServis){
        this.idJenisServis = idJenisServis;
    }
    public void setNamaServis(String NamaString){
        this.NamaServis = NamaString;
    }
    public void settime(Time time){
        this.time = time;
    }
    public void setharga(int harga){
        this.harga = harga;
    }
}
