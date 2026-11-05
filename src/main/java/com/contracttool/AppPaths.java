/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public final class AppPaths {
    /**
     * Data nằm cạnh app đang chạy (F: hay Z: đều cùng một thư mục share).
     * Chỉ khi không thấy file cạnh app mới xét F: rồi Z:.
     */
    private static final Path[] SHARED_CANDIDATES = new Path[]{
            Path.of("F:\\", "TOOL - HOP DONG", "ContractToolData"),
            Path.of("Z:\\", "TOOL - HOP DONG", "ContractToolData")
    };
    private static volatile Path cachedShared;
    private static final Path LOCAL_DIR = Path.of(System.getProperty("user.home"), "ContractToolData");
    private static final String PRODUCT_FILE = "hang_hoa_bac_nam.json";
    private static final String CUSTOMER_FILE = "khach_hang.json";
    private static final String BRAND_COLOR_FILE = "bang_mau.json";

    private static final String EMPTY_PRODUCT_JSON =
            "{\n  \"prices_nam\": [],\n  \"prices_bac\": [],\n  \"content\": [],\n  \"unit\": [],\n  \"codes\": []\n}\n";
    private static final String EMPTY_OBJECT_JSON = "{}\n";

    private AppPaths() {
    }

    /** Tạo thư mục + 3 file JSON nếu chưa có (F: hoặc Z:, không thì local). Gọi khi mở tool. */
    public static void ensureDataFiles() {
        AppPaths.savedProducts();
        AppPaths.customerHistory();
        AppPaths.brandColors();
    }

    /** Ổ chung đang dùng. Chưa map thì trả về đường F: để hiện hướng dẫn. */
    public static Path sharedDir() {
        Path found = AppPaths.trySharedDir();
        return found != null ? found : SHARED_CANDIDATES[0];
    }

    /** Thư mục data đang dùng thực tế (F: hoặc Z: nếu ghi được; không thì local tạm). */
    public static Path dataDir() {
        Path shared = AppPaths.trySharedDir();
        if (shared != null) {
            return shared;
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

    /** Lý do không dùng được ổ chung — để hiện trên UI. */
    public static String sharedDriveStatus() {
        Path dir = AppPaths.trySharedDir();
        if (dir != null) {
            return "\u0110ang d\u00f9ng \u1ed5 chung: " + dir;
        }
        boolean anyDrive = false;
        for (Path candidate : SHARED_CANDIDATES) {
            if (AppPaths.driveReady(candidate)) {
                anyDrive = true;
                break;
            }
        }
        if (anyDrive) {
            return "C\u00f3 \u1ed5 F: ho\u1eb7c Z: nh\u01b0ng KH\u00d4NG GHI \u0110\u01af\u1ee2C th\u01b0 m\u1ee5c:\n"
                    + "TOOL - HOP DONG\\ContractToolData";
        }
        return "M\u00e1y n\u00e0y CH\u01afA C\u00d3 \u1ed5 F: ho\u1eb7c Z: (ch\u01b0a map). \u0110ang l\u01b0u t\u1ea1m tr\u00ean C:.\n"
                + "H\u00e3y map \u1ed5 m\u1ea1ng th\u00e0nh F: ho\u1eb7c Z:, th\u01b0 m\u1ee5c:\n"
                + SHARED_CANDIDATES[0] + "\nho\u1eb7c\n" + SHARED_CANDIDATES[1];
    }

    /** Câu nhắc khi phải lưu tạm vì chưa có ổ chung. */
    public static String localSaveNotice(Path savedFile) {
        return "Kh\u00f4ng ghi \u0111\u01b0\u1ee3c \u1ed5 chung (F: ho\u1eb7c Z:) \u2014 \u0111\u00e3 l\u01b0u t\u1ea1m local:\n" + savedFile
                + "\n\nH\u00e3y map \u1ed5 m\u1ea1ng th\u00e0nh F: ho\u1eb7c Z:, th\u01b0 m\u1ee5c TOOL - HOP DONG\\ContractToolData.";
    }

    /**
     * Ưu tiên ContractToolData cạnh bộ app đang mở, nếu trong đó đã có file hàng hóa.
     * Tránh máy có cả F: lẫn Z: đọc nhầm bản ở ổ kia.
     */
    private static Path trySharedDir() {
        Path cached = cachedShared;
        if (cached != null) {
            try {
                if (Files.isDirectory(cached) && Files.isWritable(cached)) {
                    return cached;
                }
            }
            catch (Exception exception) {
                cachedShared = null;
            }
        }
        Path besideApp = AppPaths.installDataDir();
        if (AppPaths.usableDataDir(besideApp)) {
            cachedShared = besideApp;
            return besideApp;
        }
        for (Path dir : SHARED_CANDIDATES) {
            if (AppPaths.usableDataDir(dir)) {
                cachedShared = dir;
                return dir;
            }
        }
        Path created = AppPaths.createWritable(besideApp);
        if (created == null) {
            for (Path dir : SHARED_CANDIDATES) {
                created = AppPaths.createWritable(dir);
                if (created != null) {
                    break;
                }
            }
        }
        if (created != null) {
            cachedShared = created;
        }
        return created;
    }

    /** jpackage: .../TOOL - HOP DONG/app/contract-tool.jar → .../TOOL - HOP DONG/ContractToolData */
    private static Path installDataDir() {
        try {
            URI uri = AppPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path code = Path.of(uri);
            Path parent = Files.isRegularFile(code) ? code.getParent() : null;
            if (parent != null && "app".equalsIgnoreCase(String.valueOf(parent.getFileName()))) {
                Path root = parent.getParent();
                if (root != null) {
                    return root.resolve("ContractToolData");
                }
            }
        }
        catch (Exception exception) {
            // chạy từ IDE thì không có thư mục app
        }
        return null;
    }

    private static boolean usableDataDir(Path dir) {
        if (!AppPaths.hasProductFile(dir)) {
            return false;
        }
        try {
            return Files.isDirectory(dir) && Files.isWritable(dir);
        }
        catch (Exception exception) {
            return false;
        }
    }

    private static boolean hasProductFile(Path dir) {
        if (dir == null) {
            return false;
        }
        try {
            Path file = dir.resolve(PRODUCT_FILE);
            return Files.isRegularFile(file) && Files.size(file) > 120L;
        }
        catch (Exception exception) {
            return false;
        }
    }

    private static Path createWritable(Path dir) {
        if (dir == null || !AppPaths.driveReady(dir)) {
            return null;
        }
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
            if (Files.isDirectory(dir) && Files.isWritable(dir)) {
                return dir;
            }
        }
        catch (Exception exception) {
            // empty
        }
        return null;
    }

    private static boolean driveReady(Path dir) {
        Path root = dir.getRoot();
        if (root == null) {
            return false;
        }
        java.io.File drive = root.toFile();
        return drive.exists() || Files.exists(root);
    }

    /** Xóa bản bang_mau / brand_colors cũ trên C: — tránh migrate đè lại sau khi đã xóa trên ổ chung. */
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
        AppPaths.migrateIfMissing(target, AppPaths.legacySources(PRODUCT_FILE, "saved_data.txt"));
        AppPaths.createIfMissing(target, EMPTY_PRODUCT_JSON);
        return target;
    }

    /** File JSON khách hàng (chung công ty trên ổ F: hoặc Z:). */
    public static Path customerHistory() {
        Path target = AppPaths.dataDir().resolve(CUSTOMER_FILE);
        AppPaths.migrateIfMissing(target, AppPaths.legacySources(CUSTOMER_FILE, "customer_history.json"));
        AppPaths.createIfMissing(target, EMPTY_OBJECT_JSON);
        return target;
    }

    /** File JSON bảng màu hãng (chung công ty trên ổ F: hoặc Z:). */
    public static Path brandColors() {
        Path target = AppPaths.dataDir().resolve(BRAND_COLOR_FILE);
        AppPaths.migrateIfMissing(target, AppPaths.legacySources(BRAND_COLOR_FILE, "brand_colors.json"));
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
            // không tạo được (ổ chung khóa / quyền) — bỏ qua, lưu lúc SAVE sẽ thử lại
        }
    }

    /** Bản local và cả F:/Z: — máy đổi chữ ổ vẫn lấy được file cũ. */
    private static Path[] legacySources(String currentName, String oldName) {
        java.util.ArrayList<Path> sources = new java.util.ArrayList<Path>();
        Path beside = AppPaths.installDataDir();
        if (beside != null) {
            sources.add(beside.resolve(oldName));
            sources.add(beside.resolve(currentName));
        }
        for (Path dir : SHARED_CANDIDATES) {
            sources.add(dir.resolve(oldName));
            sources.add(dir.resolve(currentName));
        }
        sources.add(LOCAL_DIR.resolve(oldName));
        sources.add(LOCAL_DIR.resolve(currentName));
        return sources.toArray(new Path[0]);
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
