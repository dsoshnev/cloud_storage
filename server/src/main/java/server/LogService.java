package server;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class LogService {
    private static final Logger logger = Logger.getLogger(LogService.class.getName());
    private static final String LOG_FILE_NAME = "log.txt";
    static {
        FileHandler fh = null;
        try {
            fh = new FileHandler(LOG_FILE_NAME);
            fh.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    Date date = new Date();
                    date.setTime(record.getMillis());
                    return String.format("%s: %s%n", date, record.getMessage());
                }
            });
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void error(String message) {
        logger.severe(message);
    }

    public static void error(String format, Object ... args) {
        logger.severe(String.format(format, args));
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String format, Object ... args) {
        logger.info(String.format(format, args));
    }
}

