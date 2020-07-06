package id.lombokit.LapakDesaPedagang.Models;

public class ModelKab {
    int id_kab;
    String nama_kab;

    public ModelKab(int id_kab, String nama_kab) {
        this.id_kab = id_kab;
        this.nama_kab = nama_kab;
    }

    public int getId_kab() {
        return id_kab;
    }

    public void setId_kab(int id_kab) {
        this.id_kab = id_kab;
    }

    public String getNama_kab() {
        return nama_kab;
    }

    public void setNama_kab(String nama_kab) {
        this.nama_kab = nama_kab;
    }
}
