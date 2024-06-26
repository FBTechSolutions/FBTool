package ic.unicamp.fb.cli.util.logger;

import org.slf4j.LoggerFactory;

import static ic.unicamp.fb.cli.util.logger.LoggerTag.TAG_LINE_BREAK;
import static ic.unicamp.fb.cli.util.logger.LoggerTag.TAG_XGIT_ERROR;
import static ic.unicamp.fb.cli.util.logger.LoggerTag.TAG_XGIT_INFO;
import static ic.unicamp.fb.cli.util.logger.LoggerTag.TAG_XGIT_WARN;


public class SplMgrLogger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SplMgrLogger.class);

    public static void info(String message, boolean tag) {
        if (tag) {
            message = TAG_XGIT_INFO + message;
        }
        message = message + TAG_LINE_BREAK;
        logger.trace(message);
        logger.info(message);
    }

    public static void warn(String message, boolean tag) {
        if (tag) {
            message = TAG_XGIT_WARN + message;
        }
        message = message + TAG_LINE_BREAK;
        logger.trace(message);
        logger.warn(message);
    }

    public static void error(String message, boolean tag) {
        if (tag) {
            message = TAG_XGIT_ERROR + message;
        }
        message = message + TAG_LINE_BREAK;
        logger.trace(message);
        logger.error(message);
    }

    public static void message(String message, boolean tag) {
        if (tag) {
            message = TAG_XGIT_INFO + message;
        }
        logger.trace(message);
    }

    public static void message_ln(String message, boolean tag) {
        if (tag) {
            message = TAG_XGIT_INFO + message;
        }
        message = message + TAG_LINE_BREAK;
        logger.trace(message);
    }
}
