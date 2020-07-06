package id.lombokit.LapakDesaPedagang.Models;

public class Users {
    private  int id;
    private  String nama;
    private  String telpon;
    private  String alamat;
    private  int total_belanja;
    private  int jumlah;
    private  String desa;
    private  int status_0,status_1,status_2;

    public Users(int id,String nama, String telpon,int total_belanja,int jumlah,String desa,int status_0,int status_1,int status_2){
        this.id = id;
        this.nama = nama;
        this.telpon = telpon;
        this.total_belanja = total_belanja;
        this.jumlah = jumlah;
        this.desa = desa;
        this.status_0 = status_0;
        this.status_1 = status_1;
        this.status_2 = status_2;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelpon() {
        return telpon;
    }

    public void setTelpon(String telpon) {
        this.telpon = telpon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getTotal_belanja() {
        return total_belanja;
    }

    public void setTotal_belanja(int total_belanja) {
        this.total_belanja = total_belanja;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public int getStatus_0() {
        return status_0;
    }

    public void setStatus_0(int status_0) {
        this.status_0 = status_0;
    }

    public int getStatus_1() {
        return status_1;
    }

    public void setStatus_1(int status_1) {
        this.status_1 = status_1;
    }

    public int getStatus_2() {
        return status_2;
    }

    public void setStatus_2(int status_2) {
        this.status_2 = status_2;
    }
}
