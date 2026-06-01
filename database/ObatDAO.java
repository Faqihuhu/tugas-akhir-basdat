package mypharmacist.database;

import mypharmacist.model.Obat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObatDAO {

    public List<Obat> getAll() {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM Obat ORDER BY kode_obat";
        try (Connection c = DatabaseConnection.getConnection();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(map(r));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Obat> search(String keyword) {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM Obat WHERE kode_obat LIKE ? OR nama_obat LIKE ? ORDER BY kode_obat";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet r = ps.executeQuery();
            while (r.next()) list.add(map(r));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Obat getByKode(String kode) {
        String sql = "SELECT * FROM Obat WHERE kode_obat = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ResultSet r = ps.executeQuery();
            if (r.next()) return map(r);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Obat o) {
        String sql = "INSERT INTO Obat (kode_obat, nama_obat, harga, stok, deskripsi) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, o.getKodeObat());
            ps.setString(2, o.getNamaObat());
            ps.setDouble(3, o.getHarga());
            ps.setInt   (4, o.getStok());
            ps.setString(5, o.getDeskripsi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Obat o) {
        String sql = "UPDATE Obat SET nama_obat=?, harga=?, stok=?, deskripsi=? WHERE kode_obat=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, o.getNamaObat());
            ps.setDouble(2, o.getHarga());
            ps.setInt   (3, o.getStok());
            ps.setString(4, o.getDeskripsi());
            ps.setString(5, o.getKodeObat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String kodeObat) {
        String sql = "DELETE FROM Obat WHERE kode_obat = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kodeObat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Obat map(ResultSet r) throws SQLException {
        return new Obat(
            r.getString("kode_obat"),
            r.getString("nama_obat"),
            r.getDouble("harga"),
            r.getInt("stok"),
            r.getString("deskripsi")
        );
    }
}
