package mypharmacist.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import mypharmacist.database.*;
import mypharmacist.model.*;
import mypharmacist.util.*;

public class StokPanel extends JPanel {

    private final MainFrame parent;
    private final ObatDAO obatDAO = new ObatDAO();

    private JTextField tfSearch;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Obat> currentData;

    public StokPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(Theme.BG_APP);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel title = new JLabel("Stok Obat / Inventori");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        tfSearch = new JTextField(16);
        Theme.styleTextField(tfSearch);
        tfSearch.setPreferredSize(new Dimension(180, 32));
        tfSearch.setToolTipText("Cari kode atau nama obat...");

        JButton btnCari = new JButton("🔍 Cari");
        Theme.styleSecondaryButton(btnCari);
        btnCari.setPreferredSize(new Dimension(80, 32));
        btnCari.addActionListener(e -> refresh());

        JButton btnTambah = new JButton("+ Tambah Obat");
        Theme.stylePrimaryButton(btnTambah);
        btnTambah.addActionListener(e -> showFormObat(null));

        right.add(tfSearch); right.add(btnCari); right.add(btnTambah);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        String[] cols = {"Kode Obat", "Nama Obat", "Harga (Rp)", "Stok", "Deskripsi", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        table = new JTable(tableModel);
        styleTable();

        table.getColumnModel().getColumn(5).setCellRenderer(new StokActionRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new StokActionEditor(this));
        table.getColumnModel().getColumn(5).setMinWidth(90);
        table.getColumnModel().getColumn(5).setMaxWidth(110);

        table.setDefaultRenderer(Object.class, new Striped());

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sc.getViewport().setBackground(Theme.BG_CARD);
        panel.add(sc, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable() {
        table.setFont(Theme.FONT_TABLE);
        table.setRowHeight(38);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(235,245,255));
        table.setFocusable(false);
        JTableHeader h = table.getTableHeader();
        h.setFont(Theme.FONT_SUB);
        h.setBackground(Theme.BG_APP);
        h.setForeground(Theme.TEXT_SECONDARY);
        h.setPreferredSize(new Dimension(0,40));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0, Theme.PRIMARY));
        h.setReorderingAllowed(false);
    }

    public void refresh() {
        String kw = tfSearch != null ? tfSearch.getText().trim() : "";
        SwingWorker<List<Obat>, Void> w = new SwingWorker<>() {
            @Override protected List<Obat> doInBackground() {
                return kw.isEmpty() ? obatDAO.getAll() : obatDAO.search(kw);
            }
            @Override protected void done() {
                try {
                    currentData = get();
                    tableModel.setRowCount(0);
                    for (Obat o : currentData) {
                        tableModel.addRow(new Object[]{
                            o.getKodeObat(), o.getNamaObat(),
                            FormatUtil.formatCurrency(o.getHarga()),
                            o.getStok(), o.getDeskripsi(), "actions"
                        });
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        w.execute();
    }

    void showFormObat(Obat existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Data Obat / Stok Obat" : "Tambah Obat Baru", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_APP);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel heading = new JLabel(isEdit ? "Edit Data Obat / Stok Obat" : "Tambah Obat Baru");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(heading);
        content.add(Box.createVerticalStrut(16));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(16,16,16,16)
        ));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6); g.anchor = GridBagConstraints.WEST;

        JTextField tfKode = makeField(isEdit ? existing.getKodeObat() : "");
        JTextField tfNama = makeField(isEdit ? existing.getNamaObat() : "");
        JTextField tfHarga = makeField(isEdit ? String.valueOf((int)existing.getHarga()) : "");
        JTextField tfStok = makeField(isEdit ? String.valueOf(existing.getStok()) : "");
        JTextArea taDesc = new JTextArea(isEdit ? existing.getDeskripsi() : "", 3, 20);
        taDesc.setFont(Theme.FONT_BODY);
        taDesc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(6,8,6,8)
        ));
        taDesc.setLineWrap(true);

        if (isEdit) tfKode.setEditable(false);

        addRow(form, g, 0, "Kode Obat",  tfKode);
        addRow(form, g, 1, "Nama Obat",  tfNama);
        addRow(form, g, 2, "Harga Baru", tfHarga);
        addRow(form, g, 3, "Stok",       tfStok);
        g.gridx=0; g.gridy=4; form.add(makeLabel("Deskripsi"), g);
        g.gridx=1; g.gridy=4; g.fill=GridBagConstraints.BOTH; g.weightx=1;
        form.add(new JScrollPane(taDesc), g);
        g.fill=GridBagConstraints.NONE; g.weightx=0;

        content.add(form);
        content.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnBatal = new JButton("Batal");
        Theme.styleSecondaryButton(btnBatal);
        btnBatal.addActionListener(e -> dlg.dispose());

        String saveLabel = isEdit ? "Perbarui Data" : "Simpan Obat";
        JButton btnSimpan = new JButton(saveLabel);
        Theme.stylePrimaryButton(btnSimpan);
        btnSimpan.addActionListener(e -> {
            String kode  = tfKode.getText().trim();
            String nama  = tfNama.getText().trim();
            String harga = tfHarga.getText().trim();
            String stok  = tfStok.getText().trim();
            String desc  = taDesc.getText().trim();

            if (kode.isEmpty() || nama.isEmpty() || harga.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Kode, Nama, dan Harga wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double h = Double.parseDouble(harga);
                int    s = stok.isEmpty() ? 0 : Integer.parseInt(stok);
                Obat o = new Obat(kode, nama, h, s, desc);
                boolean ok = isEdit ? obatDAO.update(o) : obatDAO.insert(o);
                if (ok) {
                    JOptionPane.showMessageDialog(dlg,
                        (isEdit ? "Data obat berhasil diperbarui." : "Obat baru berhasil ditambahkan."),
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Harga dan Stok harus berupa angka.", "Validasi", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnRow.add(btnBatal); btnRow.add(btnSimpan);
        content.add(btnRow);

        dlg.setContentPane(new JScrollPane(content));
        dlg.setVisible(true);
    }

    void editObat(int row) {
        if (currentData == null || row >= currentData.size()) return;
        showFormObat(currentData.get(row));
    }

    void deleteObat(int row) {
        if (currentData == null || row >= currentData.size()) return;
        Obat obat = currentData.get(row);

        int opt = JOptionPane.showConfirmDialog(this,
            "Hapus obat \"" + obat.getNamaObat() + "\" (" + obat.getKodeObat() + ")?\nTindakan ini tidak dapat dibatalkan.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            boolean ok = obatDAO.delete(obat.getKodeObat());
            if (ok) { JOptionPane.showMessageDialog(this, "Obat berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE); refresh(); }
            else      JOptionPane.showMessageDialog(this, "Gagal menghapus obat (mungkin masih dipakai di nota).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helpers
    private JTextField makeField(String val) {
        JTextField tf = new JTextField(val, 18);
        Theme.styleTextField(tf);
        tf.setPreferredSize(new Dimension(200, 32));
        return tf;
    }
    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(Theme.FONT_BODY); l.setForeground(Theme.TEXT_SECONDARY); return l;
    }
    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JTextField tf) {
        g.gridx=0; g.gridy=row; g.fill=GridBagConstraints.NONE;  p.add(makeLabel(lbl), g);
        g.gridx=1; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1; p.add(tf, g);
        g.weightx=0;
    }

    // Renderers/Editors
    static class Striped extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            if (!sel) comp.setBackground(r%2==0 ? Theme.BG_CARD : Theme.BG_TABLE_ALT);
            comp.setForeground(Theme.TEXT_PRIMARY);
            ((JComponent)comp).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
            return comp;
        }
    }

    static class StokActionRenderer extends JPanel implements TableCellRenderer {
        StokActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER,3,5)); setOpaque(true);
            add(iconBtn("✏",Theme.WARNING)); add(iconBtn("🗑",Theme.DANGER));
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

    static class StokActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private int currentRow;
        StokActionEditor(StokPanel parent) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER,3,5));
            panel.setBackground(Theme.BG_CARD);
            JButton btnEdit = iconBtn("✏",Theme.WARNING);
            JButton btnDel  = iconBtn("🗑",Theme.DANGER);
            btnEdit.addActionListener(e -> { fireEditingStopped(); parent.editObat(currentRow); });
            btnDel.addActionListener (e -> { fireEditingStopped(); parent.deleteObat(currentRow); });
            panel.add(btnEdit); panel.add(btnDel);
        }
        private JButton iconBtn(String t, Color bg) {
            JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI Emoji",Font.PLAIN,11)); b.setFocusPainted(false);
            b.setBorderPainted(false); b.setPreferredSize(new Dimension(28,26)); b.setOpaque(true); return b;
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) { currentRow=r; return panel; }
        @Override public Object getCellEditorValue() { return ""; }
    }
}
