import com.contracttool.util.NumberUtil;
public class Fmt {
  public static void main(String[] args) {
    String a = NumberUtil.formatDot(10000);
    String b = NumberUtil.formatDot(1000000);
    String c = NumberUtil.dotsNotCommas("Tong 10,000 va 1,000,000 dong, dieu 1, 2");
    if (a.indexOf(',') >= 0 || b.indexOf(',') >= 0 || c.indexOf(',') >= 0 && c.contains(",000")) {
      System.out.println("FAIL " + a + " | " + b + " | " + c);
      System.exit(1);
    }
    System.out.println("OK " + a + " | " + b + " | " + c);
  }
}
