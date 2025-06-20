public class User{
    public enum Role {
        ADMIN("admin"), 
        PELANGGAN("pelanggan");

        private final String dbValue;
        Role(String dbValue) { this.dbValue = dbValue; }
        public String getDbValue() { return dbValue; }
        
        public static Role fromDb(String dbValue) {
            for (Role role : values()) {
                if (role.dbValue.equals(dbValue)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Role tidak valid: " + dbValue);
        }
    }

    private int idUser;
    private String username, password, nama, no_tlp;
    private Role role;
    

    //constructor
    public User(int idUser, String username, String password, Role role, String nama, String no_tlp){
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
        this.nama = nama;
        this.no_tlp = no_tlp;
    }
    public User(String username, String password, Role role, String nama, String no_tlp){
        this(0, username, password, role, nama, no_tlp);
    }

    //getter
    public int getidUser(){
        return idUser;
    }
    public String getusername(){
        return username;
    }
    public String getpassword(){
        return password;
    }
    public Role getrole(){
        return role;
    }
    public String getnama(){
        return nama;
    }
    public String getno_tlp(){
        return no_tlp;
    }

    //setter
    public void setidUser (int idUser){
        this.idUser = idUser;
    }
    public void setusername (String username){
        if (username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username tidak bolek kosong !!");
        }else {
            this.username = username;
        }
    }
    public void setpassword(String password){
        this.password = password;
    }
    public void setrole (Role role){
        this.role = role;
    }
    public void setnama(String nama){
        if (nama == null || nama.trim().isEmpty()){
            throw new IllegalArgumentException("Nama tidak bolek kosong !!");
        }else {
            this.nama = nama;
        }
    }
    public void setno_tlp(String no_tlp){
        if (no_tlp == null || no_tlp.trim().isEmpty()){
            throw new IllegalArgumentException("Nomor telepon tidak bolek kosong !!");
        }else {
            this.no_tlp = no_tlp;
        }
    }
}
