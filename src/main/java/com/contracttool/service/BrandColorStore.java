/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.service;

import com.contracttool.AppPaths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class BrandColorStore {
    public static final Color[] THEME_SWATCHES = new Color[]{new Color(14427686), new Color(15357964), new Color(14251782), new Color(13273604), new Color(6660877), new Color(1409085), new Color(1013358), new Color(165064), new Color(1920728), new Color(8141549), new Color(14362487), new Color(3359061), new Color(0)};
    private static final Color[] DEFAULT_BRAND = new Color[]{THEME_SWATCHES[0], THEME_SWATCHES[5], THEME_SWATCHES[7], THEME_SWATCHES[9]};
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
    private final Map<String, String> brandToHex = new LinkedHashMap<String, String>();

    public BrandColorStore() {
        this.reload();
    }

    /** @deprecated dùng constructor không tham số — luôn lấy path từ AppPaths (ưu tiên F:). */
    public BrandColorStore(Path ignored) {
        this();
    }

    public Path path() {
        return AppPaths.brandColors();
    }

    /** Đọc lại từ file hiện tại (F: nếu có). */
    public void reload() {
        this.brandToHex.clear();
        Path file = this.path();
        if (Files.exists(file, new LinkOption[0])) {
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Map<String, String> map = (Map<String, String>)this.gson.fromJson((Reader)reader, this.type);
                if (map != null) {
                    for (Map.Entry<String, String> e : map.entrySet()) {
                        String hex = e.getValue();
                        if (hex == null || hex.isBlank()) {
                            continue;
                        }
                        this.brandToHex.put(BrandColorStore.normalize(e.getKey()), hex.trim());
                    }
                }
            }
            catch (Exception exception) {
                // empty
            }
        }
        // Chỉ seed mặc định lần đầu (file trống) — không ghi đè sau khi user đã xóa.
        if (this.brandToHex.isEmpty()) {
            this.ensureDefault("CHEMTRUE", DEFAULT_BRAND[0]);
            this.ensureDefault("FASTEP", DEFAULT_BRAND[1]);
            this.ensureDefault("HEALGEN", DEFAULT_BRAND[2]);
            this.ensureDefault("ASSURE", DEFAULT_BRAND[3]);
            this.saveQuiet();
        }
    }

    public Color colorFor(String brand) {
        String key = BrandColorStore.normalize(brand);
        if (key.isEmpty()) {
            return Color.BLACK;
        }
        if (this.brandToHex.containsKey(key)) {
            return BrandColorStore.parseHex(this.brandToHex.get(key));
        }
        // Không tự thêm lại hãng đã xóa vào file — chỉ màu tạm để hiển thị bảng.
        int idx = Math.floorMod(key.hashCode(), THEME_SWATCHES.length);
        return THEME_SWATCHES[idx];
    }

    public Map<String, Color> all() {
        LinkedHashMap<String, Color> map = new LinkedHashMap<String, Color>();
        for (Map.Entry<String, String> e : this.brandToHex.entrySet()) {
            map.put(e.getKey(), BrandColorStore.parseHex(e.getValue()));
        }
        return Collections.unmodifiableMap(map);
    }

    public void setColor(String brand, Color color) {
        String key = BrandColorStore.normalize(brand);
        if (key.isEmpty() || color == null) {
            return;
        }
        this.brandToHex.put(key, BrandColorStore.toHex(color));
        this.saveQuiet();
    }

    public boolean removeBrand(String brand) {
        String key = BrandColorStore.normalize(brand);
        if (key.isEmpty() || !this.brandToHex.containsKey(key)) {
            return false;
        }
        this.brandToHex.remove(key);
        return this.save();
    }

    public boolean save() {
        Path file = this.path();
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, new OpenOption[0])) {
                this.gson.toJson(this.brandToHex, (Appendable)writer);
            }
            // Xóa bản cũ trên C: để lần sau không migrate đè lại hãng đã xóa.
            AppPaths.clearLocalBrandColorLegacy();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public void ensureBrand(String brand) {
        String key = BrandColorStore.normalize(brand);
        if (key.isEmpty()) {
            return;
        }
        if (this.brandToHex.containsKey(key)) {
            return;
        }
        this.brandToHex.put(key, BrandColorStore.toHex(THEME_SWATCHES[this.brandToHex.size() % THEME_SWATCHES.length]));
        this.saveQuiet();
    }

    private boolean ensureDefault(String brand, Color color) {
        String key = BrandColorStore.normalize(brand);
        if (this.brandToHex.containsKey(key)) {
            return false;
        }
        this.brandToHex.put(key, BrandColorStore.toHex(color));
        return true;
    }

    private void saveQuiet() {
        this.save();
    }

    public static String normalize(String brand) {
        if (brand == null) {
            return "";
        }
        return brand.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static Color parseHex(String hex) {
        if (hex == null || hex.isBlank()) {
            return Color.BLACK;
        }
        String h = hex.trim();
        if (!h.startsWith("#")) {
            h = "#" + h;
        }
        try {
            return Color.decode(h);
        }
        catch (Exception e) {
            return Color.BLACK;
        }
    }
}
