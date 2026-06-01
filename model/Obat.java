package mypharmacist.model;

public class Obat {
    private String kodeObat;
    private String namaObat;
    private double harga;
    private int stok;
    private String deskripsi;

    public Obat() {}

    public Obat(String kodeObat, String namaObat, double harga, int stok, String deskripsi) {
        this.kodeObat  = kodeObat;
        this.namaObat  = namaObat;
        this.harga     = harga;
        this.stok      = stok;
        this.deskripsi = deskripsi;
    }

    public String getKodeObat()  { return kodeObat; }
    public String getNamaObat()  { return namaObat; }
    public double getHarga()     { return harga; }
    public int    getStok()      { return stok; }
    public String getDeskripsi() { return deskripsi; }

    public void setKodeObat(String v)  { kodeObat  = v; }
    public void setNamaObat(String v)  { namaObat  = v; }
    public void setHarga(double v)     { harga     = v; }
    public void setStok(int v)         { stok      = v; }
    public void setDeskripsi(String v) { deskripsi = v; }

    @Override public String toString() { return namaObat; }
}
