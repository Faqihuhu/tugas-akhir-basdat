package mypharmacist.database;

import java.sql.*;
import java.util.*;
import mypharmacist.model.*;

public class NotaDAO {

    public Map<Integer, String> getAllKasir() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT id_kasir, nama_kasir FROM Kasir ORDER BY nama_kasir")) {
            while (r.next()) map.put(r.getInt("id_kasir"), r.getString("nama_kasir"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public String generateNoNota() {
        String prefix = "INV-";
        String sql = "SELECT MAX(CAST(SUBSTRING(no_nota,5,LEN(no_nota)) AS INT)) AS maxNum FROM NotaPenjualan WHERE no_nota LIKE 'INV-%'";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            if (r.next() && r.getObject("maxNum") != null) {
                return prefix + String.format("%05d", r.getInt("maxNum") + 1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return prefix + "00001";
    }

    public boolean simpanNota(NotaPenjualan nota) {
        String sqlHeader = "INSERT INTO NotaPenjualan (no_nota, tanggal, nama_pelanggan, id_kasir, total_pembayaran) VALUES (?,?,?,?,?)";
        String sqlDetail = "INSERT INTO DetailNota (no_nota, kode_obat, nama_obat, harga_unit, qty) VALUES (?,?,?,?,?)";
        Connection c = null;
        try {
            c = DatabaseConnection.getConnection();
            c.setAutoCommit(false);

            try (PreparedStatement psH = c.prepareStatement(sqlHeader)) {
                psH.setString(1, nota.getNoNota());
                psH.setDate  (2, new java.sql.Date(nota.getTanggal().getTime()));
                psH.setString(3, nota.getNamaPelanggan());
                psH.setInt   (4, nota.getIdKasir());
                psH.setDouble(5, nota.getTotalPembayaran());
                psH.executeUpdate();
            }

            try (PreparedStatement psD = c.prepareStatement(sqlDetail)) {
                for (DetailNota d : nota.getDetails()) {
                    psD.setString(1, nota.getNoNota());
                    psD.setString(2, d.getKodeObat());
                    psD.setString(3, d.getNamaObat());
                    psD.setDouble(4, d.getHargaUnit());
                    psD.setInt   (5, d.getQty());
                    psD.addBatch();
                }
                psD.executeBatch();
            }

            c.commit();
            return true;
        } catch (SQLException e) {
            try { if (c != null) c.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (c != null) c.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public List<NotaPenjualan> getAllNota(String keyword, String startDate, String endDate) {
        List<NotaPenjualan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT n.*, k.nama_kasir FROM NotaPenjualan n " +
            "LEFT JOIN Kasir k ON n.id_kasir = k.id_kasir WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (n.no_nota LIKE ? OR n.nama_pelanggan LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND n.tanggal >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND n.tanggal <= ?");
            params.add(endDate);
        }
        sql.append(" ORDER BY n.tanggal DESC, n.no_nota DESC");

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                NotaPenjualan n = new NotaPenjualan();
                n.setNoNota(r.getString("no_nota"));
                n.setTanggal(r.getDate("tanggal"));
                n.setNamaPelanggan(r.getString("nama_pelanggan"));
                n.setIdKasir(r.getInt("id_kasir"));
                n.setNamaKasir(r.getString("nama_kasir"));
                n.setTotalPembayaran(r.getDouble("total_pembayaran"));
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<DetailNota> getDetailByNota(String noNota) {
        List<DetailNota> list = new ArrayList<>();
        String sql = "SELECT * FROM DetailNota WHERE no_nota = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, noNota);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                DetailNota d = new DetailNota();
                d.setIdDetail(r.getInt("id_detail"));
                d.setNoNota(r.getString("no_nota"));
                d.setKodeObat(r.getString("kode_obat"));
                d.setNamaObat(r.getString("nama_obat"));
                d.setHargaUnit(r.getDouble("harga_unit"));
                d.setQty(r.getInt("qty"));
                list.add(d);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteNota(String noNota) {
        String sql = "DELETE FROM NotaPenjualan WHERE no_nota = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, noNota);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
