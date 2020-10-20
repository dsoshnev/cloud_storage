package server;

//import org.apache.log4j.Logger;

// Import log4j2 classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class LogService {
    private static final Logger logger = LogManager.getLogger(LogService.class.getName());

    public static void error(String message) { logger.error(message); }

    public static void error(String format, Object ... args) {
        logger.error(String.format(format, args));
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String format, Object ... args) {
        logger.info(String.format(format, args));
    }
}

