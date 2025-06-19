package com.worldline.mts.idm.scimctl.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * used when --verbose and / or --dry-run enabled
 * log level info for info
 * log level fine for dryRun
 * log level finer for debug
 */
@ApplicationScoped
public class OutputUtils {

  private static final Logger logger = Logger.getLogger("scim-ctl");

  private boolean verbose = false;

  private boolean dryRun = false;

  private boolean debug = false;

  public void configLoggerLevel() {
    if (verbose) {
      setLogLevel("scim-ctl", Level.INFO.getName());
    } else if (dryRun) {
      setLogLevel("scim-ctl", Level.FINE.getName());
    } else if (debug) {
      setLogLevel("scim-ctl", Level.FINER.getName());
    }
  }

  // use the same logger for the app
  // check if lvl correspond to actual logger.level if yes print the message
  public void logMsg(String msg) {
    switch (getCurrentLogLevel(logger).getName()) {
      case "INFO":
        logger.info(msg);
        break;
      case "FINE":
        logger.fine(msg);
        break;
      case "FINER":
        logger.finer(msg);
        break;
    }
  }

  public void toggleVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public void toggleDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }

  public boolean getDryRun() {
    return this.dryRun;
  }

  public void toggleDebug(boolean debug) {
    this.debug = debug;
  }

  private static Level getCurrentLogLevel(java.util.logging.Logger logger) {
    for (java.util.logging.Logger current = logger; current != null;) {
      Level level = current.getLevel();
      if (level != null)
        return level;
      current = current.getParent();
    }
    return Level.INFO;
  }

  private static Level setLogLevel(String loggerName, String lvl) {
    // get the logger instance
    Logger logger = Logger.getLogger(loggerName);

    // change the log-level if requested
    if (lvl != null && lvl.length() > 0)
      logger.setLevel(Level.parse(lvl));

    // return the current log-level
    return getCurrentLogLevel(logger);
  }

}
