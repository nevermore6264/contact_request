/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 */
package com.contracttool.service;

import com.contracttool.model.CustomerRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerStore {
    private final Path file;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private final Type type = new TypeToken<LinkedHashMap<String, CustomerRecord>>(){}.getType();

    public CustomerStore(Path file) {
        this.file = file;
    }

    public Map<String, CustomerRecord> loadAll() {
        if (!Files.exists(this.file, new LinkOption[0])) {
            return new LinkedHashMap<String, CustomerRecord>();
        }
        try (BufferedReader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
            Map<String, CustomerRecord> map = (Map<String, CustomerRecord>)this.gson.fromJson((Reader)reader, this.type);
            return map != null ? map : new LinkedHashMap<String, CustomerRecord>();
        }
        catch (Exception e) {
            return new LinkedHashMap<String, CustomerRecord>();
        }
    }

    public CustomerRecord findByTaxCode(String taxCode) {
        if (taxCode == null || taxCode.isBlank()) {
            return null;
        }
        String key = CustomerStore.normalize(taxCode);
        return this.loadAll().get(key);
    }

    public void save(CustomerRecord record) throws IOException {
        Map<String, CustomerRecord> all = this.loadAll();
        String key = CustomerStore.normalize(record.getTaxCode());
        record.setTaxCode(key);
        record.setUpdatedAt(System.currentTimeMillis());
        all.put(key, record);
        Files.createDirectories(this.file.toAbsolutePath().getParent() == null ? Path.of(".", new String[0]) : this.file.toAbsolutePath().getParent(), new FileAttribute[0]);
        try (BufferedWriter writer = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8, new OpenOption[0]);){
            this.gson.toJson(all, (Appendable)writer);
        }
    }

    private static String normalize(String taxCode) {
        return taxCode.trim().replaceAll("\\s+", "").toUpperCase();
    }
}

