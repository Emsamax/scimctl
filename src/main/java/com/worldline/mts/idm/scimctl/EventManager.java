package com.worldline.mts.idm.scimctl;

import static org.jboss.logging.Logger.getLogger;

import org.jboss.logging.Logger;

public class EventManager {

  private static final Logger LOGGER = getLogger(EventManager.class);

  public void logEvent(String msg) {
    LOGGER.info(msg);
  }
}
