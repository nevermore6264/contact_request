/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public final class AppPaths {
    /** Ổ data chung công ty: F:\TOOL - HOP DONG\ContractToolData\ */
    private static final Path SHARED_DIR = Path.of("F:\\", "TOOL - HOP DONG", "ContractToolData");
    private static final Path LOCAL_DIR = Path.of(System.getProperty("user.home"), "ContractToolData");
    private static final String PRODUCT_FILE = "hang_hoa_bac_nam.json";
    private static final String CUSTOMER_FILE = "khach_hang.json";
    private static final String BRAND_COLOR_FILE = "bang_mau.json";

    private static final String EMPTY_PRODUCT_JSON =
            "{\n  \"prices_nam\": [],\n  \"prices_bac\": [],\n  \"content\": [],\n  \"unit\": [],\n  \"codes\": []\n}\n";
    private static final String EMPTY_OBJECT_JSON = "{}\n";

    private AppPaths() {
    }

    /** Tạo thư mục + 3 file JSON nếu chưa có (ưu tiên F:\). Gọi khi mở tool. */
    public static void ensureDataFiles() {
        AppPaths.savedProducts();
        AppPaths.customerHistory();
        AppPaths.brandColors();
    }

    /** Đường dẫn chuẩn công ty (luôn là F:..., kể cả khi chưa map). */
    public static Path sharedDir() {
        return SHARED_DIR;
    }

    /** Thư mục data đang dùng thực tế (F: nếu có; không thì local tạm trên C:). */
    public static Path dataDir() {
        if (AppPaths.trySharedDir() != null) {
            return SHARED_DIR;
        }
        try {
            Files.createDirectories(LOCAL_DIR, new FileAttribute[0]);
        }
        catch (Exception exception) {
            // empty
        }
        return LOCAL_DIR;
    }

    public static boolean usingSharedDrive() {
        return AppPaths.trySharedDir() != null;
    }

    /** Lý do không dùng được F: — để hiện trên UI. */
    public static String sharedDriveStatus() {
        try {
            Path drive = Path.of("F:\\");
            if (!Files.exists(drive)) {
                return "M\u00e1y n\u00e0y CH\u01afA C\u00d3 \u1ed5 F: (ch\u01b0a map). \u0110ang l\u01b0u t\u1ea1m tr\u00ean C:.\n"
                        + "H\u00e3y map \u1ed5 m\u1ea1ng th\u00e0nh F: r\u1ed3i t\u1ea1o th\u01b0 m\u1ee5c:\n"
                        + SHARED_DIR;
            }
            Files.createDirectories(SHARED_DIR, new FileAttribute[0]);
            if (!Files.isWritable(SHARED_DIR)) {
                return "C\u00f3 \u1ed5 F: nh\u01b0ng KH\u00d4NG GHI \u0110\u01af\u1ee2C:\n" + SHARED_DIR;
            }
            return "\u0110ang d\u00f9ng \u1ed5 chung: " + SHARED_DIR;
        }
        catch (Exception ex) {
            return "Kh\u00f4ng d\u00f9ng \u0111\u01b0\u1ee3c \u1ed5 F: (" + ex.getMessage() + ").\nC\u1ea7n: " + SHARED_DIR;
        }
    }

    /** null nếu chưa map / không ghi được F: */
    private static Path trySharedDir() {
        try {
            Path drive = Path.of("F:\\");
            // Windows: kiểm tra root + File để chắc hơn Files.exists
            java.io.File fDrive = new java.io.File("F:\\");
            if (!fDrive.exists() && !Files.exists(drive)) {
                return null;
            }
            Files.createDirectories(SHARED_DIR, new FileAttribute[0]);
            if (Files.isDirectory(SHARED_DIR) && Files.isWritable(SHARED_DIR)) {
                return SHARED_DIR;
            }
        }
        catch (Exception exception) {
            // empty
        }
        return null;
    }

    /** Xóa bản bang_mau / brand_colors cũ trên C: — tránh migrate đè lại sau khi đã xóa trên F:. */
    public static void clearLocalBrandColorLegacy() {
        if (!AppPaths.usingSharedDrive()) {
            return;
        }
        Path[] legacy = new Path[]{
                LOCAL_DIR.resolve(BRAND_COLOR_FILE),
                LOCAL_DIR.resolve("brand_colors.json")
        };
        for (Path p : legacy) {
            try {
                Files.deleteIfExists(p);
            }
            catch (Exception ignored) {
                // empty
            }
        }
    }

    /** File JSON hàng hóa Bắc/Nam (chung công ty). */
    public static Path savedProducts() {
        Path target = AppPaths.dataDir().resolve(PRODUCT_FILE);
        AppPaths.migrateIfMissing(target,
                LOCAL_DIR.resolve("saved_data.txt"),
                LOCAL_DIR.resolve(PRODUCT_FILE),
                SHARED_DIR.resolve("saved_data.txt"),
                SHARED_DIR.resolve(PRODUCT_FILE));
        AppPaths.createIfMissing(target, EMPTY_PRODUCT_JSON);
        return target;
    }

    /** File JSON khách hàng (chung công ty trên ổ F). */
    public static Path customerHistory() {
        Path target = AppPaths.dataDir().resolve(CUSTOMER_FILE);
        AppPaths.migrateIfMissing(target,
                LOCAL_DIR.resolve("customer_history.json"),
                LOCAL_DIR.resolve(CUSTOMER_FILE),
                SHARED_DIR.resolve("customer_history.json"),
                SHARED_DIR.resolve(CUSTOMER_FILE));
        AppPaths.createIfMissing(target, EMPTY_OBJECT_JSON);
        return target;
    }

    /** File JSON bảng màu hãng (chung công ty trên ổ F). */
    public static Path brandColors() {
        Path target = AppPaths.dataDir().resolve(BRAND_COLOR_FILE);
        AppPaths.migrateIfMissing(target,
                LOCAL_DIR.resolve("brand_colors.json"),
                LOCAL_DIR.resolve(BRAND_COLOR_FILE),
                SHARED_DIR.resolve("brand_colors.json"),
                SHARED_DIR.resolve(BRAND_COLOR_FILE));
        AppPaths.createIfMissing(target, EMPTY_OBJECT_JSON);
        return target;
    }

    private static void createIfMissing(Path target, String emptyJson) {
        if (Files.exists(target)) {
            return;
        }
        try {
            Path parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            Files.writeString(target, emptyJson, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        }
        catch (Exception ignored) {
            // không tạo được (ổ F khóa / quyền) — bỏ qua, lưu lúc SAVE sẽ thử lại
        }
    }

    private static void migrateIfMissing(Path target, Path... legacySources) {
        if (Files.exists(target)) {
            return;
        }
        for (Path old : legacySources) {
            try {
                if (old == null || !Files.exists(old) || old.equals(target)) {
                    continue;
                }
                Path parent = target.getParent();
                if (parent != null) {
                    Files.createDirectories(parent, new FileAttribute[0]);
                }
                Files.copy(old, target, StandardCopyOption.REPLACE_EXISTING);
                return;
            }
            catch (Exception ignored) {
                // thử nguồn tiếp theo
            }
        }
    }
}
