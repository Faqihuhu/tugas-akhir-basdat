package mypharmacist.ui;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import mypharmacist.database.*;
import mypharmacist.model.*;
import mypharmacist.util.*;

public class PenjualanPanel extends JPanel {

    private final MainFrame parent;

    private JTextField  tfNoNota, tfTanggal, tfNamaPelanggan;
    private JComboBox<String> cbKasir, cbObat;
    private JTextField  tfHarga, tfQty;
    private JLabel      lblTotal;

    private DefaultTableModel itemModel;
    private JTable            itemTable;

    private final NotaDAO notaDAO = new NotaDAO();
    private final ObatDAO obatDAO = new ObatDAO();
    private Map<Integer, String>  kasirMap = new LinkedHashMap<>();
    private List<Obat>            obatList = new ArrayList<>();
    private final List<DetailNota> cartItems = new ArrayList<>();

    public PenjualanPanel(MainFrame parent) {
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

        JLabel pageTitle = new JLabel("Tambah Nota Penjualan");
        pageTitle.setFont(Theme.FONT_TITLE);
        pageTitle.setForeground(Theme.TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(pageTitle);
        body.add(Box.createVerticalStrut(16));

        JPanel card = Theme.makeCard();
        card.setLayout(new GridBagLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        gbc.weightx = 0.5; gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(buildInfoNotaPanel(), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.VERTICAL;
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setForeground(Theme.BORDER);
        card.add(sep, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.5; gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(buildDetailObatPanel(), gbc);

        body.add(card);
        body.add(Box.createVerticalStrut(16));

        body.add(buildItemListPanel());
        body.add(Box.createVerticalStrut(16));

        body.add(buildActionRow());
        body.add(Box.createVerticalGlue());

        return body;
    }

    private JPanel buildInfoNotaPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        JLabel secTitle = new JLabel("Informasi Nota");
        secTitle.setFont(Theme.FONT_HEADING);
        secTitle.setForeground(Theme.TEXT_PRIMARY);
        g.gridx=0; g.gridy=0; g.gridwidth=2; p.add(secTitle, g);

        g.gridwidth=1;
        addField(p, g, 1, "No Nota",   tfNoNota = makeField(""));
        addField(p, g, 2, "Tanggal",   tfTanggal = makeField(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date())));
        addField(p, g, 3, "Nama Pelanggan", tfNamaPelanggan = makeField(""));
        g.gridx=0; g.gridy=4; p.add(makeLabel("Nama Kasir"), g);
        cbKasir = new JComboBox<>();
        Theme.styleComboBox(cbKasir);
        cbKasir.setPreferredSize(new Dimension(200, 32));
        g.gridx=1; p.add(cbKasir, g);

        return p;
    }

    private JPanel buildDetailObatPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        JLabel secTitle = new JLabel("Detail Obat");
        secTitle.setFont(Theme.FONT_HEADING);
        secTitle.setForeground(Theme.TEXT_PRIMARY);
        g.gridx=0; g.gridy=0; g.gridwidth=2; p.add(secTitle, g);
        g.gridwidth=1;

        g.gridx=0; g.gridy=1; p.add(makeLabel("Nama Obat"), g);
        cbObat = new JComboBox<>();
        Theme.styleComboBox(cbObat);
        cbObat.setPreferredSize(new Dimension(200, 32));
        cbObat.addActionListener(e -> onObatSelected());
        g.gridx=1; p.add(cbObat, g);

        addField(p, g, 2, "Harga/Unit", tfHarga = makeField("0"));
        addField(p, g, 3, "Quantity",   tfQty   = makeField("1"));

        g.gridx=0; g.gridy=4; g.gridwidth=2;
        JButton btnAdd = new JButton("+ Tambah ke Daftar");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnAdd.addActionListener(e -> addToCart());
        p.add(btnAdd, g);

        return p;
    }

    private JPanel buildItemListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lbl = new JLabel("Daftar Item");
        lbl.setFont(Theme.FONT_HEADING);
        lbl.setForeground(Theme.TEXT_PRIMARY);
        panel.add(lbl, BorderLayout.NORTH);

        String[] cols = {"No", "Kode Obat", "Nama Obat", "Harga/Unit", "Qty", "Subtotal", "Aksi"};
        itemModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
            @Override public Class<?> getColumnClass(int c) { return c==6 ? JButton.class : Object.class; }
        };
        itemTable = new JTable(itemModel);
        itemTable.setFont(Theme.FONT_TABLE);
        itemTable.setRowHeight(36);
        itemTable.getTableHeader().setFont(Theme.FONT_SUB);
        itemTable.getTableHeader().setBackground(Theme.BG_APP);
        itemTable.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        itemTable.setGridColor(Theme.BORDER);
        itemTable.setShowHorizontalLines(true);
        itemTable.setShowVerticalLines(false);
        itemTable.setSelectionBackground(new Color(235,245,255));
        itemTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("🗑", Theme.DANGER));
        itemTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), "🗑", Theme.DANGER, row -> removeCartItem(row)));
        itemTable.getColumnModel().getColumn(6).setMaxWidth(60);

        JScrollPane sc = new JScrollPane(itemTable);
        sc.setBorder(BorderFactory.createMatteBorder(1,0,0,0, Theme.BORDER));
        panel.add(sc, BorderLayout.CENTER);

        JPanel totalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalRow.setOpaque(false);
        JLabel totalLbl = new JLabel("Total Pembayaran: ");
        totalLbl.setFont(Theme.FONT_HEADING);
        totalLbl.setForeground(Theme.TEXT_PRIMARY);
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(Theme.PRIMARY);
        totalRow.add(totalLbl);
        totalRow.add(lblTotal);
        panel.add(totalRow, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReset = new JButton("Reset Form");
        Theme.styleSecondaryButton(btnReset);
        btnReset.addActionListener(e -> resetForm());

        JButton btnSimpan = new JButton("💾 Simpan Nota");
        Theme.stylePrimaryButton(btnSimpan);
        btnSimpan.addActionListener(e -> simpanNota());

        row.add(btnReset);
        row.add(btnSimpan);
        return row;
    }

    public void refresh() {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                kasirMap = notaDAO.getAllKasir();
                obatList = obatDAO.getAll();
                return null;
            }
            @Override protected void done() {
                cbKasir.removeAllItems();
                kasirMap.values().forEach(cbKasir::addItem);

                cbObat.removeAllItems();
                obatList.forEach(o -> cbObat.addItem(o.getNamaObat() + " [" + o.getKodeObat() + "]"));

                if (tfNoNota.getText().isEmpty()) {
                    tfNoNota.setText(notaDAO.generateNoNota());
                }
                onObatSelected();
            }
        };
        w.execute();
    }

    private void onObatSelected() {
        int idx = cbObat.getSelectedIndex();
        if (idx >= 0 && idx < obatList.size()) {
            tfHarga.setText(String.valueOf((int)obatList.get(idx).getHarga()));
        }
    }

    private void addToCart() {
        int idx = cbObat.getSelectedIndex();
        if (idx < 0 || idx >= obatList.size()) return;

        try {
            double harga = Double.parseDouble(tfHarga.getText().trim());
            int    qty   = Integer.parseInt(tfQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();

            Obat   obat = obatList.get(idx);
            DetailNota item = new DetailNota(obat.getKodeObat(), obat.getNamaObat(), harga, qty);
            cartItems.add(item);
            refreshTable();
            tfQty.setText("1");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan Qty harus berupa angka positif.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTable() {
        itemModel.setRowCount(0);
        double total = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            DetailNota d = cartItems.get(i);
            itemModel.addRow(new Object[]{
                i+1,
                d.getKodeObat(),
                d.getNamaObat(),
                FormatUtil.formatCurrency(d.getHargaUnit()),
                d.getQty(),
                FormatUtil.formatCurrency(d.getSubtotal()),
                "🗑"
            });
            total += d.getSubtotal();
        }
        lblTotal.setText(FormatUtil.formatCurrency(total));
    }

    private void removeCartItem(int row) {
        if (row >= 0 && row < cartItems.size()) {
            cartItems.remove(row);
            refreshTable();
        }
    }

    private void simpanNota() {
        String noNota = tfNoNota.getText().trim();
        String pelanggan = tfNamaPelanggan.getText().trim();
        int kasirIdx = cbKasir.getSelectedIndex();

        if (noNota.isEmpty()) { JOptionPane.showMessageDialog(this, "No Nota tidak boleh kosong."); return; }
        if (cartItems.isEmpty()) { JOptionPane.showMessageDialog(this, "Daftar item masih kosong."); return; }
        if (kasirIdx < 0) { JOptionPane.showMessageDialog(this, "Pilih kasir terlebih dahulu."); return; }

        NotaPenjualan nota = new NotaPenjualan();
        nota.setNoNota(noNota);
        nota.setTanggal(new Date());
        nota.setNamaPelanggan(pelanggan.isEmpty() ? "Umum" : pelanggan);
        nota.setIdKasir((int) kasirMap.keySet().toArray()[kasirIdx]);
        nota.setDetails(new ArrayList<>(cartItems));
        nota.recalcTotal();

        boolean ok = notaDAO.simpanNota(nota);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Nota " + noNota + " berhasil disimpan!\nTotal: " + FormatUtil.formatCurrency(nota.getTotalPembayaran()),
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan nota. Periksa koneksi database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        cartItems.clear();
        itemModel.setRowCount(0);
        lblTotal.setText("Rp 0");
        tfNamaPelanggan.setText("");
        tfQty.setText("1");
        tfNoNota.setText(notaDAO.generateNoNota());
    }

    private JTextField makeField(String val) {
        JTextField tf = new JTextField(val, 18);
        Theme.styleTextField(tf);
        tf.setPreferredSize(new Dimension(200, 32));
        return tf;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }

    private void addField(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridx=0; g.gridy=row; g.gridwidth=1; p.add(makeLabel(label), g);
        g.gridx=1; p.add(field, g);
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer(String text, Color bg) {
            setText(text); setBackground(bg); setForeground(Color.WHITE);
            setFont(Theme.FONT_SMALL); setFocusPainted(false); setBorderPainted(false); setOpaque(true);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) { return this; }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private final JButton btn;
        private final java.util.function.IntConsumer action;
        private int currentRow;
        ButtonEditor(JCheckBox cb, String text, Color bg, java.util.function.IntConsumer action) {
            super(cb); this.action = action;
            btn = new JButton(text); btn.setBackground(bg); btn.setForeground(Color.WHITE);
            btn.setFont(Theme.FONT_SMALL); btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
            btn.addActionListener(e -> { fireEditingStopped(); action.accept(currentRow); });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            currentRow = r; return btn;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }
}
