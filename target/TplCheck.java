import com.contracttool.model.ContractInfo;
import com.contracttool.model.ExportDoc;
import com.contracttool.service.WordExportService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class TplCheck {
    public static void main(String[] args) throws Exception {
        Path in = Path.of(args[0]);
        Path out = Path.of(args[1]);
        boolean three = Boolean.parseBoolean(args[2]);
        Files.createDirectories(out);
        WordExportService svc = new WordExportService();
        Set<ExportDoc> types = EnumSet.allOf(ExportDoc.class);
        List<ExportDoc> missing = svc.findUnmatchedTypes(in, types);
        System.out.println("UNMATCHED:");
        for (ExportDoc d : missing) {
            System.out.println("  " + d.getLabel());
        }
        ContractInfo info = new ContractInfo();
        info.setContractNo("HD-01");
        info.setPartyA("BEN MUA ABC");
        info.setAddress("DIA CHI A");
        info.setPhone("0900000001");
        info.setAccount1("TK A1");
        info.setAccount2("TK A2");
        info.setTaxCode("1111111111");
        info.setBudgetCode("QHNS-A");
        info.setRepresentative1("ONG A MOT");
        info.setRepresentative2("BA A HAI");
        info.setTitle1("GIAM DOC A1");
        info.setTitle2("PHO GIAM DOC A2");
        info.setYear("2026");
        info.setThreeParty(three);
        if (three) {
            info.setPartyC("BEN NHAN C");
            info.setAddressC("DIA CHI C");
            info.setPhoneC("0900000002");
            info.setAccountC("TK C1");
            info.setAccountC2("TK C2");
            info.setTaxCodeC("2222222222");
            info.setBudgetCodeC("QHNS-C");
            info.setRepresentativeC("ONG C MOT");
            info.setRepresentativeC2("BA C HAI");
            info.setTitleC("CHUC VU C1");
            info.setTitleC2("CHUC VU C2");
        }
        List<WordExportService.LineItem> rows = List.of(
                new WordExportService.LineItem("Hang y te", "CAI", "2", "1.000", "2.000", "VIET NAM", false),
                new WordExportService.LineItem("Que thu ma tuy", "TEST", "3", "5.000", "15.000", "FASTEP", true)
        );
        int n = svc.export(in, out, rows, info, types);
        System.out.println("EXPORTED_FILES " + n + " three=" + three);
        Pattern ph = Pattern.compile("\\{\\{[A-Za-z0-9_]+\\}\\}");
        try (var stream = Files.list(out)) {
            stream.filter(p -> p.getFileName().toString().endsWith(".docx")).forEach(p -> {
                StringBuilder sb = new StringBuilder();
                try (ZipFile zip = new ZipFile(p.toFile())) {
                    var en = zip.entries();
                    while (en.hasMoreElements()) {
                        var e = en.nextElement();
                        if (!e.getName().endsWith(".xml")) continue;
                        sb.append(new String(zip.getInputStream(e).readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
                    }
                } catch (Exception ex) {
                    System.out.println("READ_FAIL " + p.getFileName() + " " + ex.getMessage());
                    return;
                }
                String text = sb.toString().replaceAll("<[^>]+>", "");
                Matcher m = ph.matcher(text);
                System.out.print("OUT " + p.getFileName());
                boolean any = false;
                while (m.find()) {
                    if (!any) System.out.println();
                    any = true;
                    System.out.println("  LEFT " + m.group());
                }
                if (!any) System.out.println("  CLEAN");
                if (text.contains("BÊN C") || text.contains("Bên Nhận") || text.contains("{{BenC}}")) {
                    System.out.println("  SHOWS_BEN_C");
                }
                for (String needle : new String[]{"BEN MUA ABC", "BEN NHAN C", "DIA CHI C", "TK C1", "ONG C MOT", "CHUC VU C2", "QHNS-C", "Hang y te", "Que thu ma tuy"}) {
                    if (text.contains(needle)) System.out.println("  HAS " + needle);
                }
            });
        }
    }
}
