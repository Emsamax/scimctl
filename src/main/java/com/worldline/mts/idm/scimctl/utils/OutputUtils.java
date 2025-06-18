package com.worldline.mts.idm.scimctl.utils;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * used when --verbose and / or --dry-run enabled
 */
@ApplicationScoped
public class OutputUtils {

  private boolean verbose = false;

  private boolean dryRun = false;

  private boolean debug = false;

  public void logMsg(Logger logger, Logger.Level lvl, String msg) {
    if (verbose) {
      logger.log(Logger.Level.INFO, msg);
    } else if (dryRun ) {
      logger.log(Logger.Level.INFO, msg);
    } else if (debug) {
      logger.log(Logger.Level.DEBUG, msg);
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
}
