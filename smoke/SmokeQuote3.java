import com.contracttool.model.ContractInfo;
import com.contracttool.model.ExportDoc;
import com.contracttool.service.WordExportService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class SmokeQuote3 {
    public static void main(String[] args) throws Exception {
        Path in = Paths.get(args[0]);
        Path out = Paths.get(args[1]);
        Files.createDirectories(out);
        WordExportService svc = new WordExportService();
        List<WordExportService.LineItem> rows = List.of(
                new WordExportService.LineItem("SP A\nline2", "Test", "2", "1000", "2000", "BRAND-A"),
                new WordExportService.LineItem("SP B", "Hop", "3", "2000", "6000", "BRAND-B"),
                new WordExportService.LineItem("SP C", "Cai", "1", "5000", "5000", "BRAND-C")
        );
        ContractInfo info = new ContractInfo();
        info.setPartyA("Cong ty Test");
        info.setTaxCode("0123456789");
        info.setContractNo("HD-TEST");
        info.setAddress("Ha Noi");
        info.setPhone("0900000000");
        info.setYear("2026");
        Set<ExportDoc> types = EnumSet.of(
                ExportDoc.BAO_GIA_1,
                ExportDoc.BAO_GIA_2,
                ExportDoc.BAO_GIA_CUNG,
                ExportDoc.HOP_DONG
        );
        int n = svc.export(in, out, rows, info, types);
        try {
            svc.processCungUngFiles(out);
        } catch (Exception e) {
            System.out.println("cung err " + e);
        }
        System.out.println("exported=" + n);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(out, "*.docx")) {
            for (Path f : ds) {
                try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(f))) {
                    int tables = doc.getTables().size();
                    int dataRows = 0;
                    String firstCells = "";
                    if (tables > 0) {
                        XWPFTable t = doc.getTables().get(0);
                        dataRows = Math.max(0, t.getNumberOfRows() - 1);
                        StringBuilder sb = new StringBuilder();
                        for (int r = 0; r < t.getNumberOfRows(); r++) {
                            String c0 = t.getRow(r).getCell(0) == null
                                    ? ""
                                    : t.getRow(r).getCell(0).getText().trim();
                            String c1 = "";
                            if (t.getRow(r).getTableCells().size() > 1 && t.getRow(r).getCell(1) != null) {
                                c1 = t.getRow(r).getCell(1).getText().replace("\n", " ").trim();
                                if (c1.length() > 40) {
                                    c1 = c1.substring(0, 40);
                                }
                            }
                            sb.append("[").append(r).append(":").append(c0).append("|").append(c1).append("] ");
                        }
                        firstCells = sb.toString();
                    }
                    System.out.println(f.getFileName() + " tables=" + tables
                            + " dataRowsApprox=" + dataRows + " " + firstCells);
                }
            }
        }
    }
}
