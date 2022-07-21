package ic.unicamp.bm.scanner;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;

public class ProductSequenceNumber {

  private static AtomicLong sequenceNumber = new AtomicLong(0);

  public static String getNextStringCode() {
    return transformToString(generateNextCode());
  }

  private static String transformToString(long generateNextCode) {
    return StringUtils.leftPad(String.valueOf(generateNextCode), 6, "0");
  }

  private static long generateNextCode() {
    long code = sequenceNumber.getAndIncrement();
    if (code == 1000000L) {
      sequenceNumber = new AtomicLong(0);
      code = sequenceNumber.getAndIncrement();
    }
    return code;
  }
}
