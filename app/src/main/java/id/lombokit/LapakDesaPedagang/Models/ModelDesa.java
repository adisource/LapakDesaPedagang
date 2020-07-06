package id.lombokit.LapakDesaPedagang.Models;

public class ModelDesa {
    private int id_desa;
    private  int id_kec;
    private String nama_desa;

    public ModelDesa(int id_desa, int id_kec, String nama_desa) {
        this.id_desa = id_desa;
        this.id_kec = id_kec;
        this.nama_desa = nama_desa;
    }

    public int getId_desa() {
        return id_desa;
    }

    public void setId_desa(int id_desa) {
        this.id_desa = id_desa;
    }

    public int getId_kec() {
        return id_kec;
    }

    public void setId_kec(int id_kec) {
        this.id_kec = id_kec;
    }

    public String getNama_desa() {
        return nama_desa;
    }

    public void setNama_desa(String nama_desa) {
        this.nama_desa = nama_desa;
    }
}
