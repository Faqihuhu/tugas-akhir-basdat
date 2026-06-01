package mypharmacist.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import mypharmacist.database.*;
import mypharmacist.model.*;
import mypharmacist.util.*;

public class DashboardPanel extends JPanel {

    private final MainFrame parent;
    private JLabel lblTotalNota, lblTotalPendapatan, lblTotalObat, lblStokRendah;

    public DashboardPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(Theme.BG_APP);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JScrollPane scroll = new JScrollPane(buildBody());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG_APP);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_APP);
        body.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(Theme.FONT_TITLE);
        pageTitle.setForeground(Theme.TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(pageTitle);

        JLabel sub = new JLabel("Selamat datang di MyPharmacist — ringkasan aktivitas apotek Anda");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sub);
        body.add(Box.createVerticalStrut(24));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        lblTotalNota       = new JLabel("–");
        lblTotalPendapatan = new JLabel("–");
        lblTotalObat       = new JLabel("–");
        lblStokRendah      = new JLabel("–");

        statsRow.add(makeStatCard("🧾 Total Nota",       lblTotalNota,       Theme.ACCENT));
        statsRow.add(makeStatCard("💰 Pendapatan Total", lblTotalPendapatan, Theme.SUCCESS));
        statsRow.add(makeStatCard("💊 Jenis Obat",       lblTotalObat,       Theme.PRIMARY));
        statsRow.add(makeStatCard("⚠️ Stok Rendah",      lblStokRendah,      Theme.WARNING));
        body.add(statsRow);
        body.add(Box.createVerticalStrut(24));

        JLabel secTitle = new JLabel("Transaksi Terbaru");
        secTitle.setFont(Theme.FONT_HEADING);
        secTitle.setForeground(Theme.TEXT_PRIMARY);
        secTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(secTitle);
        body.add(Box.createVerticalStrut(12));

        String[] cols = {"No Nota", "Tanggal", "Pelanggan", "Kasir", "Total"};
        Object[][] data = {};
        JTable table = makeTable(cols, data);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        tableScroll.setPreferredSize(new Dimension(0, 250));
        body.add(tableScroll);

        body.add(Box.createVerticalStrut(24));
        JLabel actTitle = new JLabel("Aksi Cepat");
        actTitle.setFont(Theme.FONT_HEADING);
        actTitle.setForeground(Theme.TEXT_PRIMARY);
        actTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(actTitle);
        body.add(Box.createVerticalStrut(12));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnNota = new JButton("+ Buat Nota Baru");
        Theme.stylePrimaryButton(btnNota);
        btnNota.addActionListener(e -> parent.showPage("penjualan"));

        JButton btnStok = new JButton("📦 Kelola Stok");
        Theme.styleSecondaryButton(btnStok);
        btnStok.addActionListener(e -> parent.showPage("stok"));

        JButton btnRiwayat = new JButton("📋 Riwayat Penjualan");
        Theme.styleSecondaryButton(btnRiwayat);
        btnRiwayat.addActionListener(e -> parent.showPage("riwayat"));

        actions.add(btnNota);
        actions.add(btnStok);
        actions.add(btnRiwayat);
        body.add(actions);
        body.add(Box.createVerticalGlue());

        return body;
    }

    private JPanel makeStatCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
            )
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        return card;
    }

    private JTable makeTable(String[] cols, Object[][] data) {
        JTable table = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setFont(Theme.FONT_TABLE);
        table.setRowHeight(36);
        table.getTableHeader().setFont(Theme.FONT_SUB);
        table.getTableHeader().setBackground(Theme.BG_APP);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 30));
        return table;
    }

    public void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int totalNota = 0;
            double pendapatan = 0;
            int totalObat = 0;
            int stokRendah = 0;

            @Override protected Void doInBackground() {
                NotaDAO nd = new NotaDAO();
                ObatDAO od = new ObatDAO();
                List<NotaPenjualan> notas = nd.getAllNota(null, null, null);
                totalNota = notas.size();
                pendapatan = notas.stream().mapToDouble(NotaPenjualan::getTotalPembayaran).sum();
                List<Obat> obats = od.getAll();
                totalObat = obats.size();
                stokRendah = (int) obats.stream().filter(o -> o.getStok() < 10).count();
                return null;
            }

            @Override protected void done() {
                lblTotalNota.setText(String.valueOf(totalNota));
                lblTotalPendapatan.setText(FormatUtil.formatCurrency(pendapatan));
                lblTotalObat.setText(String.valueOf(totalObat));
                lblStokRendah.setText(String.valueOf(stokRendah));
            }
        };
        worker.execute();
    }
}
