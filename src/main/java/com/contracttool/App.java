/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool;

import com.contracttool.ui.MainFrame;
import com.contracttool.ui.UiTheme;
import javax.swing.SwingUtilities;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        // Tạo F:\TOOL - HOP DONG\ContractToolData + 3 file JSON nếu chưa có (hoặc local nếu chưa map F:).
        try {
            AppPaths.ensureDataFiles();
        }
        catch (Exception exception) {
            // empty
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UiTheme.install();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (!AppPaths.usingSharedDrive()) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "M\u00e1y n\u00e0y ch\u01b0a map \u1ed5 F:.\n\n"
                                + AppPaths.sharedDriveStatus()
                                + "\n\nData \u0111ang l\u01b0u T\u1ea0M tr\u00ean C: \u2014 kh\u00f4ng d\u00f9ng chung \u0111\u01b0\u1ee3c.\n"
                                + "Map \u1ed5 m\u1ea1ng th\u00e0nh ch\u1eef F: r\u1ed3i m\u1edf l\u1ea1i TOOL.",
                        "CH\u01afA C\u00d3 \u1ed4 F:",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

