/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.poi.xwpf.usermodel.ParagraphAlignment
 *  org.apache.poi.xwpf.usermodel.UnderlinePatterns
 *  org.apache.poi.xwpf.usermodel.XWPFDocument
 *  org.apache.poi.xwpf.usermodel.XWPFParagraph
 *  org.apache.poi.xwpf.usermodel.XWPFRun
 *  org.apache.poi.xwpf.usermodel.XWPFTable
 *  org.apache.poi.xwpf.usermodel.XWPFTableCell
 *  org.apache.poi.xwpf.usermodel.XWPFTableRow
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth
 */
package com.contracttool.service;

import com.contracttool.model.ContractInfo;
import com.contracttool.model.ExportDoc;
import com.contracttool.util.MoneyWords;
import com.contracttool.util.NumberUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class WordExportService {
    public int export(Path inputFolder, Path outputFolder, List<LineItem> rows, ContractInfo info, Set<ExportDoc> selectedTypes) throws IOException {
        if (inputFolder == null || outputFolder == null) {
            throw new IllegalArgumentException("B\u1ea1n ch\u01b0a ch\u1ecdn th\u01b0 m\u1ee5c g\u1ed1c ho\u1eb7c th\u01b0 m\u1ee5c xu\u1ea5t.");
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Kh\u00f4ng c\u00f3 d\u00f2ng h\u1ee3p l\u1ec7 \u0111\u1ec3 xu\u1ea5t (c\u1ea7n \u00edt nh\u1ea5t 1 d\u00f2ng c\u00f3 s\u1ed1 l\u01b0\u1ee3ng > 0).");
        }
        if (selectedTypes == null || selectedTypes.isEmpty()) {
            throw new IllegalArgumentException("H\u00e3y ch\u1ecdn \u00edt nh\u1ea5t m\u1ed9t m\u1eabu xu\u1ea5t.");
        }
        long grand = rows.stream().mapToLong(r -> NumberUtil.parseAmount(r.total())).sum();
        String totalNumber = NumberUtil.formatDot(grand);
        String totalText = MoneyWords.toWords(grand);
        Map<String, String> placeholders = this.buildPlaceholders(info, totalNumber, totalText);
        int count = 0;
        Files.createDirectories(outputFolder, new FileAttribute[0]);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputFolder, "*.docx");){
            for (Path input : stream) {
                String name = input.getFileName().toString();
                if (name.startsWith("~$") || ExportDoc.isGuideFile(name) || !this.matchesAny(name, selectedTypes)) continue;
                Path output = outputFolder.resolve(name);
                InputStream in = Files.newInputStream(input, new OpenOption[0]);
                try (XWPFDocument doc = new XWPFDocument(in);){
                    this.replaceInDocument(doc, placeholders);
                    // Ẩn dòng trống Tài khoản / Đại diện / Chức vụ (đôi); cả 2 trống thì giữ 1.
                    this.hideEmptyPairedInfoRows(doc);
                    boolean omitPrices = WordExportService.isBaoPhatDeliveryFile(name);
                    boolean filled = this.insertOrReplaceTable(doc, rows, totalNumber, omitPrices);
                    // Mẫu báo giá cứng (99-1/99-2): điền đúng SP đã chọn trên tool, không lọc theo STT catalog.
                    if (!filled && WordExportService.isHardcodedQuoteFile(name)) {
                        this.fillHardcodedQuoteTable(doc, rows, totalNumber);
                    }
                    try (OutputStream out = Files.newOutputStream(output, new OpenOption[0]);){
                        doc.write(out);
                    }
                    ++count;
                }
                finally {
                    if (in == null) continue;
                    in.close();
                }
            }
        }
        if (count == 0) {
            throw new IllegalArgumentException("Kh\u00f4ng t\u00ecm th\u1ea5y file Word n\u00e0o kh\u1edbp m\u1eabu \u0111\u00e3 ch\u1ecdn trong th\u01b0 m\u1ee5c g\u1ed1c.\nH\u00e3y ch\u1ecdn \u0111\u00fang th\u01b0 m\u1ee5c ch\u1ee9a m\u1eabu h\u1ee3p \u0111\u1ed3ng (v\u00ed d\u1ee5 folder H\u1ee2P \u0110\u1ed2NG), kh\u00f4ng ch\u1ecdn folder ch\u1ee9a file h\u01b0\u1edbng d\u1eabn.");
        }
        return count;
    }

    /**
     * @deprecated Không còn dùng: lọc theo STT catalog làm lệch với bảng đã dựng từ UI (STT 1..n).
     */
    public void filterQuoteTables(Path outputFolder, List<Integer> keepIndexes) throws IOException {
        // no-op — giữ API cũ để không gãy chỗ gọi
    }

    private static boolean isHardcodedQuoteFile(String fileName) {
        String n = ExportDoc.strip(fileName);
        return n.contains("99-1") || n.contains("99-2") || n.contains("baogia-1") || n.contains("baogia-2");
    }

    /** BBGN báo phát: bảng hàng hóa không hiện Đơn giá / Thành tiền / Tổng cộng. */
    private static boolean isBaoPhatDeliveryFile(String fileName) {
        String n = ExportDoc.strip(fileName);
        if (n.contains("baogia")) {
            return false;
        }
        return n.contains("baophat") || n.contains("bbgn") && n.contains("phat");
    }

    public int processCungUngFiles(Path outputFolder) throws IOException {
        if (outputFolder == null) {
            throw new IllegalArgumentException("B\u1ea1n ch\u01b0a ch\u1ecdn th\u01b0 m\u1ee5c xu\u1ea5t.");
        }
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputFolder, "*.docx");){
            for (Path file : stream) {
                String name = file.getFileName().toString().toLowerCase();
                if (name.startsWith("~$") || ExportDoc.isGuideFile(name) || !name.contains("cung")) continue;
                InputStream in = Files.newInputStream(file, new OpenOption[0]);
                try (XWPFDocument doc = new XWPFDocument(in);){
                    int i;
                    XWPFTable table;
                    if (doc.getTables().isEmpty() || (table = (XWPFTable)doc.getTables().get(0)).getNumberOfRows() == 0) continue;
                    XWPFTableRow header = table.getRow(0);
                    int colQty = -1;
                    int colAmount = -1;
                    for (i = 0; i < header.getTableCells().size(); ++i) {
                        String txt = header.getCell(i).getText().trim().toLowerCase();
                        if (txt.contains("s\u1ed1 l\u01b0\u1ee3ng") || txt.contains("so luong")) {
                            colQty = i;
                            continue;
                        }
                        if (!txt.contains("th\u00e0nh ti\u1ec1n") && !txt.contains("thanh tien")) continue;
                        colAmount = i;
                    }
                    if (colQty < 0 || colAmount < 0) continue;
                    for (i = table.getNumberOfRows() - 1; i >= 0; --i) {
                        boolean isTotal = false;
                        for (XWPFTableCell cell : table.getRow(i).getTableCells()) {
                            String t = cell.getText().toLowerCase();
                            if (!t.contains("t\u1ed5ng c\u1ed9ng") && !t.contains("tong cong")) continue;
                            isTotal = true;
                            break;
                        }
                        if (!isTotal) continue;
                        table.removeRow(i);
                    }
                    for (i = 1; i < table.getNumberOfRows(); ++i) {
                        XWPFTableRow row = table.getRow(i);
                        if (colQty >= row.getTableCells().size()) continue;
                        this.setCellText(row.getCell(colQty), "1", true);
                    }
                    for (i = 0; i < table.getNumberOfRows(); ++i) {
                        XWPFTableRow row = table.getRow(i);
                        if (colAmount >= row.getTableCells().size()) continue;
                        row.removeCell(colAmount);
                    }
                    try (OutputStream out = Files.newOutputStream(file, new OpenOption[0]);){
                        doc.write(out);
                    }
                    ++count;
                }
                finally {
                    if (in == null) continue;
                    in.close();
                }
            }
        }
        return count;
    }

    public int trimTrailingEmptyParagraphs(Path outputFolder) throws IOException {
        if (outputFolder == null) {
            throw new IllegalArgumentException("B\u1ea1n ch\u01b0a ch\u1ecdn th\u01b0 m\u1ee5c xu\u1ea5t.");
        }
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputFolder, "*.docx");){
            for (Path file : stream) {
                String name = file.getFileName().toString();
                if (name.startsWith("~$") || ExportDoc.isGuideFile(name)) continue;
                InputStream in = Files.newInputStream(file, new OpenOption[0]);
                try (XWPFDocument doc = new XWPFDocument(in);){
                    XWPFParagraph p;
                    String text;
                    boolean changed = false;
                    List paragraphs = doc.getParagraphs();
                    for (int i = paragraphs.size() - 1; i >= 0 && ((text = (p = (XWPFParagraph)paragraphs.get(i)).getText()) == null || text.trim().isEmpty()); --i) {
                        int pos = doc.getPosOfParagraph(p);
                        if (pos < 0) continue;
                        doc.removeBodyElement(pos);
                        changed = true;
                    }
                    if (!changed) continue;
                    try (OutputStream out = Files.newOutputStream(file, new OpenOption[0]);){
                        doc.write(out);
                    }
                    ++count;
                }
                finally {
                    if (in == null) continue;
                    in.close();
                }
            }
        }
        return count;
    }

    public List<Integer> selectedIndexes(List<Long> quantities) {
        ArrayList<Integer> keep = new ArrayList<Integer>();
        for (int i = 0; i < quantities.size(); ++i) {
            if (quantities.get(i) == null || quantities.get(i) <= 0L) continue;
            keep.add(i + 1);
        }
        return keep;
    }

    private Map<String, String> buildPlaceholders(ContractInfo info, String totalNumber, String totalText) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("{{TOTAL_NUMBER}}", totalNumber);
        map.put("{{TOTAL_TEXT}}", totalText);
        map.put("{{BenA}}", this.nullToEmpty(info.getPartyA()));
        map.put("{{DiaChi}}", this.nullToEmpty(info.getAddress()));
        map.put("{{DienThoai}}", this.nullToEmpty(info.getPhone()));
        map.put("{{TaiKhoan1}}", WordExportService.oneLine(info.getAccount1()));
        map.put("{{TaiKhoan2}}", WordExportService.oneLine(info.getAccount2()));
        map.put("{{MaSoThue}}", this.nullToEmpty(info.getTaxCode()));
        map.put("{{DaiDien1}}", this.nullToEmpty(info.getRepresentative1()));
        map.put("{{DaiDien2}}", this.nullToEmpty(info.getRepresentative2()));
        map.put("{{ChucVu1}}", this.nullToEmpty(info.getTitle1()));
        map.put("{{ChucVu2}}", this.nullToEmpty(info.getTitle2()));
        map.put("{{HopDong}}", this.nullToEmpty(info.getContractNo()));
        map.put("{{MaQHNS}}", this.nullToEmpty(info.getBudgetCode()));
        map.put("{{Nam}}", this.nullToEmpty(info.getYear()));
        return map;
    }

    private void replaceInDocument(XWPFDocument doc, Map<String, String> placeholders) {
        ArrayList<XWPFParagraph> toRemove = new ArrayList<XWPFParagraph>();
        for (XWPFParagraph paragraph : new ArrayList<XWPFParagraph>(doc.getParagraphs())) {
            if (!this.replaceInParagraph(paragraph, placeholders)) continue;
            toRemove.add(paragraph);
        }
        for (XWPFParagraph paragraph : toRemove) {
            int pos = doc.getPosOfParagraph(paragraph);
            if (pos < 0) continue;
            doc.removeBodyElement(pos);
        }
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        this.replaceInParagraph(paragraph, placeholders);
                    }
                }
            }
        }
    }

    private boolean replaceInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        String full = paragraph.getText();
        if (full == null || full.isEmpty()) {
            return false;
        }
        boolean needsReplace = false;
        for (String key : placeholders.keySet()) {
            if (!full.contains(key)) continue;
            needsReplace = true;
            break;
        }
        if (!needsReplace) {
            return false;
        }
        boolean changed = this.replacePlaceholdersInRuns(paragraph, placeholders);
        String updated = paragraph.getText();
        if (updated == null) {
            updated = "";
        }
        if (!changed || WordExportService.stillHasPlaceholder(updated, placeholders)) {
            updated = full;
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                updated = updated.replace(e.getKey(), e.getValue() == null ? "" : (CharSequence)e.getValue());
            }
            while (!paragraph.getRuns().isEmpty()) {
                paragraph.removeRun(0);
            }
            if (updated.trim().isEmpty()) {
                return true;
            }
            this.writeFormattedParagraph(paragraph, updated);
        }
        String string = updated = paragraph.getText() == null ? "" : paragraph.getText();
        if (updated.trim().isEmpty()) {
            return true;
        }
        this.reformatCanCuPartyLines(paragraph);
        updated = paragraph.getText() == null ? "" : paragraph.getText();
        this.applyValueHangingIndent(paragraph, updated);
        return false;
    }

    private boolean replacePlaceholdersInRuns(XWPFParagraph paragraph, Map<String, String> placeholders) {
        boolean changed = false;
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.text();
            if (text == null || text.isEmpty()) continue;
            String next = text;
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                if (!next.contains(e.getKey())) continue;
                next = next.replace(e.getKey(), e.getValue() == null ? "" : (CharSequence)e.getValue());
                changed = true;
            }
            if (next.equals(text)) continue;
            run.setText(next, 0);
        }
        return changed;
    }

    private static boolean stillHasPlaceholder(String text, Map<String, String> placeholders) {
        for (String key : placeholders.keySet()) {
            if (!text.contains(key)) continue;
            return true;
        }
        return false;
    }

    private void writeFormattedParagraph(XWPFParagraph paragraph, String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.contains("b\u00ean a") && text.contains(":")) {
            int colon = text.indexOf(58);
            String label = text.substring(0, colon + 1);
            String value = text.substring(colon + 1);
            XWPFRun labelRun = paragraph.createRun();
            labelRun.setText(label);
            labelRun.setFontFamily("Times New Roman");
            labelRun.setFontSize(12);
            labelRun.setBold(true);
            labelRun.setUnderline(UnderlinePatterns.SINGLE);
            if (!value.isEmpty()) {
                XWPFRun valueRun = paragraph.createRun();
                valueRun.setText((String)(value.startsWith(" ") ? value : " " + value.trim()));
                valueRun.setFontFamily("Times New Roman");
                valueRun.setFontSize(12);
                valueRun.setBold(true);
            }
            return;
        }
        this.writeTextWithBreaks(paragraph.createRun(), text, 12, false);
    }

    private void reformatCanCuPartyLines(XWPFParagraph paragraph) {
        int afterGiua;
        String text = paragraph.getText();
        if (text == null) {
            return;
        }
        String lower = text.toLowerCase(Locale.ROOT).trim();
        if (!lower.startsWith("c\u0103n c\u1ee9") && !lower.startsWith("can cu")) {
            return;
        }
        if (!lower.contains("gi\u1eefa") || !lower.contains(" v\u00e0 ") && !lower.contains(" va ")) {
            return;
        }
        int giua = WordExportService.indexOfIgnoreCase(text, " gi\u1eefa ");
        if (giua < 0) {
            return;
        }
        for (afterGiua = giua + 6; afterGiua < text.length() && Character.isWhitespace(text.charAt(afterGiua)); ++afterGiua) {
        }
        int va = WordExportService.indexOfIgnoreCase(text, " v\u00e0 ", afterGiua);
        if (va < 0) {
            va = WordExportService.indexOfIgnoreCase(text, " va ", afterGiua);
        }
        if (va < 0 || va <= afterGiua) {
            return;
        }
        String prefix = text.substring(0, giua + 6).replaceAll("\\s+$", "");
        String party = text.substring(afterGiua, va).trim();
        String company = text.substring(va + 4).trim();
        if (party.length() < 8 || company.length() < 4) {
            return;
        }
        String partyLower = party.toLowerCase(Locale.ROOT);
        if (partyLower.equals("hai b\u00ean") || partyLower.equals("hai ben") || partyLower.equals("c\u00e1c b\u00ean")) {
            return;
        }
        while (!paragraph.getRuns().isEmpty()) {
            paragraph.removeRun(0);
        }
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setIndentationFirstLine(0);
        paragraph.setIndentationLeft(0);
        paragraph.setIndentationHanging(0);
        // Chỉ ngắt sau "giữa". Không ép xuống dòng sau "và" — để Word tự wrap khi hết dòng.
        XWPFRun r1 = paragraph.createRun();
        r1.setText(prefix);
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(12);
        r1.addBreak();
        XWPFRun r2 = paragraph.createRun();
        r2.setText(party + " v\u00e0 ");
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(12);
        r2.setBold(true);
        XWPFRun r3 = paragraph.createRun();
        r3.setText(company);
        r3.setFontFamily("Times New Roman");
        r3.setFontSize(12);
        r3.setBold(true);
    }

    private static int indexOfIgnoreCase(String text, String needle) {
        return WordExportService.indexOfIgnoreCase(text, needle, 0);
    }

    private static int indexOfIgnoreCase(String text, String needle, int from) {
        if (text == null || needle == null) {
            return -1;
        }
        return text.toLowerCase(Locale.ROOT).indexOf(needle.toLowerCase(Locale.ROOT), from);
    }

    private void applyValueHangingIndent(XWPFParagraph paragraph, String text) {
        int valueStart;
        boolean account;
        if (text == null || text.isBlank()) {
            return;
        }
        String trimmed = text.stripLeading();
        String lower = trimmed.toLowerCase(Locale.ROOT);
        boolean partyA = lower.startsWith("b\u00ean a") || lower.startsWith("ben a") || lower.startsWith("i. \u0111\u1ea1i di\u1ec7n b\u00ean a") || lower.startsWith("i. dai dien ben a");
        boolean bl = account = lower.startsWith("t\u00e0i kho\u1ea3n") || lower.startsWith("tai khoan");
        if (!partyA && !account) {
            return;
        }
        int colon = trimmed.indexOf(58);
        if (colon < 0) {
            return;
        }
        for (valueStart = colon + 1; valueStart < trimmed.length() && Character.isWhitespace(trimmed.charAt(valueStart)); ++valueStart) {
        }
        int charWidth = partyA ? 130 : 115;
        int twips = Math.max(partyA ? 2200 : 1800, valueStart * charWidth);
        paragraph.setIndentationFirstLine(0);
        paragraph.setIndentationLeft(twips);
        paragraph.setIndentationHanging(twips);
    }

    private void writeTextWithBreaks(XWPFRun run, String text, int fontSize, boolean bold) {
        String[] lines;
        run.setFontFamily("Times New Roman");
        run.setFontSize(fontSize);
        run.setBold(bold);
        if (text == null) {
            text = "";
        }
        if ((lines = text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)).length == 0) {
            run.setText("", 0);
            return;
        }
        run.setText(lines[0] == null ? "" : lines[0], 0);
        for (int i = 1; i < lines.length; ++i) {
            run.addBreak();
            run.setText(lines[i] == null ? "" : lines[i], i);
        }
    }

    private static String oneLine(String s) {
        if (s == null) {
            return "";
        }
        return s.replace('\r', ' ').replace('\n', ' ').replaceAll("\\s+", " ").trim();
    }

    private boolean insertOrReplaceTable(XWPFDocument doc, List<LineItem> rows, String totalNumber) {
        return this.insertOrReplaceTable(doc, rows, totalNumber, false);
    }

    private boolean insertOrReplaceTable(XWPFDocument doc, List<LineItem> rows, String totalNumber, boolean omitPrices) {
        int i;
        XWPFParagraph placeholderPara = null;
        for (XWPFParagraph p : doc.getParagraphs()) {
            if (p.getText() == null || !p.getText().contains("{{TABLE_PLACEHOLDER}}")) continue;
            placeholderPara = p;
            break;
        }
        if (placeholderPara == null) {
            return false;
        }
        while (!placeholderPara.getRuns().isEmpty()) {
            placeholderPara.removeRun(0);
        }
        XWPFTable table = doc.insertNewTbl(placeholderPara.getCTP().newCursor());
        int pos = doc.getPosOfParagraph(placeholderPara);
        if (pos >= 0) {
            doc.removeBodyElement(pos);
        }
        String[] headers = omitPrices
                ? new String[]{"STT", "N\u1ed9i dung c\u00f4ng vi\u1ec7c", "\u0110\u01a1n v\u1ecb t\u00ednh", "S\u1ed1 l\u01b0\u1ee3ng"}
                : new String[]{"STT", "N\u1ed9i dung c\u00f4ng vi\u1ec7c", "\u0110\u01a1n v\u1ecb t\u00ednh", "S\u1ed1 l\u01b0\u1ee3ng", "\u0110\u01a1n gi\u00e1", "Th\u00e0nh ti\u1ec1n"};
        int colCount = headers.length;
        this.ensureColumns(table, colCount);
        XWPFTableRow header = table.getRow(0);
        for (i = 0; i < headers.length; ++i) {
            this.setCellText(header.getCell(i), headers[i], true);
            this.addCellBorder(header.getCell(i));
        }
        for (i = 0; i < rows.size(); ++i) {
            LineItem item = rows.get(i);
            XWPFTableRow row = table.createRow();
            this.ensureRowColumns(row, colCount);
            this.setCellText(row.getCell(0), String.valueOf(i + 1), true);
            this.setCellText(row.getCell(1), item.content(), false);
            this.setCellText(row.getCell(2), item.unit(), true);
            this.setCellText(row.getCell(3), item.qty(), true);
            if (!omitPrices) {
                this.setCellText(row.getCell(4), item.price(), true);
                this.setCellText(row.getCell(5), item.total(), true);
            }
            for (int c = 0; c < colCount; ++c) {
                this.addCellBorder(row.getCell(c));
            }
        }
        if (!omitPrices) {
            XWPFTableRow totalRow = table.createRow();
            this.ensureRowColumns(totalRow, colCount);
            this.mergeCellsHorizontally(totalRow, 0, 4);
            this.setCellText(totalRow.getCell(0), "T\u1ed5ng c\u1ed9ng", false);
            this.setCellText(totalRow.getCell(5), totalNumber, true);
            for (int c = 0; c < colCount; ++c) {
                this.addCellBorder(totalRow.getCell(c));
            }
        }
        int[] widths = omitPrices
                ? new int[]{800, 5500, 1500, 1500}
                : new int[]{800, 4500, 1200, 1200, 1500, 1500};
        for (XWPFTableRow row : table.getRows()) {
            for (int i2 = 0; i2 < Math.min(widths.length, row.getTableCells().size()); ++i2) {
                this.setCellWidth(row.getCell(i2), widths[i2]);
            }
        }
        return true;
    }

    /** Điền lại bảng báo giá cứng (99-1/99-2) bằng đúng các dòng đã chọn trên tool. */
    private void fillHardcodedQuoteTable(XWPFDocument doc, List<LineItem> rows, String totalNumber) {
        if (doc.getTables().isEmpty() || rows == null || rows.isEmpty()) {
            return;
        }
        XWPFTable table = doc.getTables().get(0);
        if (table.getNumberOfRows() == 0) {
            return;
        }
        XWPFTableRow header = table.getRow(0);
        int colCount = Math.max(6, header.getTableCells().size());
        int colContent = 1;
        int colBrand = 2;
        int colUnit = 3;
        int colQty = 4;
        int colPrice = 5;
        int colAmount = -1;
        for (int c = 0; c < header.getTableCells().size(); ++c) {
            String h = header.getCell(c).getText().trim().toLowerCase(Locale.ROOT);
            if (h.contains("h\u00e0ng h\u00f3a") || h.contains("n\u1ed9i dung") || h.contains("hang hoa")) {
                colContent = c;
            } else if (h.contains("th\u01b0\u01a1ng hi\u1ec7u") || h.contains("xu\u1ea5t x\u1ee9") || h.contains("thuong hieu")) {
                colBrand = c;
            } else if (h.contains("\u0111\u01a1n v\u1ecb") || h.contains("don vi")) {
                colUnit = c;
            } else if (h.contains("s\u1ed1 l\u01b0\u1ee3ng") || h.equals("sl") || h.contains("so luong")) {
                colQty = c;
            } else if (h.contains("\u0111\u01a1n gi\u00e1") || h.contains("don gia")) {
                colPrice = c;
            } else if (h.contains("th\u00e0nh ti\u1ec1n") || h.contains("thanh tien")) {
                colAmount = c;
            }
        }
        for (int i = table.getNumberOfRows() - 1; i >= 1; --i) {
            table.removeRow(i);
        }
        for (int i = 0; i < rows.size(); ++i) {
            LineItem item = rows.get(i);
            XWPFTableRow row = table.createRow();
            this.ensureRowColumns(row, colCount);
            this.setCellText(row.getCell(0), String.valueOf(i + 1), true);
            if (colContent >= 0 && colContent < row.getTableCells().size()) {
                this.setCellText(row.getCell(colContent), item.content(), false);
            }
            if (colBrand >= 0 && colBrand < row.getTableCells().size()) {
                this.setCellText(row.getCell(colBrand), item.brand() == null ? "" : item.brand(), true);
            }
            if (colUnit >= 0 && colUnit < row.getTableCells().size()) {
                this.setCellText(row.getCell(colUnit), item.unit(), true);
            }
            if (colQty >= 0 && colQty < row.getTableCells().size()) {
                this.setCellText(row.getCell(colQty), item.qty(), true);
            }
            if (colPrice >= 0 && colPrice < row.getTableCells().size()) {
                this.setCellText(row.getCell(colPrice), item.price(), true);
            }
            if (colAmount >= 0 && colAmount < row.getTableCells().size()) {
                this.setCellText(row.getCell(colAmount), item.total(), true);
            }
            for (int c = 0; c < row.getTableCells().size(); ++c) {
                this.addCellBorder(row.getCell(c));
            }
        }
        // Dòng tổng (nếu mẫu có cột thành tiền)
        if (colAmount >= 0) {
            XWPFTableRow totalRow = table.createRow();
            this.ensureRowColumns(totalRow, colCount);
            this.setCellText(totalRow.getCell(0), "T\u1ed5ng c\u1ed9ng", false);
            if (colContent >= 0 && colContent < totalRow.getTableCells().size()) {
                this.setCellText(totalRow.getCell(colContent), "", false);
            }
            this.setCellText(totalRow.getCell(colAmount), totalNumber, true);
            for (int c = 0; c < totalRow.getTableCells().size(); ++c) {
                this.addCellBorder(totalRow.getCell(c));
            }
        }
    }

    private void mergeCellsHorizontally(XWPFTableRow row, int from, int to) {
        for (int i = from; i <= to; ++i) {
            CTTcPr tcPr;
            CTTcPr cTTcPr = tcPr = row.getCell(i).getCTTc().isSetTcPr() ? row.getCell(i).getCTTc().getTcPr() : row.getCell(i).getCTTc().addNewTcPr();
            if (tcPr.isSetHMerge()) {
                tcPr.unsetHMerge();
            }
            if (i == from) {
                tcPr.addNewHMerge().setVal(STMerge.RESTART);
                continue;
            }
            tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    private boolean matchesAny(String fileName, Set<ExportDoc> types) {
        for (ExportDoc type : types) {
            if (!type.matchesFileName(fileName)) continue;
            return true;
        }
        return false;
    }

    public List<ExportDoc> findUnmatchedTypes(Path inputFolder, Set<ExportDoc> selectedTypes) throws IOException {
        EnumSet<ExportDoc> matched = EnumSet.noneOf(ExportDoc.class);
        if (inputFolder == null || selectedTypes == null || selectedTypes.isEmpty()) {
            return List.of();
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputFolder, "*.docx");){
            for (Path input : stream) {
                String name = input.getFileName().toString();
                if (name.startsWith("~$") || ExportDoc.isGuideFile(name)) continue;
                for (ExportDoc type : selectedTypes) {
                    if (!type.matchesFileName(name)) continue;
                    matched.add(type);
                }
            }
        }
        ArrayList<ExportDoc> missing = new ArrayList<ExportDoc>();
        for (ExportDoc type : selectedTypes) {
            if (matched.contains((Object)type)) continue;
            missing.add(type);
        }
        return missing;
    }

    private void ensureColumns(XWPFTable table, int cols) {
        XWPFTableRow first = table.getRow(0);
        if (first == null) {
            first = table.createRow();
        }
        this.ensureRowColumns(first, cols);
    }

    private void ensureRowColumns(XWPFTableRow row, int cols) {
        while (row.getTableCells().size() < cols) {
            row.addNewTableCell();
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean center) {
        if (cell == null) {
            return;
        }
        for (int i = cell.getParagraphs().size() - 1; i >= 0; --i) {
            cell.removeParagraph(i);
        }
        String raw = text == null ? "" : text.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = raw.split("\n", -1);
        if (lines.length == 0) {
            lines = new String[]{""};
        }
        boolean boldAll = "T\u1ed5ng c\u1ed9ng".equals(raw.trim()) || center;
        for (int i = 0; i < lines.length; ++i) {
            XWPFParagraph p = cell.addParagraph();
            p.setAlignment(center ? ParagraphAlignment.CENTER : ParagraphAlignment.LEFT);
            p.setSpacingBefore(0);
            p.setSpacingAfter(0);
            XWPFRun run = p.createRun();
            run.setText(lines[i] == null ? "" : lines[i]);
            run.setFontFamily("Times New Roman");
            run.setFontSize(11);
            run.setBold(boldAll);
        }
    }

    private void addCellBorder(XWPFTableCell cell) {
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();
        this.setBorder(borders.isSetTop() ? borders.getTop() : borders.addNewTop());
        this.setBorder(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom());
        this.setBorder(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft());
        this.setBorder(borders.isSetRight() ? borders.getRight() : borders.addNewRight());
    }

    private void setBorder(CTBorder border) {
        border.setVal(STBorder.SINGLE);
        border.setSz(BigInteger.valueOf(4L));
        border.setColor((Object)"000000");
    }

    private void setCellWidth(XWPFTableCell cell, int width) {
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTblWidth tblWidth = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW((Object)BigInteger.valueOf(width));
    }

    /**
     * Ẩn dòng trống của cặp: Tài khoản số / Đại diện / Chức vụ.
     * - Có dữ liệu: giữ. Không có: ẩn.
     * - Cả 2 dòng cùng loại đều trống: giữ lại 1 dòng.
     */
    private void hideEmptyPairedInfoRows(XWPFDocument doc) {
        for (XWPFTable table : doc.getTables()) {
            this.hideEmptyPairedInfoRowsInTable(table);
        }
    }

    private void hideEmptyPairedInfoRowsInTable(XWPFTable table) {
        if (table == null || table.getNumberOfRows() == 0) {
            return;
        }
        String[] kinds = new String[]{"taikhoan", "daidien", "chucvu"};
        for (String kind : kinds) {
            ArrayList<Integer> idxs = new ArrayList<Integer>();
            for (int i = 0; i < table.getNumberOfRows(); ++i) {
                if (WordExportService.pairKindOf(table.getRow(i)) == null) {
                    continue;
                }
                if (!kind.equals(WordExportService.pairKindOf(table.getRow(i)))) {
                    continue;
                }
                idxs.add(i);
            }
            if (idxs.size() < 2) {
                // 1 dòng: trống thì vẫn giữ (không có cặp để ẩn)
                continue;
            }
            ArrayList<Integer> emptyIdxs = new ArrayList<Integer>();
            ArrayList<Integer> filledIdxs = new ArrayList<Integer>();
            for (Integer idx : idxs) {
                if (WordExportService.isPairRowValueEmpty(table.getRow(idx))) {
                    emptyIdxs.add(idx);
                } else {
                    filledIdxs.add(idx);
                }
            }
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            if (filledIdxs.isEmpty()) {
                // cả 2 (hoặc nhiều) trống → giữ 1 dòng đầu, xóa phần còn lại
                for (int k = 1; k < idxs.size(); ++k) {
                    toRemove.add(idxs.get(k));
                }
            } else {
                // có dữ liệu → ẩn hết dòng trống
                toRemove.addAll(emptyIdxs);
            }
            toRemove.sort((a, b) -> Integer.compare(b, a));
            for (Integer ri : toRemove) {
                if (ri != null && ri >= 0 && ri < table.getNumberOfRows()) {
                    table.removeRow(ri);
                }
            }
        }
    }

    /** null nếu không phải 3 loại cặp cần xử lý. */
    private static String pairKindOf(XWPFTableRow row) {
        if (row == null || row.getTableCells().isEmpty()) {
            return null;
        }
        String label = WordExportService.normalizeAscii(row.getCell(0).getText());
        if (label.isEmpty()) {
            return null;
        }
        // Bỏ qua dòng kiểu "I. Đại diện Bên A..."
        if (label.contains("bena") || label.contains("benb") || label.contains("benmua") || label.contains("benban")
                || label.contains("bennhan") || label.contains("bengiao")) {
            return null;
        }
        if (label.contains("taikhoan")) {
            return "taikhoan";
        }
        if (label.contains("daidien") && !label.contains("doong")) {
            // chỉ label ngắn "Đại diện"
            if (label.length() <= 20 || label.startsWith("daidien")) {
                return "daidien";
            }
        }
        if (label.contains("chucvu") && label.length() <= 20) {
            return "chucvu";
        }
        return null;
    }

    private static boolean isPairRowValueEmpty(XWPFTableRow row) {
        StringBuilder sb = new StringBuilder();
        for (XWPFTableCell cell : row.getTableCells()) {
            if (cell == null) {
                continue;
            }
            String t = cell.getText();
            if (t != null) {
                sb.append(t).append(' ');
            }
        }
        String full = sb.toString().replace('\u00a0', ' ').replace('\r', ' ').replace('\n', ' ');
        int colon = full.lastIndexOf(':');
        String value = colon >= 0 ? full.substring(colon + 1) : full;
        value = value.replace('|', ' ').trim();
        // còn sót placeholder thì coi như trống
        if (value.contains("{{") && value.contains("}}")) {
            return true;
        }
        return value.isBlank();
    }

    private static String normalizeAscii(String input) {
        if (input == null) {
            return "";
        }
        String s = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replace('\u0111', 'd')
                .replace('\u00a0', ' ');
        return s.replaceAll("[^a-z0-9]", "");
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public record LineItem(String content, String unit, String qty, String price, String total, String brand) {
        public LineItem(String content, String unit, String qty, String price, String total) {
            this(content, unit, qty, price, total, "");
        }
    }
}

