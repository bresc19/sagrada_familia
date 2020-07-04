package it.polimi.ingsw.server.log;

import it.polimi.ingsw.server.set.up.TakeDataFile;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.*;

import static it.polimi.ingsw.server.costants.NameConstants.LEVEL_LOG_VISIBLE;
import static it.polimi.ingsw.server.costants.NameConstants.PATH_LOG_FILE;

public class Log {
    private static Log log;
    private final Logger logger;
    private final ConsoleHandler consoleHandler;
    private Log() {
        logger = Logger.getLogger(Log.class.toString());
        logger.setUseParentHandlers(false);
        TakeDataFile config = new TakeDataFile();
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(Paths.get(config.getParameter(PATH_LOG_FILE)).toString());
        } catch (Exception e) {
            e.getMessage();
        }
        consoleHandler = new ConsoleHandler();
        logger.addHandler(Objects.requireNonNull(fileHandler));
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
        this.setVisibleLog(config.getParameter(LEVEL_LOG_VISIBLE));
        Formatter f = new SimpleFormatter();
        fileHandler.setFormatter(f);
    }

    /**
     * get logger
     * @return logger
     */
    public static Log getLogger(){
        if(log == null)
            log = new Log();
        return log;
    }

    /**
     * show log on console
     * @param level is the level of log
     */
    public void setVisibleLog(String level)
    { Level level_log;
        switch(level)
        {
            case "ERROR": level_log = Level.SEVERE; break;
            case "INFO": level_log = Level.INFO; break;
            case "ALL": level_log = Level.ALL; break;
            default: level_log= Level.OFF;
        }
        consoleHandler.setLevel(level_log);
    }

    /**
     * add a log in log file
     * @param message is message to log
     * @param levelLogin is level of message to log
     * @param sourceClass is source class that invoke thid method
     * @param sourceMethod is source method that invoke this method
     */
    public void addLog(String message,Level levelLogin,String sourceClass,String sourceMethod)
    {
        logger.logp(levelLogin,sourceClass,sourceMethod,message);
    }

}
