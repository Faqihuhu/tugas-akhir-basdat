package mypharmacist.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import mypharmacist.database.*;
import mypharmacist.model.*;
import mypharmacist.util.*;

public class RiwayatPanel extends JPanel {

    private final MainFrame parent;
    private final NotaDAO notaDAO = new NotaDAO();

    private JTextField tfSearch, tfStart, tfEnd;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<NotaPenjualan> currentData;

    public RiwayatPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(Theme.BG_APP);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildTable(),    BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel title = new JLabel("Riwayat Nota Penjualan");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        bar.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        tfSearch = new JTextField(14);
        Theme.styleTextField(tfSearch);
        tfSearch.setPreferredSize(new Dimension(160, 32));
        tfSearch.putClientProperty("placeholder", "Cari no/pelanggan...");

        tfStart = new JTextField(10);
        Theme.styleTextField(tfStart);
        tfStart.setPreferredSize(new Dimension(110, 32));
        tfStart.setToolTipText("Tanggal mulai (yyyy-MM-dd)");

        tfEnd = new JTextField(10);
        Theme.styleTextField(tfEnd);
        tfEnd.setPreferredSize(new Dimension(110, 32));
        tfEnd.setToolTipText("Tanggal akhir (yyyy-MM-dd)");

        JButton btnFilter = new JButton("Filter");
        Theme.stylePrimaryButton(btnFilter);
        btnFilter.setPreferredSize(new Dimension(80, 32));
        btnFilter.addActionListener(e -> refresh());

        JButton btnReset = new JButton("Reset");
        Theme.styleSecondaryButton(btnReset);
        btnReset.setPreferredSize(new Dimension(70, 32));
        btnReset.addActionListener(e -> { tfSearch.setText(""); tfStart.setText(""); tfEnd.setText(""); refresh(); });

        filterPanel.add(makeSmallLabel("Cari:"));
        filterPanel.add(tfSearch);
        filterPanel.add(makeSmallLabel("Dari:"));
        filterPanel.add(tfStart);
        filterPanel.add(makeSmallLabel("s/d:"));
        filterPanel.add(tfEnd);
        filterPanel.add(btnFilter);
        filterPanel.add(btnReset);
        bar.add(filterPanel, BorderLayout.EAST);

