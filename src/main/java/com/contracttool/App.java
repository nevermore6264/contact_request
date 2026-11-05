/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool;

import com.contracttool.ui.MainFrame;
import com.contracttool.ui.UiTheme;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.swing.SwingUtilities;

public final class App {
    /** Giữ khóa đến khi tắt app. File nằm trên máy này, không khóa cả công ty trên ổ chung. */
    private static FileChannel instanceChannel;
    private static FileLock instanceLock;

    private App() {
    }

    public static void main(String[] args) {
        if (!App.acquireSingleInstance()) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Ph\u1ea7n m\u1ec1m \u0111ang m\u1edf.\nCh\u1ec9 ch\u1ea1y m\u1ed9t c\u1eeda s\u1ed5 \u2014 h\u00e3y d\u00f9ng c\u1eeda s\u1ed5 \u0111\u00e3 m\u1edf.",
                    "TOOL - HOP DONG",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        // Tạo TOOL - HOP DONG\ContractToolData trên F: hoặc Z: + 3 file JSON nếu chưa có (không thì local).
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
                        "M\u00e1y n\u00e0y ch\u01b0a map \u1ed5 F: ho\u1eb7c Z:.\n\n"
                                + AppPaths.sharedDriveStatus()
                                + "\n\nData \u0111ang l\u01b0u T\u1ea0M tr\u00ean C: \u2014 kh\u00f4ng d\u00f9ng chung \u0111\u01b0\u1ee3c.\n"
                                + "Map \u1ed5 m\u1ea1ng th\u00e0nh ch\u1eef F: ho\u1eb7c Z: r\u1ed3i m\u1edf l\u1ea1i TOOL.",
                        "CH\u01afA C\u00d3 \u1ed4 CHUNG",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static boolean acquireSingleInstance() {
        try {
            Path dir = Path.of(System.getProperty("user.home"), "ContractToolData");
            Files.createDirectories(dir);
            App.instanceChannel = FileChannel.open(dir.resolve("app.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            App.instanceLock = App.instanceChannel.tryLock();
            return App.instanceLock != null;
        }
        catch (Exception exception) {
            return true;
        }
    }
}

