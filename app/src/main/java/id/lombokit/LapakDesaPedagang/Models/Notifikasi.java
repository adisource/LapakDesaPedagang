package id.lombokit.LapakDesaPedagang.Models;

public class Notifikasi {
    private  int id_pemberitahuan;
    private String tgl;
    private String jam;
    private String judul;
    private String isi;
    private String from;

    public Notifikasi(int id_pemberitahuan,String tgl, String jam,String judul, String isi,String from) {
        this.id_pemberitahuan= id_pemberitahuan;
        this.tgl = tgl;
        this.jam = jam;
        this.judul = judul;
        this.isi = isi;
        this.from = from;
    }

    public int getId_pemberitahuan() {
        return id_pemberitahuan;
    }

    public void setId_pemberitahuan(int id_pemberitahuan) {
        this.id_pemberitahuan = id_pemberitahuan;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
