import com.contracttool.model.ContractInfo;
import com.contracttool.model.ExportDoc;
import com.contracttool.service.WordExportService;
import org.apache.poi.xwpf.usermodel.*;
import java.nio.file.*;
import java.util.*;

public class SmokePairHide {
  public static void main(String[] a) throws Exception {
    Path in = Paths.get(a[0]); Path out = Paths.get(a[1]);
    Files.createDirectories(out);
    WordExportService svc = new WordExportService();
    List<WordExportService.LineItem> rows = List.of(
      new WordExportService.LineItem("SP", "Test", "1", "1000", "1000", "B"));
    ContractInfo info = new ContractInfo();
    info.setPartyA("TRAM Y TE SON LUONG");
    info.setAddress("LAO CAI");
    info.setTaxCode("5200964219");
    info.setPhone("");
    info.setAccount1(""); info.setAccount2("");
    info.setRepresentative1("ong DANG TON PIEN"); info.setRepresentative2("");
    info.setTitle1("GIAM DOC"); info.setTitle2("");
    info.setBudgetCode(""); info.setContractNo("01"); info.setYear("2026");
    svc.export(in, out, rows, info, EnumSet.of(ExportDoc.HOP_DONG));
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(out, "*.docx")) {
      for (Path f : ds) {
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(f))) {
          XWPFTable t = doc.getTables().get(0);
          System.out.println("FILE "+f.getFileName()+" rows="+t.getNumberOfRows());
          for (int r=0;r<t.getNumberOfRows();r++) {
            StringBuilder sb=new StringBuilder();
            for (XWPFTableCell c: t.getRow(r).getTableCells()) sb.append(c.getText().replace("\n"," ").trim()).append(" | ");
            System.out.println(" r"+r+": "+sb);
          }
        }
      }
    }
  }
}