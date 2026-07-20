/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 */
package com.contracttool.service;

import com.contracttool.model.SavedData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class DataStore {
    private final Path file;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public DataStore(Path file) {
        this.file = file;
    }

    public SavedData load() {
        if (!Files.exists(this.file, new LinkOption[0])) {
            return null;
        }
        try (BufferedReader reader = Files.newBufferedReader(this.file, StandardCharsets.UTF_8)) {
            return (SavedData)this.gson.fromJson((Reader)reader, SavedData.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void save(SavedData data) throws IOException {
        Path parent = this.file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8, new OpenOption[0]);){
            this.gson.toJson((Object)data, (Appendable)writer);
        }
    }
}

