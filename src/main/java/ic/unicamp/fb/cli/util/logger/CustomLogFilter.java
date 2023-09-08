package ic.unicamp.fb.cli.util.logger;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class CustomLogFilter extends Filter {
    @Override
    public int decide(LoggingEvent event) {
        if (event.getMessage().toString().startsWith("instrumented a special java.util.Set into:")) {
            return Filter.DENY;
        }
        return Filter.NEUTRAL;
    }
}