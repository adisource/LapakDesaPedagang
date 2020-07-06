package id.lombokit.LapakDesaPedagang.Models;

public class ModelKecamatan {
    int id_kec;
    int id_kab;
    String nama_kecamatan;

    public ModelKecamatan(int id_kec,int id_kab, String nama_kecamatan) {
        this.id_kec = id_kec;
        this.id_kab = id_kab;
        this.nama_kecamatan = nama_kecamatan;
    }

    public int getId_kec() {
        return id_kec;
    }


    public void setId_kec(int id_kec) {
        this.id_kec = id_kec;
    }

    public int getId_kab() {
        return id_kab;
    }

    public void setId_kab(int id_kab) {
        this.id_kab = id_kab;
    }

    public String getNama_kecamatan() {
        return nama_kecamatan;
    }

    public void setNama_kecamatan(String nama_kecamatan) {
        this.nama_kecamatan = nama_kecamatan;
    }
}
