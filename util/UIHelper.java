package apotekk24.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.NumberFormat;
import java.util.Locale;

public class UIHelper {

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(UIConstants.PRIMARY_DARK);
                else if (getModel().isRollover()) g2.setColor(UIConstants.PRIMARY_LIGHT);
                else g2.setColor(UIConstants.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(UIConstants.fontBold(14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(UIConstants.fontBold(14));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? UIConstants.BG_CARD2 : UIConstants.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(UIConstants.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(UIConstants.TEXT_LIGHT);
                g2.setFont(UIConstants.fontBold(13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(UIConstants.fontBold(13));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? UIConstants.DANGER.darker() : UIConstants.DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(UIConstants.fontBold(13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(UIConstants.TEXT_GRAY);
                    g2.setFont(UIConstants.fontRegular(13));
                    g2.drawString(placeholder, 10, getHeight()/2 + 5);
                }
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? UIConstants.PRIMARY : UIConstants.BORDER);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2 : 1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setBackground(UIConstants.BG_CARD2);
        field.setForeground(UIConstants.TEXT_WHITE);
        field.setCaretColor(UIConstants.PRIMARY);
        field.setFont(UIConstants.fontRegular(13));
        field.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        field.setPreferredSize(new Dimension(200, 38));
        return field;
    }

    public static JLabel createLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.fontBold(size));
        lbl.setForeground(color);
        return lbl;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
                g2.setColor(UIConstants.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return panel;
    }

    public static JScrollPane styledScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = UIConstants.PRIMARY;
                trackColor = UIConstants.BG_CARD2;
            }
            @Override protected JButton createDecreaseButton(int o) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
            }
        });
        return sp;
    }

    public static String formatRupiah(double amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp " + nf.format((long)amount);
    }

    public static JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UIConstants.fontRegular(13));
        combo.setBackground(UIConstants.BG_CARD2);
        combo.setForeground(UIConstants.TEXT_WHITE);
        combo.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        combo.setPreferredSize(new Dimension(200, 38));
        return combo;
    }

    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(UIConstants.fontRegular(13));
        area.setBackground(UIConstants.BG_CARD2);
        area.setForeground(UIConstants.TEXT_WHITE);
        area.setCaretColor(UIConstants.PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return area;
    }
}
