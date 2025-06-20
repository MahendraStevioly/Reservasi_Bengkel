public class detail_servis {
    private int idDetail, idBooking, idJenisServis;

    //constructor
    public detail_servis(int idDetail, int idBooking, int idJenisServis){
        this.idDetail = idDetail;
        this.idBooking = idBooking;
        this.idJenisServis = idJenisServis;
    }
    public detail_servis(int idBooking, int idJenisServis){
        this(0, idBooking, idJenisServis);
    }

    //getter
    public int getidDetail(){
        return  idDetail;
    }
    public int getidBooking(){
        return idBooking;
    }
    public int getidJenisServis(){
        return idJenisServis;
    }

    //setter
    public void setidDetail(int idDetail){
        this.idDetail = idDetail;
    }
    public void setidBooking(int idBooking){
        this.idBooking = idBooking;
    }
    public void setidJenisServis(int idJenisServis){
        this.idJenisServis = idJenisServis;
    }

}
