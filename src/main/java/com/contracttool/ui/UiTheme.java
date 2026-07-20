/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.formdev.flatlaf.FlatLightLaf
 */
package com.contracttool.ui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

public final class UiTheme {
    public static final Color BG = new Color(15659766);
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(14870768);
    public static final Color TEXT = new Color(988970);
    public static final Color MUTED = new Color(6583435);
    public static final Color BTN_TEAL = new Color(1013358);
    public static final Color BTN_DONE = new Color(1409085);
    public static final Color NAM_RED = new Color(14427686);
    public static final Color BAC_GREEN = new Color(1409085);
    public static final Color ROW_SELECTED = new Color(14412542);
    public static final Color[] HEADER_COLORS = new Color[]{new Color(4674921), new Color(2450411), new Color(14427686), new Color(366185), new Color(14251782), new Color(8141549), new Color(14362487)};
    public static final Font FONT = new Font("Segoe UI", 0, 13);
    public static final Font FONT_BOLD = FONT.deriveFont(1);
    public static final Font FONT_BTN = new Font("Segoe UI", 1, 16);
    public static final Font FONT_SIZE20 = new Font("Segoe UI", 1, 20);
    public static final Font FONT_TOTAL = new Font("Segoe UI", 1, 20);

    private UiTheme() {
    }

    public static void install() throws Exception {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 16);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("CheckBox.icon.style", "outlined");
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", true);
        UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
        UIManager.put("Table.selectionBackground", ROW_SELECTED);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.selectionInactiveBackground", ROW_SELECTED);
        UIManager.put("Table.selectionInactiveForeground", TEXT);
        UIManager.put("defaultFont", FONT);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1, true), BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(UiTheme.cardBorder());
        p.setOpaque(true);
        return p;
    }

    public static JButton actionButton(String text) {
        return UiTheme.actionButton(text, BTN_TEAL);
    }

    public static JButton actionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(12));
        b.setFont(FONT_BTN);
        b.setMargin(new Insets(10, 16, 10, 16));
        b.putClientProperty("JButton.buttonType", "roundRect");
        return b;
    }
}

