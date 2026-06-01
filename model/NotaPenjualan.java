package mypharmacist.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class NotaPenjualan {
    private String noNota;
    private Date   tanggal;
    private String namaPelanggan;
    private int    idKasir;
    private String namaKasir;
    private double totalPembayaran;
    private List<DetailNota> details = new ArrayList<>();

    public NotaPenjualan() {}

    public String getNoNota()          { return noNota; }
    public Date   getTanggal()         { return tanggal; }
    public String getNamaPelanggan()   { return namaPelanggan; }
    public int    getIdKasir()         { return idKasir; }
    public String getNamaKasir()       { return namaKasir; }
    public double getTotalPembayaran() { return totalPembayaran; }
    public List<DetailNota> getDetails() { return details; }

    public void setNoNota(String v)          { noNota          = v; }
    public void setTanggal(Date v)           { tanggal         = v; }
    public void setNamaPelanggan(String v)   { namaPelanggan   = v; }
    public void setIdKasir(int v)            { idKasir         = v; }
    public void setNamaKasir(String v)       { namaKasir       = v; }
    public void setTotalPembayaran(double v) { totalPembayaran = v; }
    public void setDetails(List<DetailNota> v) { details = v; }

    public void addDetail(DetailNota d) { details.add(d); }

    public void recalcTotal() {
        totalPembayaran = details.stream().mapToDouble(d -> d.getHargaUnit() * d.getQty()).sum();
    }
}
