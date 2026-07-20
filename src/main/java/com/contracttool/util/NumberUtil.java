/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class NumberUtil {
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
        return NumberFormat.getInstance(Locale.GERMANY).format(n);
    }

    public static String formatTyping(String raw) {
        long value = NumberUtil.parseAmount(raw);
        return NumberUtil.formatDot(value);
    }
}

