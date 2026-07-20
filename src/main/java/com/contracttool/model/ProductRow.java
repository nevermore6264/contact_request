/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.model;

public class ProductRow {
    private String content;
    private String code;
    private String unit;
    private String quantity;
    private String unitPrice;

    public ProductRow() {
    }

    public ProductRow(String content, String code, String unit, String quantity, String unitPrice) {
        this.content = content;
        this.code = code;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}