        return bar;
    }

    private JPanel buildTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        String[] cols = {"", "No Nota", "Tanggal", "Nama Pelanggan", "Kasir", "Total Pembayaran", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionEditor(this));
        table.getColumnModel().getColumn(6).setMinWidth(100);
        table.getColumnModel().getColumn(6).setMaxWidth(120);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sc.getViewport().setBackground(Theme.BG_CARD);
        panel.add(sc, BorderLayout.CENTER);

        JLabel statusBar = new JLabel(" Menampilkan 0 data");
        statusBar.setFont(Theme.FONT_SMALL);
        statusBar.setForeground(Theme.TEXT_SECONDARY);
        panel.add(statusBar, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.FONT_TABLE);
        t.setRowHeight(40);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setGridColor(Theme.BORDER);
        t.setSelectionBackground(new Color(235, 245, 255));
        t.setSelectionForeground(Theme.TEXT_PRIMARY);
        t.setFocusable(false);

        JTableHeader header = t.getTableHeader();
        header.setFont(Theme.FONT_SUB);
        header.setBackground(Theme.BG_APP);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0, Theme.PRIMARY));
        header.setReorderingAllowed(false);
    }

    public void refresh() {
        String kw    = tfSearch != null ? tfSearch.getText().trim() : "";
        String start = tfStart  != null ? tfStart.getText().trim()  : "";
        String end   = tfEnd    != null ? tfEnd.getText().trim()    : "";

        SwingWorker<List<NotaPenjualan>, Void> w = new SwingWorker<>() {
            @Override protected List<NotaPenjualan> doInBackground() {
                return notaDAO.getAllNota(kw, start, end);
            }
            @Override protected void done() {
                try {
                    currentData = get();
                    tableModel.setRowCount(0);
                    for (int i = 0; i < currentData.size(); i++) {
                        NotaPenjualan n = currentData.get(i);
                        tableModel.addRow(new Object[]{
                            i+1,
                            n.getNoNota(),
                            FormatUtil.formatDate(n.getTanggal()),
                            n.getNamaPelanggan(),
                            n.getNamaKasir(),
                            FormatUtil.formatCurrency(n.getTotalPembayaran()),
                            "actions"
                        });
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        w.execute();
    }

    void viewDetail(int row) {
        if (currentData == null || row >= currentData.size()) return;
        NotaPenjualan nota = currentData.get(row);

        List<DetailNota> details = new NotaDAO().getDetailByNota(nota.getNoNota());

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Nota: " + nota.getNoNota(), true);
        dlg.setSize(560, 420);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(0,12));
        content.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        content.setBackground(Theme.BG_APP);

        JPanel info = new JPanel(new GridLayout(0,2,8,6));
        info.setBackground(Theme.BG_CARD);
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        addInfoRow(info, "No Nota:",      nota.getNoNota());
        addInfoRow(info, "Tanggal:",      FormatUtil.formatDate(nota.getTanggal()));
        addInfoRow(info, "Pelanggan:",    nota.getNamaPelanggan());
        addInfoRow(info, "Kasir:",        nota.getNamaKasir());
        addInfoRow(info, "Total:",        FormatUtil.formatCurrency(nota.getTotalPembayaran()));
        content.add(info, BorderLayout.NORTH);

        String[] cols = {"Kode", "Nama Obat", "Harga/Unit", "Qty", "Subtotal"};
        Object[][] data = new Object[details.size()][5];
        for (int i=0; i<details.size(); i++) {
            DetailNota d = details.get(i);
            data[i] = new Object[]{d.getKodeObat(), d.getNamaObat(),
                FormatUtil.formatCurrency(d.getHargaUnit()), d.getQty(),
                FormatUtil.formatCurrency(d.getSubtotal())};
        }
        JTable detailTable = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        detailTable.setFont(Theme.FONT_TABLE);
        detailTable.setRowHeight(34);
        content.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        JButton btnClose = new JButton("Tutup");
        Theme.styleSecondaryButton(btnClose);
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        btnRow.add(btnClose);
        content.add(btnRow, BorderLayout.SOUTH);

        dlg.setContentPane(content);
        dlg.setVisible(true);
    }

    void deleteNota(int row) {
        if (currentData == null || row >= currentData.size()) return;
        NotaPenjualan nota = currentData.get(row);

        JDialog confirm = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Konfirmasi Hapus", true);
        confirm.setSize(380, 200);
        confirm.setLocationRelativeTo(this);
        confirm.setResizable(false);

        JPanel cp = new JPanel(new BorderLayout(0,12));
        cp.setBorder(BorderFactory.createEmptyBorder(20,20,16,20));
        cp.setBackground(Theme.BG_CARD);

        JPanel msgRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        msgRow.setOpaque(false);
        JLabel icon = new JLabel("⚠️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel msg = new JLabel("<html><b>KONFIRMASI HAPUS DATA!</b><br>Apakah Anda yakin ingin menghapus Nota<br><b>" + nota.getNoNota() + "</b> beserta detailnya?<br><small>Tindakan ini tidak dapat dibatalkan.</small></html>");
        msg.setFont(Theme.FONT_BODY);
        msg.setForeground(Theme.TEXT_PRIMARY);
        msgRow.add(icon); msgRow.add(msg);
        cp.add(msgRow, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnHapus = new JButton("YA, HAPUS");
        Theme.styleDangerButton(btnHapus);
        JButton btnBatal = new JButton("BATAL");
        Theme.styleSecondaryButton(btnBatal);

        btnHapus.addActionListener(e -> {
            confirm.dispose();
            boolean ok = notaDAO.deleteNota(nota.getNoNota());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Nota berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus nota.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnBatal.addActionListener(e -> confirm.dispose());

        btnRow.add(btnHapus); btnRow.add(btnBatal);
        cp.add(btnRow, BorderLayout.SOUTH);
        confirm.setContentPane(cp);
        confirm.setVisible(true);
    }

    void editNota(int row) {
        if (currentData == null || row >= currentData.size()) return;
        JOptionPane.showMessageDialog(this, "Fitur edit nota akan segera tersedia.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addInfoRow(JPanel p, String label, String value) {
        JLabel l = new JLabel(label); l.setFont(Theme.FONT_SUB); l.setForeground(Theme.TEXT_SECONDARY);
        JLabel v = new JLabel(value); v.setFont(Theme.FONT_BODY); v.setForeground(Theme.TEXT_PRIMARY);
        p.add(l); p.add(v);
    }

    private JLabel makeSmallLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(Theme.FONT_SMALL); l.setForeground(Theme.TEXT_SECONDARY); return l;
    }

    static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            if (!sel) comp.setBackground(r%2==0 ? Theme.BG_CARD : Theme.BG_TABLE_ALT);
            comp.setForeground(Theme.TEXT_PRIMARY);
            ((JComponent)comp).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
            return comp;
        }
    }

    static class ActionRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnView, btnEdit, btnDel;
        ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 4));
            setOpaque(true);
            btnView = iconBtn("👁", Theme.ACCENT);
            btnEdit = iconBtn("✏", Theme.WARNING);
            btnDel  = iconBtn("🗑", Theme.DANGER);
            add(btnView); add(btnEdit); add(btnDel);
        }
        private JButton iconBtn(String t, Color bg) {
            JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI Emoji",Font.PLAIN,11)); b.setFocusPainted(false);
            b.setBorderPainted(false); b.setPreferredSize(new Dimension(28,26)); b.setOpaque(true); return b;
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(r%2==0 ? Theme.BG_CARD : Theme.BG_TABLE_ALT); return this;
        }
    }

    static class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton btnView, btnEdit, btnDel;
        private int currentRow;

        ActionEditor(RiwayatPanel parent) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 4));
            panel.setBackground(Theme.BG_CARD);
            btnView = iconBtn("👁", Theme.ACCENT);
            btnEdit = iconBtn("✏", Theme.WARNING);
            btnDel  = iconBtn("🗑", Theme.DANGER);
            btnView.addActionListener(e -> { fireEditingStopped(); parent.viewDetail(currentRow); });
            btnEdit.addActionListener(e -> { fireEditingStopped(); parent.editNota(currentRow); });
            btnDel.addActionListener (e -> { fireEditingStopped(); parent.deleteNota(currentRow); });
            panel.add(btnView); panel.add(btnEdit); panel.add(btnDel);
        }
        private JButton iconBtn(String t, Color bg) {
            JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI Emoji",Font.PLAIN,11)); b.setFocusPainted(false);
            b.setBorderPainted(false); b.setPreferredSize(new Dimension(28,26)); b.setOpaque(true); return b;
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            currentRow = r; return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }
}
