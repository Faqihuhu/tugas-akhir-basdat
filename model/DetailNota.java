package mypharmacist.model;

public class DetailNota {
    private int    idDetail;
    private String noNota;
    private String kodeObat;
    private String namaObat;
    private double hargaUnit;
    private int    qty;

    public DetailNota() {}

    public DetailNota(String kodeObat, String namaObat, double hargaUnit, int qty) {
        this.kodeObat  = kodeObat;
        this.namaObat  = namaObat;
        this.hargaUnit = hargaUnit;
        this.qty       = qty;
    }

    public int    getIdDetail()  { return idDetail; }
    public String getNoNota()    { return noNota; }
    public String getKodeObat()  { return kodeObat; }
    public String getNamaObat()  { return namaObat; }
    public double getHargaUnit() { return hargaUnit; }
    public int    getQty()       { return qty; }
    public double getSubtotal()  { return hargaUnit * qty; }

    public void setIdDetail(int v)    { idDetail  = v; }
    public void setNoNota(String v)   { noNota    = v; }
    public void setKodeObat(String v) { kodeObat  = v; }
    public void setNamaObat(String v) { namaObat  = v; }
    public void setHargaUnit(double v){ hargaUnit = v; }
    public void setQty(int v)         { qty       = v; }
}
