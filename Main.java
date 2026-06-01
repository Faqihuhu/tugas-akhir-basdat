package mypharmacist;

import javax.swing.*;
import mypharmacist.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        UIManager.put("Button.arc",          10);
        UIManager.put("Component.arc",       8);
        UIManager.put("TextComponent.arc",   6);
        UIManager.put("ScrollBar.width",     10);
        UIManager.put("ScrollBar.thumbArc",  999);
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines",   false);
        UIManager.put("OptionPane.messageFont",  new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont",   new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        UIManager.put("ToolTip.font",            new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        UIManager.put("ComboBox.font",           new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        UIManager.put("TextField.font",          new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        UIManager.put("Label.font",              new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
