/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.util;

import java.util.ArrayList;

public final class MoneyWords {
    private static final String[] NUMS = new String[]{"kh\u00f4ng", "m\u1ed9t", "hai", "ba", "b\u1ed1n", "n\u0103m", "s\u00e1u", "b\u1ea3y", "t\u00e1m", "ch\u00edn"};

    private MoneyWords() {
    }

    public static String toWords(long n) {
        String text;
        if (n == 0L) {
            return "Kh\u00f4ng \u0111\u1ed3ng ch\u1eb5n";
        }
        String s = String.format("%012d", n);
        int ty = Integer.parseInt(s.substring(0, 3));
        int trieu = Integer.parseInt(s.substring(3, 6));
        int nghin = Integer.parseInt(s.substring(6, 9));
        int dong = Integer.parseInt(s.substring(9, 12));
        ArrayList<String> parts = new ArrayList<String>();
        if (ty > 0) {
            parts.add(MoneyWords.read3(ty, false) + " t\u1ef7");
        }
        if (trieu > 0) {
            parts.add(MoneyWords.read3(trieu, ty > 0) + " tri\u1ec7u");
        }
        if (nghin > 0) {
            parts.add(MoneyWords.read3(nghin, ty > 0 || trieu > 0) + " ngh\u00ecn");
        }
        if (dong > 0) {
            parts.add(MoneyWords.read3(dong, ty > 0 || trieu > 0 || nghin > 0));
        }
        if ((text = String.join(" ", parts).trim()).isEmpty()) {
            return "Kh\u00f4ng \u0111\u1ed3ng ch\u1eb5n";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1) + " \u0111\u1ed3ng ch\u1eb5n";
    }

    private static String read3(int num, boolean hasHigher) {
        int hundreds = num / 100;
        int tens = num % 100 / 10;
        int ones = num % 10;
        ArrayList<String> parts = new ArrayList<String>();
        if (hundreds > 0) {
            parts.add(NUMS[hundreds] + " tr\u0103m");
        } else if (hasHigher && (tens > 0 || ones > 0)) {
            parts.add("kh\u00f4ng tr\u0103m");
        }
        if (tens > 1) {
            parts.add(NUMS[tens] + " m\u01b0\u01a1i");
            if (ones == 1) {
                parts.add("m\u1ed1t");
            } else if (ones == 5) {
                parts.add("l\u0103m");
            } else if (ones > 0) {
                parts.add(NUMS[ones]);
            }
        } else if (tens == 1) {
            parts.add("m\u01b0\u1eddi");
            if (ones == 5) {
                parts.add("l\u0103m");
            } else if (ones > 0) {
                parts.add(NUMS[ones]);
            }
        } else if (ones > 0) {
            if (hundreds > 0 || hasHigher) {
                parts.add("l\u1ebb");
            }
            parts.add(NUMS[ones]);
        }
        return String.join(" ", parts);
    }
}

