/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.contracttool.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class SavedData {
    @SerializedName(value="prices_nam")
    private List<Integer> pricesSouth = new ArrayList<Integer>();
    @SerializedName(value="prices_bac")
    private List<Integer> pricesNorth = new ArrayList<Integer>();
    private List<String> content = new ArrayList<String>();
    private List<String> unit = new ArrayList<String>();
    private List<String> codes = new ArrayList<String>();

    public List<Integer> getPricesSouth() {
        return this.pricesSouth;
    }

    public void setPricesSouth(List<Integer> pricesSouth) {
        this.pricesSouth = pricesSouth;
    }

    public List<Integer> getPricesNorth() {
        return this.pricesNorth;
    }

    public void setPricesNorth(List<Integer> pricesNorth) {
        this.pricesNorth = pricesNorth;
    }

    public List<String> getContent() {
        return this.content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public List<String> getUnit() {
        return this.unit;
    }

    public void setUnit(List<String> unit) {
        this.unit = unit;
    }

    public List<String> getCodes() {
        return this.codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}

