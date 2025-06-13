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

  private static final Logger LOGGER = Logger.getLogger(OutputUtils.class);

  public void logMsg(Logger logger, Logger.Level lvl, String msg) {
    if (verbose || dryRun) {
      try {
        logger.log(lvl, msg);
      } catch (IllegalArgumentException e) {
        LOGGER.info("lvl isn't in Enum Logger.Level jbossloging " + e.getMessage());
      } catch (NullPointerException e) {
        LOGGER.info("lvl can't be null" + e.getMessage());
      }
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
}
