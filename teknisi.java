

public class teknisi {
    private int idTeknisi;
    private String nama, status;

    //constructor
    public teknisi(int idTeknisi, String nama, String status){
        this.idTeknisi = idTeknisi;
        this.nama = nama;
        this.status = status;
    }
    public teknisi (String nama, String status){
        this(0, nama, status);
    }

    //getter
    public int getidTeknisi(){
        return idTeknisi;
    }
    public String getnama(){
        return nama;
    }
    public String getstatus(){
        return status;
    }

    //setter
    public void setidTeknisi(int idTeknisi){
        this.idTeknisi = idTeknisi;
    }
    public void setnama(String nama){
        if(nama == null || nama.trim().isEmpty()){
            throw new IllegalArgumentException("Nama tidak boleh kosong !!");
        }else {
            this.nama = nama;
        }
    }
    public void setstatus(String status){
        this.status = status;
    }
}
