package mypharmacist.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import mypharmacist.database.DatabaseConnection;
import mypharmacist.util.Theme;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JButton btnDashboard, btnPenjualan, btnStok, btnKasir, btnLaporan;
    private JButton activeBtn;

    // Pages
    private DashboardPanel  dashboardPanel;
    private PenjualanPanel  penjualanPanel;
    private RiwayatPanel    riwayatPanel;
    private StokPanel       stokPanel;

    public MainFrame() {
        setTitle("MyPharmacist – Sistem Manajemen Apotek");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setBackground(Theme.BG_APP);

        if (!DatabaseConnection.testConnection()) {
            int opt = JOptionPane.showConfirmDialog(null,
                "Tidak dapat terhubung ke database.\nAplikasi akan berjalan tanpa koneksi database.\n\nLanjutkan?",
                "Peringatan Koneksi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt != JOptionPane.YES_OPTION) System.exit(0);
        }

        initUI();
        showPage("dashboard");
    }

    private void initUI() {
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG_APP);
        body.add(buildSidebar(), BorderLayout.WEST);
        body.add(buildContent(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        header.setPreferredSize(new Dimension(0, 58));

        // Logo area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoPanel.setOpaque(false);

        // Red pill logo badge
        JLabel badge = new JLabel("Rx") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Rx", (getWidth()-fm.stringWidth("Rx"))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(34, 34));
        badge.setOpaque(false);

        JLabel title = new JLabel("MyPharmacist");
        title.setFont(Theme.FONT_LOGO);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Sistem Manajemen Apotek");
        subtitle.setFont(Theme.FONT_SMALL);
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        titleStack.add(title);
        titleStack.add(subtitle);

        logoPanel.add(badge);
        logoPanel.add(titleStack);
        header.add(logoPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel connStatus = new JLabel();
        if (DatabaseConnection.testConnection()) {
            connStatus.setText("● Database Terhubung");
            connStatus.setForeground(Theme.SUCCESS);
        } else {
            connStatus.setText("● Database Offline");
            connStatus.setForeground(Theme.DANGER);
        }
        connStatus.setFont(Theme.FONT_SMALL);

        JLabel userIcon = new JLabel("👤 Admin");
        userIcon.setFont(Theme.FONT_SMALL);
        userIcon.setForeground(Theme.TEXT_SECONDARY);

        rightPanel.add(connStatus);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(userIcon);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        btnDashboard = makeSidebarButton("🏠  Dashboard",  "dashboard");
        btnPenjualan = makeSidebarButton("🧾  Penjualan",  "penjualan");
        btnStok      = makeSidebarButton("💊  Stok Obat",  "stok");
        btnKasir     = makeSidebarButton("💰  Riwayat",    "riwayat");
        btnLaporan   = makeSidebarButton("📊  Laporan",    "laporan");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(btnPenjualan);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(btnStok);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(btnKasir);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(btnLaporan);
        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("v1.0.0  •  MyPharmacist");
        version.setFont(Theme.FONT_SMALL);
        version.setForeground(Theme.TEXT_SECONDARY);
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        sidebar.add(version);

        return sidebar;
    }

    private JButton makeSidebarButton(String text, String page) {
        JButton btn = new JButton(text) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                boolean isActive = (activeBtn == this);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isActive) {
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRect(0, 0, 4, getHeight());
                    g2.setColor(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 30));
                    g2.fillRect(4, 0, getWidth()-4, getHeight());
                } else if (hovered) {
                    g2.setColor(Theme.BG_SIDEBAR_H);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(Theme.TEXT_SIDEBAR);
        btn.setBackground(new Color(0,0,0,0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
        btn.addActionListener(e -> showPage(page));
        return btn;
    }

    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BG_APP);

        dashboardPanel = new DashboardPanel(this);
        penjualanPanel = new PenjualanPanel(this);
        riwayatPanel   = new RiwayatPanel(this);
        stokPanel      = new StokPanel(this);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(penjualanPanel, "penjualan");
        contentPanel.add(riwayatPanel,   "riwayat");
        contentPanel.add(stokPanel,      "stok");
        contentPanel.add(buildLaunchPage("📊 Laporan", "Fitur laporan akan segera hadir."), "laporan");

        return contentPanel;
    }

    private JPanel buildLaunchPage(String title, String desc) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BG_APP);
        JLabel lbl = new JLabel("<html><center><h2>" + title + "</h2><p>" + desc + "</p></center></html>");
        lbl.setFont(Theme.FONT_HEADING);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        p.add(lbl);
        return p;
    }

    public void showPage(String page) {
        cardLayout.show(contentPanel, page);
        updateActiveButton(page);
        if ("riwayat".equals(page))   riwayatPanel.refresh();
        if ("stok".equals(page))      stokPanel.refresh();
        if ("penjualan".equals(page)) penjualanPanel.refresh();
        if ("dashboard".equals(page)) dashboardPanel.refresh();
    }

    private void updateActiveButton(String page) {
        activeBtn = switch (page) {
            case "penjualan" -> btnPenjualan;
            case "stok"      -> btnStok;
            case "riwayat"   -> btnKasir;
            case "laporan"   -> btnLaporan;
            default          -> btnDashboard;
        };

        for (JButton b : new JButton[]{btnDashboard, btnPenjualan, btnStok, btnKasir, btnLaporan}) {
            b.setForeground(b == activeBtn ? Color.WHITE : Theme.TEXT_SIDEBAR);
        }
        repaint();
    }
}
