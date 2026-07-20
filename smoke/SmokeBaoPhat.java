import com.contracttool.model.ContractInfo;
import com.contracttool.model.ExportDoc;
import com.contracttool.service.WordExportService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import java.nio.file.*;
import java.util.*;

public class SmokeBaoPhat {
  public static void main(String[] args) throws Exception {
    Path in = Paths.get(args[0]);
    Path out = Paths.get(args[1]);
    if (Files.exists(out)) {
      try (DirectoryStream<Path> ds = Files.newDirectoryStream(out)) {
        for (Path p : ds) Files.deleteIfExists(p);
      }
    } else Files.createDirectories(out);
    WordExportService svc = new WordExportService();
    List<WordExportService.LineItem> rows = List.of(
      new WordExportService.LineItem("SP A", "Test", "2", "1000", "2000", "B1"),
      new WordExportService.LineItem("SP B", "Hop", "3", "2000", "6000", "B2")
    );
    ContractInfo info = new ContractInfo();
    info.setPartyA("Cong ty Test");
    info.setTaxCode("0123456789");
    info.setContractNo("HD-1");
    info.setAddress("HN");
    info.setPhone("09");
    info.setYear("2026");
    int n = svc.export(in, out, rows, info, EnumSet.of(ExportDoc.BAO_PHAT, ExportDoc.BBGN));
    System.out.println("exported=" + n);
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(out, "*.docx")) {
      for (Path f : ds) {
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(f))) {
          System.out.println("FILE " + f.getFileName());
          int ti = 0;
          for (XWPFTable t : doc.getTables()) {
            String h0 = t.getRow(0).getCell(0).getText().trim();
            String headers = "";
            for (int c = 0; c < t.getRow(0).getTableCells().size(); c++) {
              if (c > 0) headers += " | ";
              headers += t.getRow(0).getCell(c).getText().trim();
            }
            boolean product = headers.toLowerCase().contains("stt") || headers.toLowerCase().contains("nội dung") || headers.toLowerCase().contains("noi dung") || headers.toLowerCase().contains("số lượng") || headers.toLowerCase().contains("so luong");
            if (!product && !headers.toLowerCase().contains("đơn") && !headers.toLowerCase().contains("don")) {
              // also detect by col count / content
              product = headers.toLowerCase().contains("hang") || h0.equalsIgnoreCase("STT");
            }
            if (product || h0.equalsIgnoreCase("STT")) {
              System.out.println("  productTable#" + ti + " rows=" + t.getNumberOfRows() + " headers=[" + headers + "]");
              for (int r = 0; r < t.getNumberOfRows(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < t.getRow(r).getTableCells().size(); c++) {
                  if (c > 0) sb.append(" || ");
                  sb.append(t.getRow(r).getCell(c).getText().replace("\n"," ").trim());
                }
                System.out.println("    r" + r + ": " + sb);
              }
            }
            ti++;
          }
        }
      }
    }
  }
}