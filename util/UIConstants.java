package apotekk24.util;

import java.awt.*;

public class UIConstants {
    public static final Color PRIMARY       = new Color(0xE8380D);   
    public static final Color PRIMARY_DARK  = new Color(0xBF2D09);
    public static final Color PRIMARY_LIGHT = new Color(0xFF5733);
    public static final Color SECONDARY     = new Color(0x1A1A2E);   
    public static final Color ACCENT        = new Color(0xFFBE00);   
    public static final Color BG_DARK       = new Color(0x12121F);
    public static final Color BG_CARD       = new Color(0x1E1E30);
    public static final Color BG_CARD2      = new Color(0x252540);
    public static final Color BG_SIDEBAR    = new Color(0x0F0F1E);
    public static final Color TEXT_WHITE    = new Color(0xFFFFFF);
    public static final Color TEXT_GRAY     = new Color(0xA0A0C0);
    public static final Color TEXT_LIGHT    = new Color(0xD0D0E8);
    public static final Color BORDER        = new Color(0x2E2E4E);
    public static final Color SUCCESS       = new Color(0x00C896);
    public static final Color WARNING       = new Color(0xFFBE00);
    public static final Color DANGER        = new Color(0xFF4757);
    public static final Color TABLE_ROW_ODD  = new Color(0x1A1A2E);
    public static final Color TABLE_ROW_EVEN = new Color(0x1E1E35);
    public static final Color TABLE_HEADER   = new Color(0xE8380D);
    public static final Color TABLE_SELECT   = new Color(0x3A1A50);
    public static Font fontTitle(float size)  { return new Font("Segoe UI", Font.BOLD, (int)size); }
    public static Font fontBold(float size)   { return new Font("Segoe UI", Font.BOLD, (int)size); }
    public static Font fontRegular(float size){ return new Font("Segoe UI", Font.PLAIN, (int)size); }
    public static Font fontMono(float size)   { return new Font("Consolas", Font.PLAIN, (int)size); }
    public static final int SIDEBAR_WIDTH = 320;
    public static final int HEADER_HEIGHT = 60;
    public static final int CORNER_RADIUS = 12;
}
