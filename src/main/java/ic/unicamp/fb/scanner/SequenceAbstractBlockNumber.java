package ic.unicamp.fb.scanner;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceAbstractBlockNumber {

    private static AtomicLong sequenceNumber = new AtomicLong(0);

    public static String getNextStringCode() {
        return transformToString(generateNextCode());
    }

    private static String transformToString(long generateNextCode) {
        return StringUtils.leftPad(String.valueOf(generateNextCode), 16, "0");
    }

    private static long generateNextCode() {
        long code = sequenceNumber.getAndIncrement();
        if (code == 10000000000000000L) {
            sequenceNumber = new AtomicLong(0);
            code = sequenceNumber.getAndIncrement();
        }
        return code;
    }
}
