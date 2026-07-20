/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContractNumberUtil {
    private static final Set<String> SKIP_WORDS = Set.of("MTV", "VA", "AND", "THE", "OF", "TNHH", "CTCP", "CP", "LLC", "JSC", "LTD", "CO", "CTY", "HKD");
    private static final Pattern CONG_TY = Pattern.compile("(?i)^\\s*c[o\u00f4\u1ed1\u1ecd]ng\\s*ty\\s+", 64);
    private static final Pattern HO_KINH_DOANH = Pattern.compile("(?i)^\\s*h[o\u1ecf\u1ecd\u1ed9]\\s*kinh\\s*doanh\\s+", 64);
    private static final Pattern LOAI_HINH = Pattern.compile("(?i)^\\s*(tnhh(\\s+mtv)?|c[o\u00f4\u1ed1]\\s*ph[a\u1ea7\u1ea5]n|h[o\u1ee3\u1ecd]\\s*danh|mtv|ctcp|cp)\\s+", 64);

    private ContractNumberUtil() {
    }

    public static String fromPartyName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String name = raw.trim().replaceAll("\\s+", " ");
        String lower = ContractNumberUtil.stripAccents(name).toLowerCase(Locale.ROOT);
        String placeCode = ContractNumberUtil.initials(ContractNumberUtil.extractPlace(name));
        if (lower.contains("cong an")) {
            if (lower.contains("dac khu")) {
                return "CA\u0110K" + placeCode;
            }
            if (lower.contains("phuong")) {
                return "CAP" + placeCode;
            }
            if (lower.contains("xa")) {
                return "CAX" + placeCode;
            }
        }
        if (ContractNumberUtil.isUbnd(lower)) {
            if (lower.contains("dac khu")) {
                return "UBNDDK" + placeCode;
            }
            if (lower.contains("phuong")) {
                return "UBNDP" + placeCode;
            }
            return "UBNDX" + placeCode;
        }
        return ContractNumberUtil.companyInitials(name, lower);
    }

    private static boolean isUbnd(String lower) {
        return lower.contains("uy ban nhan dan") || lower.contains("hoi dong nhan dan") || lower.contains("van phong hoi dong") || lower.startsWith("ubnd");
    }

    private static String companyInitials(String name, String lower) {
        if (lower.startsWith("ho kinh doanh") || lower.contains("ho kinh doanh")) {
            String remainder = ContractNumberUtil.removeFirst(HO_KINH_DOANH, name);
            if (remainder.equals(name)) {
                remainder = ContractNumberUtil.stripPrefixAccentInsensitive(name, "ho kinh doanh");
            }
            return "HKD" + ContractNumberUtil.generalInitials(remainder);
        }
        if (lower.startsWith("cong ty") || lower.contains("cong ty ")) {
            String prev;
            String remainder = ContractNumberUtil.removeFirst(CONG_TY, name);
            if (remainder.equals(name)) {
                remainder = ContractNumberUtil.stripPrefixAccentInsensitive(name, "cong ty");
            }
            do {
                prev = remainder;
                if (!(remainder = ContractNumberUtil.removeFirst(LOAI_HINH, remainder)).equals(prev)) continue;
                String tmp = ContractNumberUtil.stripPrefixAccentInsensitive(remainder, "tnhh mtv");
                if (tmp.equals(remainder)) {
                    tmp = ContractNumberUtil.stripPrefixAccentInsensitive(remainder, "tnhh");
                }
                if (tmp.equals(remainder)) {
                    tmp = ContractNumberUtil.stripPrefixAccentInsensitive(remainder, "co phan");
                }
                if (tmp.equals(remainder)) {
                    tmp = ContractNumberUtil.stripPrefixAccentInsensitive(remainder, "hop danh");
                }
                if (tmp.equals(remainder)) {
                    tmp = ContractNumberUtil.stripPrefixAccentInsensitive(remainder, "mtv");
                }
                remainder = tmp;
            } while (!remainder.equals(prev) && !remainder.isBlank());
            return "CTY" + ContractNumberUtil.generalInitials(remainder);
        }
        return ContractNumberUtil.generalInitials(name);
    }

    private static String removeFirst(Pattern p, String text) {
        Matcher m = p.matcher(text);
        if (m.find() && m.start() == 0) {
            return text.substring(m.end()).trim();
        }
        m = p.matcher(text.trim());
        if (m.find() && m.start() == 0) {
            return text.trim().substring(m.end()).trim();
        }
        return text;
    }

    private static String stripPrefixAccentInsensitive(String name, String prefixAscii) {
        String[] p;
        String[] words = name.trim().split("\\s+");
        if (words.length < (p = prefixAscii.trim().split("\\s+")).length) {
            return name;
        }
        for (int i = 0; i < p.length; ++i) {
            if (ContractNumberUtil.stripAccents(words[i]).equalsIgnoreCase(p[i])) continue;
            return name;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = p.length; i < words.length; ++i) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(words[i]);
        }
        return sb.toString().trim();
    }

    private static String extractPlace(String name) {
        String[] markers;
        String lower = name.toLowerCase(Locale.ROOT);
        for (String m : markers = new String[]{"\u0111\u1eb7c khu ", "dac khu ", "ph\u01b0\u1eddng ", "phuong ", "x\u00e3 ", "xa "}) {
            int idx = lower.lastIndexOf(m);
            if (idx < 0) continue;
            return name.substring(idx + m.length()).trim();
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return parts[parts.length - 2] + " " + parts[parts.length - 1];
        }
        return name;
    }

    private static String generalInitials(String name) {
        String cleaned = name.replace("/", " ").replace("&", " ").replace("-", " ");
        StringBuilder sb = new StringBuilder();
        for (String word : cleaned.split("\\s+")) {
            String noAccent;
            String bare;
            if (word.isBlank() || (bare = word.toUpperCase(Locale.ROOT).replaceAll("[^\\p{L}0-9]", "")).isEmpty() || SKIP_WORDS.contains(noAccent = ContractNumberUtil.stripAccents(bare).toUpperCase(Locale.ROOT))) continue;
            sb.append(Character.toUpperCase(noAccent.charAt(0)));
        }
        return sb.toString();
    }

    private static String initials(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            String bare = word.replaceAll("[^\\p{L}0-9]", "");
            if (bare.isEmpty()) continue;
            sb.append(Character.toUpperCase(bare.charAt(0)));
        }
        return sb.toString();
    }

    private static String stripAccents(String input) {
        String n = Normalizer.normalize(input, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        return n.replace("\u0111", "d").replace("\u0110", "D");
    }
}

