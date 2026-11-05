/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NumberUtil {
    /** 1.000.000 — không dùng dấu phẩy, không phụ thuộc locale máy. */
    private static final DecimalFormatSymbols DOT_SYMBOLS = new DecimalFormatSymbols(Locale.ROOT);
    private static final Pattern COMMA_GROUPS = Pattern.compile("(?<!\\d)\\d{1,3}(?:,\\d{3})+(?!\\d)");

    static {
        DOT_SYMBOLS.setGroupingSeparator('.');
        DOT_SYMBOLS.setDecimalSeparator(',');
    }

    private NumberUtil() {
    }

    public static long parseAmount(String text) {
        if (text == null || text.isBlank()) {
            return 0L;
        }
        String digits = text.replace(".", "").replace(",", "").replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(digits);
    }

    public static String formatDot(long n) {
        DecimalFormat fmt = new DecimalFormat("#,##0", DOT_SYMBOLS);
        fmt.setGroupingUsed(true);
        fmt.setGroupingSize(3);
        return fmt.format(n);
    }

    public static String formatTyping(String raw) {
        long value = NumberUtil.parseAmount(raw);
        return NumberUtil.formatDot(value);
    }

    /** 10,000 hoặc 1,000,000 trong câu chữ → 10.000 / 1.000.000. Không đụng số điện thoại, MST. */
    public static String dotsNotCommas(String text) {
        if (text == null || text.indexOf(',') < 0) {
            return text;
        }
        Matcher matcher = COMMA_GROUPS.matcher(text);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(out, Matcher.quoteReplacement(matcher.group().replace(',', '.')));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}

