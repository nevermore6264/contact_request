/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.model;

import java.text.Normalizer;
import java.util.Locale;

public enum ExportDoc {
    HOP_DONG("H\u1ee2P \u0110\u1ed2NG"),
    NT_BG("NT & BG"),
    THANH_LY("THANH L\u00dd"),
    BBGN("BBGN"),
    MAU_08A("M\u1eaaU 08A"),
    DOI_CHIEU("\u0110\u1ed0I CHI\u1ebeU C\u00d4NG N\u1ee2"),
    DE_NGHI_TT("\u0110\u1ec0 NGH\u1eca THANH TO\u00c1N"),
    BAO_PHAT("B\u00c1O PH\u00c1T"),
    BAO_GIA_1("B\u00c1O GI\u00c1 \u2013 1"),
    BAO_GIA_2("B\u00c1O GI\u00c1 \u2013 2"),
    BAO_GIA_CUNG("B\u00c1O GI\u00c1 \u2013 \u0110\u01a0N V\u1eca CUNG \u1ee8NG");

    private final String label;

    private ExportDoc(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean matchesFileName(String fileName) {
        String n = ExportDoc.strip(fileName);
        if (n.isEmpty() || ExportDoc.isGuideFile(n)) {
            return false;
        }
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HOP_DONG -> {
                if (n.contains("hopdong") && !n.contains("ntbg") && !n.contains("thanhly") && !n.contains("bbgn")) {
                    yield true;
                }
                yield false;
            }
            case NT_BG -> {
                if (n.contains("ntbg") || n.contains("nghithuong") || n.contains("nt") && n.contains("bg") && n.contains("hopdong")) {
                    yield true;
                }
                yield false;
            }
            case THANH_LY -> n.contains("thanhly");
            // BBGN có HĐ; bản "báo phát" thuộc BAO_PHAT (không trùng tick).
            case BBGN -> n.contains("bbgn") && !n.contains("baophat") && !(n.contains("bao") && n.contains("phat"));
            case MAU_08A -> {
                if (n.contains("08a") || n.contains("mau08")) {
                    yield true;
                }
                yield false;
            }
            case DOI_CHIEU -> {
                if (n.contains("doichieu") || n.contains("congno")) {
                    yield true;
                }
                yield false;
            }
            case DE_NGHI_TT -> {
                if (n.contains("denghi") || n.contains("denghithanhtoan")) {
                    yield true;
                }
                yield false;
            }
            case BAO_PHAT -> {
                if (n.contains("baophat") || n.contains("phat") && n.contains("bao") && !n.contains("baogia")) {
                    yield true;
                }
                yield false;
            }
            case BAO_GIA_1 -> {
                if (n.contains("99-1") || n.contains("baogia-1") || n.matches(".*baogia.*1\\.docx")) {
                    yield true;
                }
                yield false;
            }
            case BAO_GIA_2 -> {
                if (n.contains("99-2") || n.contains("baogia-2") || n.contains("baogia") && n.contains("-2") && !n.contains("cung")) {
                    yield true;
                }
                yield false;
            }
            case BAO_GIA_CUNG -> n.contains("99-3") || n.contains("cungung") || n.contains("donvicung") || n.contains("baogia") && n.contains("cung");
        };
    }

    public boolean isQuoteDoc() {
        return this == BAO_GIA_1 || this == BAO_GIA_2 || this == BAO_GIA_CUNG;
    }

    public static boolean isGuideFile(String fileName) {
        String n = ExportDoc.strip(fileName);
        return n.contains("huongdan") || n.contains("readme") || n.startsWith("~$");
    }

    public static String strip(String input) {
        if (input == null) {
            return "";
        }
        String s = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).replace("\u0111", "d");
        return s.replaceAll("[^a-z0-9.\\-]", "");
    }
}

